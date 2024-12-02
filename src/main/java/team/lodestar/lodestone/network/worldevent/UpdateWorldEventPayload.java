package team.lodestar.lodestone.network.worldevent;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.registry.common.LodestoneAttachmentTypes;
import team.lodestar.lodestone.systems.network.LodestonePayload;
import team.lodestar.lodestone.systems.worldevent.WorldEventInstance;

import java.util.UUID;

public class UpdateWorldEventPayload implements CustomPacketPayload, LodestonePayload {
    public static CustomPacketPayload.Type<UpdateWorldEventPayload> ID = new CustomPacketPayload.Type<>(LodestoneLib.lodestonePath("update_world_event"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, UpdateWorldEventPayload> STREAM_CODEC = CustomPacketPayload.codec(UpdateWorldEventPayload::write, UpdateWorldEventPayload::new);


    private final UUID uuid;
    private final CompoundTag eventData;

    public UpdateWorldEventPayload(FriendlyByteBuf byteBuf) {
        this(byteBuf.readUUID(), byteBuf.readNbt());
    }

    public UpdateWorldEventPayload(WorldEventInstance instance) {
        this(instance.uuid, instance.serializeNBT());
    }

    public UpdateWorldEventPayload(UUID uuid, CompoundTag eventData) {
        this.uuid = uuid;
        this.eventData = eventData;
    }

    private void write(RegistryFriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(uuid);
        byteBuf.writeNbt(eventData);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    @Override
    public <T extends CustomPacketPayload> void handle(T payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                var worldData = level.getAttachedOrCreate(LodestoneAttachmentTypes.WORLD_EVENT_DATA);
                for (WorldEventInstance instance : worldData.activeWorldEvents) {
                    if (instance.uuid.equals(uuid)) {
                        instance.deserializeNBT(eventData);
                        break;
                    }
                }
            }
        });
    }
}
