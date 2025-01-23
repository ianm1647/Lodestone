package team.lodestar.lodestone.systems.model.obj;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import team.lodestar.lodestone.systems.rendering.IVertexBuffer;
import team.lodestar.lodestone.systems.model.obj.data.*;
import team.lodestar.lodestone.systems.model.obj.modifier.*;

import java.util.ArrayList;
import java.util.List;

public abstract class IndexedModel {
    protected List<Vertex> vertices;
    protected List<IndexedMesh> meshes;
    protected List<Integer> bakedIndices;
    public List<ModelModifier> earlyModifiers;
    protected List<ModelModifier> modifiers;
    protected ResourceLocation modelId;
    protected VertexBuffer modelBuffer;
    protected MeshData meshData;

    public IndexedModel(ResourceLocation modelId) {
        this.modelId = modelId;
        this.vertices = new ArrayList<>();
        this.meshes = new ArrayList<>();
        this.bakedIndices = new ArrayList<>();
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

    public void renderInstanced(PoseStack poseStack, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, RenderType renderType, int instances) {
        this.createMeshBuffer(poseStack, renderType);
        this.modelBuffer.bind();
        renderType.setupRenderState();
        IVertexBuffer.cast(this.modelBuffer).drawWithShaderInstanced(frustrumMatrix, projectionMatrix, RenderSystem.getShader(), instances);
        renderType.clearRenderState();
        VertexBuffer.unbind();
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

        // TODO: Use triangulation modifier
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

    public void createMeshBuffer(PoseStack poseStack, RenderType renderType) {
        if (this.modelBuffer == null) {
            this.modelBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
        }
        this.modelBuffer.bind();
        this.createMesh(poseStack, Tesselator.getInstance(), renderType);
        this.modelBuffer.upload(this.meshData);
        VertexBuffer.unbind();
    }

    public void createMesh(PoseStack poseStack, Tesselator tesselator, RenderType renderType) {
        BufferBuilder bufferBuilder = tesselator.begin(renderType.mode(), renderType.format());
        for (IndexedMesh mesh : this.meshes) {
            for (Vertex vertex : mesh.getVertices(this)) {
                vertex.supplyVertexData(bufferBuilder, renderType.format(), poseStack);
            }
        }
        this.meshData = bufferBuilder.buildOrThrow();
    }

    public VertexBuffer getModelBuffer() {
        return this.modelBuffer;
    }

    public void cleanup() {
        if (this.modelBuffer != null) {
            this.modelBuffer.close();
        }
    }
}
