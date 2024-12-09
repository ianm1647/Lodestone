package team.lodestar.lodestone.systems.rendering;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

public interface IVertexBuffer {
    static IVertexBuffer cast(VertexBuffer vertexBuffer) {
        return (IVertexBuffer) (Object) vertexBuffer;
    }
    void drawInstanced(int instances);

    void drawWithShaderInstanced(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader, int instances);
}
