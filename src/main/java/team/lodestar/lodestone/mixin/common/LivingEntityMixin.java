package team.lodestar.lodestone.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.block.SoundType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.lodestar.lodestone.registry.common.LodestoneAttributes;
import team.lodestar.lodestone.systems.sound.ExtendedSoundType;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {


    @Unique
    private SoundType lodestone$type;

    @ModifyVariable(method = "playBlockFallSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private SoundType lodestone$GetStepSound(SoundType type) {
        return this.lodestone$type = type;
    }

    @Inject(method = "playBlockFallSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void lodestone$CallExtendedStepSound(CallbackInfo ci) {
        if (lodestone$type instanceof ExtendedSoundType extendedSoundType) {
            Entity entity = ((Entity) (Object) this);
            extendedSoundType.onPlayFallSound(entity.level(), entity.getOnPos(), entity.getSoundSource());
        }
    }

    @Inject(method = "createLivingAttributes", require = 1, allow = 1, at = @At("RETURN"))
    private static void additionalEntityAttributes$addPlayerAttributes(final CallbackInfoReturnable<AttributeSupplier.Builder> info) {
        info.getReturnValue().add(LodestoneAttributes.MAGIC_DAMAGE);
        info.getReturnValue().add(LodestoneAttributes.MAGIC_PROFICIENCY);
        info.getReturnValue().add(LodestoneAttributes.MAGIC_RESISTANCE);
    }
}
