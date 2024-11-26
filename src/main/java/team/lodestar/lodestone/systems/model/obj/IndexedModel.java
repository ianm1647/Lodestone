package team.lodestar.lodestone.systems.model.obj;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.handlers.InstanceRenderHandler;
import team.lodestar.lodestone.systems.model.obj.data.*;
import team.lodestar.lodestone.systems.model.obj.modifier.*;
import team.lodestar.lodestone.systems.rendering.instancing.InstancedData;
import team.lodestar.lodestone.systems.rendering.instancing.InstancedVertexBuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.mojang.blaze3d.vertex.VertexFormatElement.*;

public abstract class IndexedModel {
    protected List<Vertex> vertices;
    protected List<IndexedMesh> meshes;
    protected List<Integer> bakedIndices;
    protected List<ModelModifier<?>> modifiers;
    protected ResourceLocation modelId;
    protected InstancedVertexBuffer instancedVertexBuffer;

    public IndexedModel(ResourceLocation modelId) {
        this.modelId = modelId;
        this.vertices = new ArrayList<>();
        this.meshes = new ArrayList<>();
        this.bakedIndices = new ArrayList<>();
        this.instancedVertexBuffer = new InstancedVertexBuffer(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES);
    }

    public void render(PoseStack poseStack, RenderType renderType, MultiBufferSource.BufferSource bufferSource) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        this.render(poseStack, vertexConsumer, renderType);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, RenderType renderType) {
        for (IndexedMesh mesh : this.meshes) {
            if (mesh.isCompatibleWith(renderType.mode())) {
                for (Vertex vertex : mesh.getVertices(this)) {
                    vertex.supplyVertexData(vertexConsumer, renderType.format(), poseStack);
                }
            }
        }
    }

    public abstract void loadModel();

    public void applyModifiers() {
        if (modifiers != null) {
            modifiers.forEach(modifier -> modifier.apply(this));
        }
    }

    public List<Vertex> getVertices() {
        return this.vertices;
    }

    public List<IndexedMesh> getMeshes() {
        return this.meshes;
    }

    public void setMeshes(List<IndexedMesh> meshes) {
        this.meshes = meshes;
    }

    public List<Integer> getBakedIndices() {
        return this.bakedIndices;
    }

    public ResourceLocation getModelId() {
        return this.modelId;
    }

    public ResourceLocation getAssetLocation() {
        return ResourceLocation.fromNamespaceAndPath(modelId.getNamespace(), "models/" + modelId.getPath() + ".obj");
    }

    public void bakeIndices(VertexFormat.Mode mode, boolean triangulate) {
        this.bakedIndices.clear();
        this.meshes.stream()
                .filter(mesh -> mesh.indices.size() == mode.primitiveLength)
                .forEach(mesh -> this.bakedIndices.addAll(mesh.indices));

        for (IndexedMesh mesh : meshes) {
            if (mesh.indices.size() != mode.primitiveLength) {
                if (mesh.indices.size() == 4 && triangulate) {
                    this.bakedIndices.add(mesh.indices.get(0));
                    this.bakedIndices.add(mesh.indices.get(1));
                    this.bakedIndices.add(mesh.indices.get(2));

                    this.bakedIndices.add(mesh.indices.get(2));
                    this.bakedIndices.add(mesh.indices.get(3));
                    this.bakedIndices.add(mesh.indices.get(0));
                } else {
                    this.bakedIndices.addAll(mesh.indices);
                }
            } else {
                this.bakedIndices.addAll(mesh.indices);
            }
        }

    }

    public ByteBuffer getPositionsBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(this.vertices.size() * 3 * Float.BYTES);
        this.vertices.forEach(vertex -> {
            buffer.putFloat(vertex.getPosition().x);
            buffer.putFloat(vertex.getPosition().y);
            buffer.putFloat(vertex.getPosition().z);
        });
        buffer.flip();
        return buffer;
    }

    public ByteBuffer getNormalsBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(this.vertices.size() * 3 * Float.BYTES);
        this.vertices.forEach(vertex -> {
            buffer.putFloat(vertex.getNormal().x);
            buffer.putFloat(vertex.getNormal().y);
            buffer.putFloat(vertex.getNormal().z);
        });
        buffer.flip();
        return buffer;
    }

    public ByteBuffer getTextureCoordinatesBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(this.vertices.size() * 2 * Float.BYTES);
        this.vertices.forEach(vertex -> {
            buffer.putFloat(vertex.getUv().x);
            buffer.putFloat(vertex.getUv().y);
        });
        buffer.flip();
        return buffer;
    }

    public ByteBuffer getColorBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(this.vertices.size() * 4 * Float.BYTES);
        this.vertices.forEach(vertex -> {
            buffer.putFloat(vertex.getColor().x());
            buffer.putFloat(vertex.getColor().y());
            buffer.putFloat(vertex.getColor().z());
            buffer.putFloat(vertex.getColor().w());
        });
        buffer.flip();
        return buffer;
    }

    public ByteBuffer getAttribute(VertexFormatElement element) {
        if (element.equals(POSITION)) {
            return getPositionsBuffer();
        } else if (element.equals(NORMAL)) {
            return getNormalsBuffer();
        } else if (element.equals(UV)) {
            return getTextureCoordinatesBuffer();
        } else if (element.equals(COLOR)) {
            return getColorBuffer();
        } else {
            return null;
        }
    }

    public ByteBuffer getIndicesBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(this.bakedIndices.size() * Integer.BYTES);
        this.bakedIndices.forEach(buffer::putInt);
        buffer.flip();
        return buffer;
    }

    public void addInstance(InstancedData data) {
        InstanceRenderHandler.addInstance(this, data);
    }
}
