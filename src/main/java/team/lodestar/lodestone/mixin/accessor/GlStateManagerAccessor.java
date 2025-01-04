package team.lodestar.lodestone.mixin.accessor;

import com.mojang.blaze3d.platform.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GlStateManager.class)
public interface GlStateManagerAccessor {
    @Accessor("TEXTURES")
    static GlStateManager.TextureState[] getTextureStates() {
        throw new AssertionError();
    }

    @Accessor("activeTexture")
    static int getActiveTexture() {
        throw new AssertionError();
    }
}
