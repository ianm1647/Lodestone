package team.lodestar.lodestone.systems.worldevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.*;
import team.lodestar.lodestone.network.worldevent.SyncWorldEventPayload;
import team.lodestar.lodestone.registry.common.LodestoneNetworkPayloads;
import team.lodestar.lodestone.registry.common.LodestoneWorldEventTypes;

import java.util.UUID;

/**
 * World events are tickable instanced objects which are saved in a level capability, which means they are unique per dimension.
 * They can exist on the client and are ticked separately.
 * @see <a href="https://github.com/LodestarMC/Lodestone/wiki/World-Events">Lodestone World Events Wiki</a>
 */
public abstract class WorldEventInstance {
    public UUID uuid;
    public WorldEventType type;
    public Level level;
    public boolean discarded;
    public boolean dirty;
    public boolean frozen;

    public WorldEventInstance(WorldEventType type) {
        if (type == null) throw new IllegalArgumentException("World event type cannot be null");
        this.uuid = UUID.randomUUID();
        this.type = type;
    }

    /**
     * Abstract method for ticking.
     */
    public abstract void tick(Level level);

    /**
     * Codec for base fields. Subclasses must add their own fields.
     */
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);

    public static final Codec<WorldEventInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUID_CODEC.fieldOf("uuid").forGetter(event -> event.uuid),
            WorldEventType.CODEC.fieldOf("type").forGetter(event -> event.type),
            CompoundTag.CODEC.optionalFieldOf("data").forGetter(event -> {
                // Handle serialization of additional fields
                CompoundTag tag = new CompoundTag();

                return java.util.Optional.of(tag);
            }),
            Codec.BOOL.fieldOf("discarded").forGetter(event -> event.discarded),
            Codec.BOOL.fieldOf("frozen").forGetter(event -> event.frozen)
    ).apply(instance, (uuid, type, data, discarded, frozen) -> {
        // Deserialize additional data
        WorldEventInstance event = type.createInstance(data.orElse(new CompoundTag()));
        event.uuid = uuid;
        event.discarded = discarded;
        event.frozen = frozen;
        return event;
    }));
    /**
     * Subclasses must implement this to extend serialization.
     */
    protected abstract Codec<? extends WorldEventInstance> getCodec();

    /**
     * Syncing and management methods remain unchanged.
     */
    public void end(Level level) {
        discarded = true;
    }

    public void setDirty() {
        dirty = true;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public Level getLevel() {
        return level;
    }

    public void sync(Level level) {
        if (!level.isClientSide && type.isClientSynced()) {
            sync(this);
        }
    }

    public void start(Level level) {
        this.level = level;
    }

    public CompoundTag synchronizeNBT() {
        return serializeNBT();
    }

    public CompoundTag serializeNBT() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(System.err::println)
                .map(tag -> (CompoundTag) tag)
                .orElse(new CompoundTag());
    }

    public static WorldEventInstance deserializeNBT(CompoundTag tag) {
        return CODEC.parse(NbtOps.INSTANCE, tag)
                .result()
                .orElseThrow(() -> new IllegalStateException("Failed to deserialize WorldEventInstance!"));
    }

    public static <T extends WorldEventInstance> void sync(T instance) {
        sync(instance, null);
    }

    public static <T extends WorldEventInstance> void sync(T instance, @Nullable ServerPlayer player) {
        if (player != null) {
            ServerPlayNetworking.send(player, new SyncWorldEventPayload(instance, false));
            return;
        }
        LodestoneNetworkPayloads.sendToPlayers(instance.level, new SyncWorldEventPayload(instance, false));
    }
}