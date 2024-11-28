package team.lodestar.lodestone.registry.common;

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

public class LodestoneNetworkPayloads {

    public static void register() {


        LODESTONE_CHANNEL.playToClient(registrar, "totem_of_undying", TotemOfUndyingPayload.class, TotemOfUndyingPayload::new);
        LODESTONE_CHANNEL.playToClient(registrar, "sync_world_event", SyncWorldEventPayload.class, SyncWorldEventPayload::new);
        LODESTONE_CHANNEL.playToClient(registrar, "update_world_event", UpdateWorldEventPayload.class, UpdateWorldEventPayload::new);
        LODESTONE_CHANNEL.playToClient(registrar, "screenshake", ScreenshakePayload.class, ScreenshakePayload::new);
        LODESTONE_CHANNEL.playToClient(registrar, "positioned_screenshake", PositionedScreenshakePayload.class, PositionedScreenshakePayload::new);
    }

    private <T extends CustomPacketPayload> void registerC2S(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            NetworkManager.NetworkReceiver<T> receiver
    ) {
        NetworkManager.registerReceiver(NetworkManager.c2s(), type, codec, receiver);
    }

    private <T extends CustomPacketPayload> void registerS2C(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            NetworkManager.NetworkReceiver<T> receiver
    ) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), type, codec, receiver);
        } else {
            NetworkManager.registerS2CPayloadType(type, codec);
        }
    }

    public <T extends CustomPacketPayload> void sendToPlayers(Level level, T payload) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerLevel currentLevel : serverLevel.getServer().getAllLevels()) {
                for (Player player : currentLevel.players()) {
                    NetworkManager.sendToPlayer((ServerPlayer) player, payload);
                }
            }
        }
    }

    public <T extends CustomPacketPayload> void sendToPlayers(Level level, BlockPos pos, T payload) {
        if (level instanceof ServerLevel serverLevel) {
            sendToPlayers(serverLevel, pos, payload);
        }
    }

    public <T extends CustomPacketPayload> void sendToPlayers(ServerLevel level, BlockPos pos, T payload) {
        var players = level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false);
        for (ServerPlayer player : players) {
            NetworkManager.sendToPlayer(player, payload);
        }
    }
}