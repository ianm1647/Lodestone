package team.lodestar.lodestone.mixin.client;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.EffectInstance;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.mixin.accessor.GlStateManagerAccessor;
import team.lodestar.lodestone.systems.rendering.shader.SamplerType;

import java.util.List;
import java.util.Map;

@Mixin(EffectInstance.class)
public abstract class EffectInstanceMixin {

    @Unique
    private Map<String, SamplerType> samplerTypeMap = Maps.newHashMap();

    @Redirect(method = "parseSamplerNode", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean samplerDimension(List<String> samplerNames, Object s, @Local JsonElement json) {
        String name = s.toString();
        if (json.getAsJsonObject().has("type")) {
            SamplerType type = SamplerType.fromString(json.getAsJsonObject().get("type").getAsString());
            if (type == null) {
                LodestoneLib.LOGGER.warn("Unknown sampler type: " + json.getAsJsonObject().get("type").getAsString());
            } else {
                samplerTypeMap.put(name, type);
            }
        }
        return samplerNames.add(name);
    }

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindTexture(I)V"))
    private void bindTex(int textureBinding, @Local String s) {
        if (samplerTypeMap.containsKey(s)) {
            SamplerType type = samplerTypeMap.get(s);
            bindTexture(type.getGlType(), textureBinding);
        } else {
            RenderSystem.bindTexture(textureBinding);
        }
    }

    private void bindTexture(int samplerType, int texture) {
        if (texture != GlStateManagerAccessor.getTextureStates()[GlStateManagerAccessor.getActiveTexture()].binding) {
            GlStateManagerAccessor.getTextureStates()[GlStateManagerAccessor.getActiveTexture()].binding = texture;
            GL11.glBindTexture(samplerType, texture);
        }
    }
}
