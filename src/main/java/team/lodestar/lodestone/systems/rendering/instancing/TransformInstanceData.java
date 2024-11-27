package team.lodestar.lodestone.systems.rendering.instancing;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class TransformInstanceData {
    private Matrix4f matrix4f;

    public TransformInstanceData(@NotNull PoseStack poseStack) {
        this(poseStack.last().pose());
    }

    public TransformInstanceData(@NotNull Matrix4f matrix4f) {
        this.matrix4f = matrix4f;
    }

    public void storeInstancedData(InstancedVertexBuffer vertexBuffer, FloatBuffer floatBuffer) {

        int index = vertexBuffer.getNextIndex();
        vertexBuffer.bind();
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_DYNAMIC_DRAW);
        for (int i = 0; i < 4; i++) {
            glVertexAttribPointer(index + i, 4, GL_FLOAT, false, 4 * 4 * Float.BYTES, i * 4 * Float.BYTES);
            glEnableVertexAttribArray(index + i);
            glVertexAttribDivisor(index + i, 1);
        }
    }

    public static int getSizeInBytes() {
        return 16 * Float.BYTES;
    }

    public Matrix4f getTransform() {
        return matrix4f;
    }
}
