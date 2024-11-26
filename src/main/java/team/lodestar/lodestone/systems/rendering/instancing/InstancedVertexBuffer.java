package team.lodestar.lodestone.systems.rendering.instancing;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.registry.client.LodestoneOBJModels;
import team.lodestar.lodestone.systems.model.obj.IndexedModel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class InstancedVertexBuffer implements AutoCloseable {
    private List<Integer> vbos = new ArrayList<>();
    private int indexBufferObject = -1;
    private int vertexArrayObject = -1;
    private VertexFormat format;
    private VertexFormat.Mode mode;

    public InstancedVertexBuffer(VertexFormat format, VertexFormat.Mode mode) {
        this.format = format;
        this.mode = mode;
        LodestoneOBJModels.addInstancedVertexBuffer(this);
    }

    public void uploadModel(IndexedModel model) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                LodestoneLib.LOGGER.info("Uploading model on render thread: " + model);
                _uploadModel(model);
            });
        } else {
            LodestoneLib.LOGGER.info("Uploading model on render thread2: " + model);
            _uploadModel(model);
        }
    }

    private void _uploadModel(IndexedModel model) {
        createVAO();
        storeIndexBuffer(model.getIndicesBuffer());
        storeVertexAttributes(model);
    }

    private void createVAO() {
        this.vertexArrayObject = GlStateManager._glGenVertexArrays();
        GlStateManager._glBindVertexArray(this.vertexArrayObject);
    }

    private void storeIndexBuffer(ByteBuffer indices) {
        this.indexBufferObject = GlStateManager._glGenBuffers();
        GlStateManager._glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indexBufferObject);
        GlStateManager._glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    private void storeVertexAttributes(IndexedModel model) {
        int stride = this.format.getVertexSize();
        for (int j = 0; j < this.format.getElements().size(); j++) {
            VertexFormatElement element = this.format.getElements().get(j);
            storeVertexAttribute(element, model.getAttribute(element), j, stride);
        }
    }

    private void storeVertexAttribute(VertexFormatElement element, ByteBuffer data, int attributeNumber, int stride) {
        int vbo = glGenBuffers();
        this.vbos.add(vbo);
        GlStateManager._glBindBuffer(GL_ARRAY_BUFFER, vbo);
        GlStateManager._glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        element.setupBufferState(attributeNumber, this.format.getOffset(element), stride); // glVertexAttribPointer
        GlStateManager._enableVertexAttribArray(0);
    }

    public void bind() {
        GlStateManager._glBindVertexArray(this.vertexArrayObject);
    }

    public void unbind() {
        GlStateManager._glBindVertexArray(0);
    }

    @Override
    public void close() {
        LodestoneLib.LOGGER.info("Deleting InstancedVertexBuffer: " + this);
        for (int i = 0; i < this.vbos.size(); i++) {
            if (this.vbos.get(i) >= 0) {
                RenderSystem.glDeleteBuffers(this.vbos.get(i));
                this.vbos.set(i, -1);
            }
        }

        if (this.indexBufferObject >= 0) {
            RenderSystem.glDeleteBuffers(this.indexBufferObject);
            this.indexBufferObject = -1;
        }

        if (this.vertexArrayObject >= 0) {
            RenderSystem.glDeleteVertexArrays(this.vertexArrayObject);
            this.vertexArrayObject = -1;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("InstancedVertexBuffer{");
        builder.append("vertexArrayObject=").append(this.vertexArrayObject);
        builder.append(", indexBufferObject=").append(this.indexBufferObject);
        builder.append(", vbos=").append(this.vbos);
        return builder.append('}').toString();
    }
}
