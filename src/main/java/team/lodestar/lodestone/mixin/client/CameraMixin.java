package team.lodestar.lodestone.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.config.ClientConfig;
import team.lodestar.lodestone.events.LodestoneCameraEvent;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;


@Mixin(Camera.class)
public class CameraMixin {

    @Inject(method = "setup", at = @At("RETURN"))
    private void lodestone$Screenshake(BlockGetter area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (ClientConfig.SCREENSHAKE_INTENSITY.getConfigValue() > 0) {
            ScreenshakeHandler.clientTick((Camera) (Object) this);
        }
    }

    @WrapWithCondition(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 0))
    private boolean lodestone$cameraEventInject(Camera instance, float yRot, float xRot, @Local(argsOnly = true) float partialTick){
        var cameraSetup = LodestoneCameraEvent.EVENT.invoker().setup(instance, partialTick, yRot, xRot);

        return true;
    }
}