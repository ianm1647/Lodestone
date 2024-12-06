package team.lodestar.lodestone.systems.model.obj;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.ducks.IVertexBuffer;
import team.lodestar.lodestone.systems.model.obj.data.*;
import team.lodestar.lodestone.systems.model.obj.modifier.*;

import java.util.ArrayList;
import java.util.List;

public abstract class IndexedModel {
    protected List<Vertex> vertices;
    protected List<IndexedMesh> meshes;
    protected List<Integer> bakedIndices;
    protected List<ModelModifier<?>> modifiers;
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

    public void renderInstanced(PoseStack poseStack, RenderType renderType, int instances) {
        this.createMeshBuffer(poseStack);
        this.modelBuffer.bind();
        renderType.setupRenderState();
        ((IVertexBuffer) (Object) this.modelBuffer).drawWithShaderInstanced(poseStack.last().pose(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader(), instances);
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

    public void createMeshBuffer(PoseStack poseStack) {
        if (this.modelBuffer != null) {
            return;
        }

        this.modelBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.modelBuffer.bind();
        MeshData meshData = this.drawMesh(poseStack, Tesselator.getInstance());
        this.modelBuffer.upload(meshData);
        VertexBuffer.unbind();
    }

    public MeshData drawMesh(PoseStack poseStack, Tesselator tesselator) {
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);
        for (IndexedMesh mesh : this.meshes) {
            for (Vertex vertex : mesh.getVertices(this)) {
                vertex.supplyVertexData(bufferBuilder, DefaultVertexFormat.POSITION, poseStack);
            }
        }
        return bufferBuilder.buildOrThrow();
    }

    public void cleanup() {
        if (this.modelBuffer != null) {
            this.modelBuffer.close();
        }
    }
}
