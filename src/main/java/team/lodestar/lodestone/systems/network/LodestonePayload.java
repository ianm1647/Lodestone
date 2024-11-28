package team.lodestar.lodestone.systems.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@FunctionalInterface
public interface LodestonePayload {
    <T extends CustomPacketPayload> void handle(T payload, ClientPlayNetworking.Context context);
}
