package team.lodestar.lodestone.mixin.client;

import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import team.lodestar.lodestone.systems.rendering.IVertexBuffer;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderSystem;

import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

@Mixin(VertexBuffer.class)
public abstract class VertexBufferMixin implements IVertexBuffer {
    @Override
    public void drawInstanced(int instances) {
        LodestoneRenderSystem.wrap(() -> {
            glDrawElementsInstanced(this.mode.asGLMode, this.indexCount, this.getIndexType().asGLType, 0, instances);
        });
    }

    @Override
    public void drawWithShaderInstanced(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader, int instances) {
        LodestoneRenderSystem.wrap(() -> {
            shader.setDefaultUniforms(this.mode, modelViewMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
            shader.apply();
            this.drawInstanced(instances);
            shader.clear();
        });
    }

    @Shadow
    private VertexFormat.Mode mode;
    @Shadow
    private int indexCount;
    @Shadow
    public abstract VertexFormat.IndexType getIndexType();
}
