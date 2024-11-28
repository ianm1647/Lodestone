package team.lodestar.lodestone.registry.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import team.lodestar.lodestone.network.TotemOfUndyingPayload;
import team.lodestar.lodestone.network.screenshake.PositionedScreenshakePayload;
import team.lodestar.lodestone.network.screenshake.ScreenshakePayload;
import team.lodestar.lodestone.network.worldevent.SyncWorldEventPayload;
import team.lodestar.lodestone.network.worldevent.UpdateWorldEventPayload;
import team.lodestar.lodestone.systems.network.LodestonePayload;

import java.util.function.BiConsumer;

public class LodestoneNetworkPayloads {

    public static void register() {
        registerS2C(
                PositionedScreenshakePayload.ID,
                PositionedScreenshakePayload.PAYLOAD_STREAM_CODEC,
                (payload, context) -> payload.handle(payload, context)
        );
        registerS2C(
                ScreenshakePayload.ID,
                ScreenshakePayload.PAYLOAD_STREAM_CODEC,
                (payload, context) -> payload.handle(payload, context)
        );
        registerS2C(
                UpdateWorldEventPayload.ID,
                UpdateWorldEventPayload.STREAM_CODEC,
                (payload, context) -> payload.handle(payload, context)
        );
        registerS2C(
                SyncWorldEventPayload.ID,
                SyncWorldEventPayload.STREAM_CODEC,
                (payload, context) -> payload.handle(payload, context)
        );
        registerS2C(
                TotemOfUndyingPayload.ID,
                TotemOfUndyingPayload.STREAM_CODEC,
                (payload, context) -> payload.handle(payload, context)
        );
    }

    private static <T extends CustomPacketPayload> void registerS2C(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, ClientPlayNetworking.Context> handler
    ) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
                context.client().execute(() -> {
                    handler.accept(payload, context);
                });
            });
        } else {
            PayloadTypeRegistry.playS2C().register(type, codec);
        }
    }

    public static <T extends CustomPacketPayload> void sendToPlayers(Level level, T payload) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerLevel currentLevel : serverLevel.getServer().getAllLevels()) {
                for (ServerPlayer player : currentLevel.players()) {
                    ServerPlayNetworking.send(player, payload);
                }
            }
        }
    }

    public static <T extends CustomPacketPayload> void sendToPlayers(Level level, BlockPos pos, T payload) {
        if (level instanceof ServerLevel serverLevel) {
            sendToPlayers(serverLevel, pos, payload);
        }
    }

    public static <T extends CustomPacketPayload> void sendToPlayers(ServerLevel level, BlockPos pos, T payload) {
        var players = level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
        for (ServerPlayer player : players) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}