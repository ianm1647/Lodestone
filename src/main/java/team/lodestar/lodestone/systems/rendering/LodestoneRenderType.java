package team.lodestar.lodestone.systems.rendering;

import com.mojang.blaze3d.systems.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import team.lodestar.lodestone.registry.client.*;

import java.util.*;

import static net.minecraft.client.renderer.RenderType.CompositeRenderType.OUTLINE;

public class LodestoneRenderType extends RenderType {
    public final RenderType.CompositeState state;
    private final Optional<RenderType> outline;
    private final boolean isOutline;

    public static LodestoneRenderType createRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, CompositeState pState) {
        return new LodestoneRenderType(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pState);
    }

    public LodestoneRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, RenderType.CompositeState pState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, () -> {
            pState.states.forEach(RenderStateShard::setupRenderState);
        }, () -> {
            pState.states.forEach(RenderStateShard::clearRenderState);
        });
        this.state = pState;
        this.outline = pState.outlineProperty == RenderType.OutlineProperty.AFFECTS_OUTLINE ? pState.textureState.cutoutTexture().map((p_173270_) -> OUTLINE.apply(p_173270_, pState.cullState)) : Optional.empty();
        this.isOutline = pState.outlineProperty == RenderType.OutlineProperty.IS_OUTLINE;
    }

    public Optional<RenderType> outline() {
        return this.outline;
    }

    public boolean isOutline() {
        return this.isOutline;
    }

    protected final RenderType.CompositeState state() {
        return this.state;
    }

    public String toString() {
        return "RenderType[" + this.name + ":" + this.state + "]";
    }

    @Override
    public void draw(MeshData meshData) {
        //I think there's a better way to do this, but this makes sure our depth writing is correct
        RenderSystem.depthMask(state.writeMaskState.writeDepth);
        this.setupRenderState();
        BufferUploader.drawWithShader(meshData);
        this.clearRenderState();
    }
}