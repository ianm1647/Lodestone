package team.lodestar.lodestone.mixin.client;

import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import team.lodestar.lodestone.systems.rendering.shader.ExtendedShaderInstance;

@Mixin(ShaderInstance.class)
public class ShaderInstanceMixin {


}
