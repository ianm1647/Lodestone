package team.lodestar.lodestone.ducks;

import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

public interface IVertexBuffer {
    void drawInstanced(int instances);

    void drawWithShaderInstanced(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader, int instances);
}
