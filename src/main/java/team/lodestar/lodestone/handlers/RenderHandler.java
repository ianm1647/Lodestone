package team.lodestar.lodestone.handlers;

import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Matrix4f;
import team.lodestar.lodestone.*;
import team.lodestar.lodestone.helpers.RenderHelper;
import team.lodestar.lodestone.systems.rendering.rendeertype.ShaderUniformHandler;
import team.lodestar.lodestone.systems.rendering.shader.ExtendedShaderInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static team.lodestar.lodestone.systems.rendering.StateShards.NORMAL_TRANSPARENCY;

/**
 * A handler responsible for all the backend rendering processes.
 * To have additive transparency work in a minecraft environment, we need to buffer our rendering till after clouds and water have rendered.
 * This happens for particles, as well as all of our custom RenderTypes
 */
public class RenderHandler {
    public static final HashMap<RenderType, BufferBuilder> BUFFERS = new HashMap<>();
    public static final HashMap<RenderType, BufferBuilder> PARTICLE_BUFFERS = new HashMap<>();
    public static final HashMap<RenderType, ShaderUniformHandler> UNIFORM_HANDLERS = new HashMap<>();
    public static final Collection<RenderType> TRANSPARENT_RENDER_TYPES = new ArrayList<>();

    public static boolean LARGER_BUFFER_SOURCES = ModList.get().isLoaded("rubidium");

    public static MultiBufferSource.BufferSource DELAYED_RENDER;
    public static MultiBufferSource.BufferSource DELAYED_PARTICLE_RENDER;

    public static PoseStack MAIN_POSE_STACK;
    public static Matrix4f MATRIX4F;

    public static float FOG_NEAR;
    public static float FOG_FAR;
    public static FogShape FOG_SHAPE;
    public static float FOG_RED, FOG_GREEN, FOG_BLUE;

    public static PostChain LODESTONE_POST_CHAIN;
    public static RenderTarget LODESTONE_DEPTH;
    public static RenderTarget LODESTONE_TRANSLUCENT_TARGET;
    public static RenderTarget LODESTONE_ADDITIVE_TARGET;

    public static void onClientSetup(FMLClientSetupEvent event) {
        int size = LARGER_BUFFER_SOURCES ? 262144 : 256;

        DELAYED_RENDER = MultiBufferSource.immediateWithBuffers(BUFFERS, new BufferBuilder(size));
        DELAYED_PARTICLE_RENDER = MultiBufferSource.immediateWithBuffers(PARTICLE_BUFFERS, new BufferBuilder(size));
    }

    public static void setupLodestoneRenderTargets() {
        Minecraft minecraft = Minecraft.getInstance();
        try {
            PostChain postChain = new PostChain(minecraft.getTextureManager(), minecraft.getResourceManager(), minecraft.getMainRenderTarget(), LodestoneLib.lodestonePath("shaders/lodestone_post_chain.json"));
            postChain.resize(minecraft.getWindow().getWidth(), minecraft.getWindow().getHeight());
            LODESTONE_DEPTH = postChain.getTempTarget("lodestone_depth");
            LODESTONE_TRANSLUCENT_TARGET = postChain.getTempTarget("lodestone_translucent");
            LODESTONE_ADDITIVE_TARGET = postChain.getTempTarget("lodestone_additive");
            LODESTONE_POST_CHAIN = postChain;
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
    public static void resize(int width, int height) {
        if (LODESTONE_POST_CHAIN != null) {
            LODESTONE_POST_CHAIN.resize(width, height);
            LODESTONE_DEPTH.resize(width, height, Minecraft.ON_OSX);
        }
    }

    public static void endBatchesEarly() {
        LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
        endBatches(levelRenderer.transparencyChain != null);
    }

    public static void endBatchesLate() {
        LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
        if (levelRenderer.transparencyChain != null) {
            LODESTONE_POST_CHAIN.process(Minecraft.getInstance().getPartialTick());
        }
    }

    public static void endBatches(boolean isFabulous) {
        LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
        Matrix4f last = new Matrix4f(RenderSystem.getModelViewMatrix());
        if (isFabulous) {
            RenderHandler.LODESTONE_DEPTH.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
            RenderHandler.LODESTONE_TRANSLUCENT_TARGET.clear(Minecraft.ON_OSX);
            RenderHandler.LODESTONE_TRANSLUCENT_TARGET.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
            RenderHandler.LODESTONE_TRANSLUCENT_TARGET.bindWrite(false);
        }
        RenderHandler.beginBufferedRendering();
        RenderHandler.renderBufferedParticles(true);
        if (RenderHandler.MATRIX4F != null) {
            RenderSystem.getModelViewMatrix().set(RenderHandler.MATRIX4F);
        }
        RenderHandler.renderBufferedBatches(true);
        if (isFabulous) {
            RenderHandler.LODESTONE_TRANSLUCENT_TARGET.unbindWrite();
            RenderHandler.LODESTONE_ADDITIVE_TARGET.clear(Minecraft.ON_OSX);
            RenderHandler.LODESTONE_ADDITIVE_TARGET.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
            RenderHandler.LODESTONE_ADDITIVE_TARGET.bindWrite(false);
        }
        RenderHandler.renderBufferedBatches(false);
        RenderSystem.getModelViewMatrix().set(last);
        RenderHandler.renderBufferedParticles(false);

        RenderHandler.endBufferedRendering();
        if (isFabulous) {
            levelRenderer.getCloudsTarget().bindWrite(false);
        }
    }

    public static void cacheFogData(ViewportEvent.RenderFog event) {
        FOG_NEAR = event.getNearPlaneDistance();
        FOG_FAR = event.getFarPlaneDistance();
        FOG_SHAPE = event.getFogShape();
    }

    public static void cacheFogData(ViewportEvent.ComputeFogColor event) {
        FOG_RED = event.getRed();
        FOG_GREEN = event.getGreen();
        FOG_BLUE = event.getBlue();
    }

    public static void beginBufferedRendering() {
        float[] shaderFogColor = RenderSystem.getShaderFogColor();
        float fogRed = shaderFogColor[0];
        float fogGreen = shaderFogColor[1];
        float fogBlue = shaderFogColor[2];
        float shaderFogStart = RenderSystem.getShaderFogStart();
        float shaderFogEnd = RenderSystem.getShaderFogEnd();
        FogShape shaderFogShape = RenderSystem.getShaderFogShape();

        RenderSystem.setShaderFogStart(FOG_NEAR);
        RenderSystem.setShaderFogEnd(FOG_FAR);
        RenderSystem.setShaderFogShape(FOG_SHAPE);
        RenderSystem.setShaderFogColor(FOG_RED, FOG_GREEN, FOG_BLUE);

        FOG_RED = fogRed;
        FOG_GREEN = fogGreen;
        FOG_BLUE = fogBlue;

        FOG_NEAR = shaderFogStart;
        FOG_FAR = shaderFogEnd;
        FOG_SHAPE = shaderFogShape;
    }

    public static void endBufferedRendering() {
        RenderSystem.setShaderFogStart(FOG_NEAR);
        RenderSystem.setShaderFogEnd(FOG_FAR);
        RenderSystem.setShaderFogShape(FOG_SHAPE);
        RenderSystem.setShaderFogColor(FOG_RED, FOG_GREEN, FOG_BLUE);
    }


    public static void renderBufferedParticles(boolean transparentOnly) {
        renderBufferedBatches(DELAYED_PARTICLE_RENDER, PARTICLE_BUFFERS, transparentOnly);
    }

    public static void renderBufferedBatches(boolean transparentOnly) {
        renderBufferedBatches(DELAYED_RENDER, BUFFERS, transparentOnly);
    }

    private static void renderBufferedBatches(MultiBufferSource.BufferSource bufferSource, HashMap<RenderType, BufferBuilder> buffer, boolean transparentOnly) {
        if (transparentOnly) {
            endBatches(bufferSource, TRANSPARENT_RENDER_TYPES);
        } else {
            Collection<RenderType> nonTransparentRenderTypes = new ArrayList<>(buffer.keySet());
            nonTransparentRenderTypes.removeIf(TRANSPARENT_RENDER_TYPES::contains);
            endBatches(bufferSource, nonTransparentRenderTypes);
        }
    }

    public static void endBatches(MultiBufferSource.BufferSource source, Collection<RenderType> renderTypes) {
        for (RenderType type : renderTypes) {
            ShaderInstance instance = RenderHelper.getShader(type);
            if (UNIFORM_HANDLERS.containsKey(type)) {
                ShaderUniformHandler handler = UNIFORM_HANDLERS.get(type);
                handler.updateShaderData(instance);
            }
            source.endBatch(type);
            if (instance instanceof ExtendedShaderInstance extendedShaderInstance) {
                extendedShaderInstance.setUniformDefaults();
            }
        }
    }

    //TODO: offer some actual option here to decide if particle or not
    public static void addRenderType(RenderType renderType) {
        int size = LARGER_BUFFER_SOURCES ? 262144 : renderType.bufferSize();
        final boolean isParticle = renderType.name.contains("particle");
        HashMap<RenderType, BufferBuilder> buffers = isParticle ? PARTICLE_BUFFERS : BUFFERS;
        buffers.put(renderType, new BufferBuilder(size));
        if (NORMAL_TRANSPARENCY.equals(RenderHelper.getTransparencyShard(renderType))) {
            TRANSPARENT_RENDER_TYPES.add(renderType);
        }
    }
}