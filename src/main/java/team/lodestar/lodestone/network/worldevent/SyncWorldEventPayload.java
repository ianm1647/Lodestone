package team.lodestar.lodestone.network.worldevent;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.*;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.handlers.WorldEventHandler;
import team.lodestar.lodestone.network.TotemOfUndyingPayload;
import team.lodestar.lodestone.registry.common.LodestoneWorldEventTypes;
import team.lodestar.lodestone.systems.network.LodestonePayload;
import team.lodestar.lodestone.systems.worldevent.*;

public class SyncWorldEventPayload implements CustomPacketPayload, LodestonePayload {

    public static CustomPacketPayload.Type<SyncWorldEventPayload> ID = new CustomPacketPayload.Type<>(LodestoneLib.lodestonePath("sync_world_event"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, SyncWorldEventPayload> STREAM_CODEC = CustomPacketPayload.codec(SyncWorldEventPayload::write, SyncWorldEventPayload::new);
    private final ResourceLocation type;
    private final boolean start;
    private final CompoundTag eventData;

    public SyncWorldEventPayload(FriendlyByteBuf byteBuf) {
        this(byteBuf.readResourceLocation(), byteBuf.readBoolean(), byteBuf.readNbt());
    }
    public SyncWorldEventPayload(WorldEventInstance instance, boolean start) {
        this(instance.type.id, start, instance.serializeNBT());
    }

    public SyncWorldEventPayload(ResourceLocation type, boolean start, CompoundTag eventData) {
        this.type = type;
        this.start = start;
        this.eventData = eventData;
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(type);
        buf.writeBoolean(start);
        buf.writeNbt(eventData);
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    @Override
    public <T extends CustomPacketPayload> void handle(T payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            WorldEventType eventType = LodestoneWorldEventTypes.WORLD_EVENT_TYPE_REGISTRY.get(type);
            ClientLevel level = Minecraft.getInstance().level;
            WorldEventHandler.addWorldEvent(level, start, eventType.createInstance(eventData));
        });
    }
}
