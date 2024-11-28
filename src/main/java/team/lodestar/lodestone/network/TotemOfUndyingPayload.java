package team.lodestar.lodestone.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.*;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.network.LodestonePayload;

public class TotemOfUndyingPayload implements CustomPacketPayload, LodestonePayload {

    public static CustomPacketPayload.Type<TotemOfUndyingPayload> ID = new CustomPacketPayload.Type<>(LodestoneLib.lodestonePath("totem_of_undying"));
    public static final StreamCodec<? super RegistryFriendlyByteBuf, TotemOfUndyingPayload> STREAM_CODEC = CustomPacketPayload.codec(TotemOfUndyingPayload::write, TotemOfUndyingPayload::new);
    private final int entityId;
    private ItemStack stack;

    public TotemOfUndyingPayload(RegistryFriendlyByteBuf byteBuf) {
        entityId = byteBuf.readInt();
        stack = ItemStack.parseOptional(byteBuf.registryAccess(), byteBuf.readNbt());
    }

    @Override
    public <T extends CustomPacketPayload> void handle(T payload, ClientPlayNetworking.Context context) {

        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = minecraft.level.getEntity(entityId);
        if (entity instanceof LivingEntity livingEntity) {
            minecraft.particleEngine.createTrackingEmitter(livingEntity, ParticleTypes.TOTEM_OF_UNDYING, 30);
            minecraft.level.playLocalSound(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.TOTEM_USE, livingEntity.getSoundSource(), 1.0F, 1.0F, false);
            if (livingEntity == minecraft.player) {
                minecraft.gameRenderer.displayItemActivation(stack);
            }
        }
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeNbt(stack.save(buf.registryAccess()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
