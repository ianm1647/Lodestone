package team.lodestar.lodestone.registry.client;

import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.systems.*;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.systems.rendering.*;
import team.lodestar.lodestone.systems.rendering.rendeertype.*;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;

import java.util.*;
import java.util.function.*;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;
import static com.mojang.blaze3d.vertex.VertexFormat.Mode.*;

public class LodestoneRenderTypes extends RenderStateShard {

    public static final Runnable TRANSPARENT_FUNCTION = () -> RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

    public static final Runnable ADDITIVE_FUNCTION = () -> RenderSystem.blendFunc(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

    public static final EmptyTextureStateShard NO_TEXTURE = RenderStateShard.NO_TEXTURE;
    public static final LightmapStateShard LIGHTMAP = RenderStateShard.LIGHTMAP;
    public static final LightmapStateShard NO_LIGHTMAP = RenderStateShard.NO_LIGHTMAP;
    public static final CullStateShard CULL = RenderStateShard.CULL;
    public static final CullStateShard NO_CULL = RenderStateShard.NO_CULL;
    public static final WriteMaskStateShard COLOR_DEPTH_WRITE = RenderStateShard.COLOR_DEPTH_WRITE;
    public static final WriteMaskStateShard COLOR_WRITE = RenderStateShard.COLOR_WRITE;

    public LodestoneRenderTypes(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
        super(p_110161_, p_110162_, p_110163_);
    }

    /**
     * Stores many copies of render types, a copy is a new instance of a render type with the same properties.
     * It's useful when we want to apply different uniform changes with each separate use of our render type.
     * Use the {@link #copyAndStore(Object, LodestoneRenderType)} {@link #copy(LodestoneRenderType)} methods to create copies.
     */
    public static final HashMap<Pair<Object, LodestoneRenderType>, LodestoneRenderType> COPIES = new HashMap<>();

    public static final Function<RenderTypeData, LodestoneRenderType> GENERIC = (data) -> createGenericRenderType(data.name, data.format, data.mode, builder().copyState(data.state));

    private static Consumer<LodestoneCompositeStateBuilder> MODIFIER;
    /**
     * Static, one off Render Types. Should be self-explanatory.
     */

    public static final LodestoneRenderType ADDITIVE_PARTICLE = createGenericRenderType("additive_particle", PARTICLE, QUADS,
            builder(TextureAtlas.LOCATION_PARTICLES, StateShards.ADDITIVE_TRANSPARENCY, LodestoneShaders.PARTICLE, NO_CULL, LIGHTMAP, COLOR_WRITE));

    public static final LodestoneRenderType ADDITIVE_BLOCK_PARTICLE = createGenericRenderType("additive_block_particle", PARTICLE, QUADS,
            builder(TextureAtlas.LOCATION_BLOCKS, StateShards.ADDITIVE_TRANSPARENCY, LodestoneShaders.PARTICLE, NO_CULL, LIGHTMAP, COLOR_WRITE));

    public static final LodestoneRenderType ADDITIVE_BLOCK = createGenericRenderType("additive_block",
            builder(TextureAtlas.LOCATION_BLOCKS, StateShards.ADDITIVE_TRANSPARENCY, LodestoneShaders.LODESTONE_TEXTURE, NO_CULL, LIGHTMAP, COLOR_WRITE));

    public static final LodestoneRenderType ADDITIVE_SOLID = createGenericRenderType("additive_solid", POSITION_COLOR_LIGHTMAP, QUADS,
            builder(StateShards.ADDITIVE_TRANSPARENCY, POSITION_COLOR_LIGHTMAP_SHADER, NO_CULL, LIGHTMAP, COLOR_WRITE));

    public static final LodestoneRenderType TRANSPARENT_PARTICLE = createGenericRenderType("transparent_particle", PARTICLE, QUADS,
            builder(TextureAtlas.LOCATION_PARTICLES, StateShards.NORMAL_TRANSPARENCY, LodestoneShaders.PARTICLE, NO_CULL, LIGHTMAP, COLOR_WRITE));

    public static final LodestoneRenderType TRANSPARENT_BLOCK_PARTICLE = createGenericRenderType("transparent_block_particle", PARTICLE, QUADS,
            builder(TextureAtlas.LOCATION_BLOCKS, StateShards.NORMAL_TRANSPARENCY, LodestoneShaders.PARTICLE, NO_CULL, LIGHTMAP, COLOR_WRITE));

    public static final LodestoneRenderType TRANSPARENT_BLOCK = createGenericRenderType("transparent_block",
            builder(TextureAtlas.LOCATION_BLOCKS, StateShards.NORMAL_TRANSPARENCY, LodestoneShaders.LODESTONE_TEXTURE, NO_CULL, LIGHTMAP, COLOR_WRITE));

    public static final LodestoneRenderType TRANSPARENT_SOLID = createGenericRenderType("transparent_solid", POSITION_COLOR_LIGHTMAP, QUADS,
            builder(StateShards.NORMAL_TRANSPARENCY, POSITION_COLOR_LIGHTMAP_SHADER, NO_CULL, LIGHTMAP, COLOR_WRITE));

    public static final LodestoneRenderType LUMITRANSPARENT_PARTICLE = copyWithUniformChanges("lodestone:lumitransparent_particle", TRANSPARENT_PARTICLE, ShaderUniformHandler.LUMITRANSPARENT);
    public static final LodestoneRenderType LUMITRANSPARENT_BLOCK_PARTICLE = copyWithUniformChanges("lodestone:lumitransparent_block_particle", TRANSPARENT_BLOCK_PARTICLE, ShaderUniformHandler.LUMITRANSPARENT);
    public static final LodestoneRenderType LUMITRANSPARENT_BLOCK = copyWithUniformChanges("lodestone:lumitransparent_block", TRANSPARENT_BLOCK, ShaderUniformHandler.LUMITRANSPARENT);
    public static final LodestoneRenderType LUMITRANSPARENT_SOLID = copyWithUniformChanges("lodestone:lumitransparent_solid", TRANSPARENT_SOLID, ShaderUniformHandler.LUMITRANSPARENT);

    /**
     * Render Functions. You can create Render Types by statically applying these to your texture. Alternatively, use {@link #GENERIC} if none of the presets suit your needs.
     * Use {@link RenderTypeProvider#apply(RenderTypeToken)} to create a render type.
     * Render Types are cached using the texture, Shader Uniform Handler, and Render Type Modifier as keys.
     */
    public static final RenderTypeProvider TEXTURE = new RenderTypeProvider((token) ->
            createGenericRenderType("texture",
                    builder(token, StateShards.NO_TRANSPARENCY, LodestoneShaders.LODESTONE_TEXTURE, CULL, LIGHTMAP)));

    public static final RenderTypeProvider CUTOUT_TEXTURE = new RenderTypeProvider((token) ->
            createGenericRenderType("cutout_texture",
                    builder(token, StateShards.NO_TRANSPARENCY, LodestoneShaders.LODESTONE_TEXTURE, CULL, LIGHTMAP)));

    public static final RenderTypeProvider TRANSPARENT_TEXTURE = new RenderTypeProvider((token) ->
            createGenericRenderType("transparent_texture",
                    builder(token, StateShards.NORMAL_TRANSPARENCY, LodestoneShaders.LODESTONE_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));
    public static final RenderTypeProvider TRANSPARENT_DISTORTED_TEXTURE = new RenderTypeProvider((token) ->
            createGenericRenderType("distorted_transparent_texture",
                    builder(token, StateShards.NORMAL_TRANSPARENCY, LodestoneShaders.DISTORTED_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));
    public static final RenderTypeProvider TRANSPARENT_TEXTURE_TRIANGLE = new RenderTypeProvider((token) ->
            createGenericRenderType("transparent_texture_triangle",
                    builder(token, StateShards.NORMAL_TRANSPARENCY, LodestoneShaders.TRIANGLE_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));
    public static final RenderTypeProvider TRANSPARENT_TWO_SIDED_TEXTURE_TRIANGLE = new RenderTypeProvider((token) ->
            createGenericRenderType("transparent_two_sided_texture_triangle",
                    builder(token, StateShards.NORMAL_TRANSPARENCY, LodestoneShaders.TWO_SIDED_TRIANGLE_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));
    public static final RenderTypeProvider TRANSPARENT_ROUNDED_TEXTURE_TRIANGLE = new RenderTypeProvider((token) ->
            createGenericRenderType("transparent_rounded_texture_triangle",
                    builder(token, StateShards.NORMAL_TRANSPARENCY, LodestoneShaders.ROUNDED_TRIANGLE_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));
    public static final RenderTypeProvider TRANSPARENT_SCROLLING_TEXTURE_TRIANGLE = new RenderTypeProvider((token) ->
            createGenericRenderType("transparent_scrolling_texture_triangle",
                    builder(token, StateShards.NORMAL_TRANSPARENCY, LodestoneShaders.SCROLLING_TRIANGLE_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));

    public static final RenderTypeProvider TRANSPARENT_TEXT = new RenderTypeProvider((token) ->
            LodestoneRenderTypes.createGenericRenderType("transparent_text",
                    builder(token, StateShards.NORMAL_TRANSPARENCY, LodestoneShaders.LODESTONE_TEXT, LIGHTMAP)));

    public static final RenderTypeProvider ADDITIVE_TEXTURE = new RenderTypeProvider((token) ->
            createGenericRenderType("additive_texture",
                    builder(token, StateShards.ADDITIVE_TRANSPARENCY, LodestoneShaders.LODESTONE_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));
    public static final RenderTypeProvider ADDITIVE_DISTORTED_TEXTURE = new RenderTypeProvider((token) ->
            createGenericRenderType("distorted_additive_texture",
                    builder(token, StateShards.ADDITIVE_TRANSPARENCY, LodestoneShaders.DISTORTED_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));
    public static final RenderTypeProvider ADDITIVE_TEXTURE_TRIANGLE = new RenderTypeProvider((token) ->
            createGenericRenderType("additive_texture_triangle",
                    builder(token, StateShards.ADDITIVE_TRANSPARENCY, LodestoneShaders.TRIANGLE_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));
    public static final RenderTypeProvider ADDITIVE_TWO_SIDED_TEXTURE_TRIANGLE = new RenderTypeProvider((token) ->
            createGenericRenderType("additive_two_sided_texture_triangle",
                    builder(token, StateShards.ADDITIVE_TRANSPARENCY, LodestoneShaders.TWO_SIDED_TRIANGLE_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));
    public static final RenderTypeProvider ADDITIVE_ROUNDED_TEXTURE_TRIANGLE = new RenderTypeProvider((token) ->
            createGenericRenderType("additive_rounded_texture_triangle",
                    builder(token, StateShards.ADDITIVE_TRANSPARENCY, LodestoneShaders.ROUNDED_TRIANGLE_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));
    public static final RenderTypeProvider ADDITIVE_SCROLLING_TEXTURE_TRIANGLE = new RenderTypeProvider((token) ->
            createGenericRenderType("additive_scrolling_texture_triangle",
                    builder(token, StateShards.ADDITIVE_TRANSPARENCY, LodestoneShaders.SCROLLING_TRIANGLE_TEXTURE, CULL, LIGHTMAP, COLOR_WRITE)));

    public static final RenderTypeProvider ADDITIVE_TEXT = new RenderTypeProvider((token) ->
            LodestoneRenderTypes.createGenericRenderType("additive_text",
                    builder(token, StateShards.ADDITIVE_TRANSPARENCY, LodestoneShaders.LODESTONE_TEXT, LIGHTMAP, COLOR_WRITE)));


    public static LodestoneRenderType createGenericRenderType(String name, LodestoneCompositeStateBuilder builder) {
        return createGenericRenderType(name, POSITION_COLOR_TEX_LIGHTMAP, QUADS, builder);
    }
    public static LodestoneRenderType createGenericRenderType(String name, VertexFormat format, VertexFormat.Mode mode, LodestoneCompositeStateBuilder builder) {
        return createGenericRenderType(name, format, mode, builder, null);
    }
    /**
     * Creates a custom render type and creates a buffer builder for it.
     */
    public static LodestoneRenderType createGenericRenderType(String name, VertexFormat format, VertexFormat.Mode mode, LodestoneCompositeStateBuilder builder, ShaderUniformHandler handler) {
        if (MODIFIER != null) {
            MODIFIER.accept(builder);
        }
        LodestoneRenderType type = LodestoneRenderType.createRenderType(name, format, builder.modeOverride != null ? builder.modeOverride : mode, 256, false, true, builder.createCompositeState(true));
        RenderHandler.addRenderType(type);
        if (handler != null) {
            applyUniformChanges(type, handler);
        }
        MODIFIER = null;
        return type;
    }

    public static LodestoneRenderType copyWithUniformChanges(LodestoneRenderType type, ShaderUniformHandler handler) {
        return applyUniformChanges(copy(type), handler);
    }

    public static LodestoneRenderType copyWithUniformChanges(String newName, LodestoneRenderType type, ShaderUniformHandler handler) {
        return applyUniformChanges(copy(newName, type), handler);
    }

    /**
     * Queues shader uniform changes for a render type. When we end batches in {@link RenderHandler}}, we do so one render type at a time.
     * Prior to ending a batch, we run {@link ShaderUniformHandler#updateShaderData(ShaderInstance)} if one is present for a given render type.
     */
    public static LodestoneRenderType applyUniformChanges(LodestoneRenderType type, ShaderUniformHandler handler) {
        RenderHandler.UNIFORM_HANDLERS.put(type, handler);
        return type;
    }

    /**
     * Creates a copy of a render type.
     */
    public static LodestoneRenderType copy(LodestoneRenderType type) {
        return GENERIC.apply(new RenderTypeData(type));
    }

    public static LodestoneRenderType copy(String newName, LodestoneRenderType type) {
        return GENERIC.apply(new RenderTypeData(newName, type));
    }

    /**
     * Creates a copy of a render type and stores it in the {@link #COPIES} hashmap, with the key being a pair of original render type and copy index.
     */
    public static LodestoneRenderType copyAndStore(Object index, LodestoneRenderType type) {
        return COPIES.computeIfAbsent(Pair.of(index, type), (p) -> GENERIC.apply(new RenderTypeData(type)));
    }
    public static LodestoneRenderType copyAndStore(Object index, LodestoneRenderType type, ShaderUniformHandler handler) {
        return applyUniformChanges(copyAndStore(index, type), handler);
    }

    public static void addRenderTypeModifier(Consumer<LodestoneCompositeStateBuilder> modifier) {
        MODIFIER = modifier;
    }

    public static LodestoneCompositeStateBuilder builder(Object... objects) {
        return builder().setStateShards(objects);
    }
    public static LodestoneCompositeStateBuilder builder() {
        return new LodestoneCompositeStateBuilder();
    }

    public static class LodestoneCompositeStateBuilder extends RenderType.CompositeState.CompositeStateBuilder {

        protected VertexFormat.Mode modeOverride;
        LodestoneCompositeStateBuilder() {
            super();
        }

        public LodestoneCompositeStateBuilder replaceVertexFormat(VertexFormat.Mode mode) {
            this.modeOverride = mode;
            return this;
        }

        public LodestoneCompositeStateBuilder copyState(RenderType.CompositeState state) {
            for (RenderStateShard renderStateShard : state.states) {
                setStateShards(renderStateShard);
            }
            return this;
        }

        public LodestoneCompositeStateBuilder setStateShards(Object... objects) {
            for (Object object : objects) {
                if (object instanceof ResourceLocation texture) {
                    setTextureState(texture);
                }
                else if (object instanceof RenderTypeToken token) {
                    setTextureState(token.get());
                }
                else if (object instanceof RenderStateShard.EmptyTextureStateShard shard) {
                    setTextureState(shard);
                }
                else if (object instanceof ShaderHolder shaderHolder) {
                    setShaderState(shaderHolder);
                }
                else if (object instanceof RenderStateShard.ShaderStateShard shard) {
                    setShaderState(shard);
                }
                else if (object instanceof RenderStateShard.TransparencyStateShard shard) {
                    setTransparencyState(shard);
                }
                else if (object instanceof RenderStateShard.DepthTestStateShard shard) {
                    setDepthTestState(shard);
                }
                else if (object instanceof RenderStateShard.CullStateShard shard) {
                    setCullState(shard);
                }
                else if (object instanceof RenderStateShard.LightmapStateShard shard) {
                    setLightmapState(shard);
                }
                else if (object instanceof RenderStateShard.OverlayStateShard shard) {
                    setOverlayState(shard);
                }
                else if (object instanceof RenderStateShard.LayeringStateShard shard) {
                    setLayeringState(shard);
                }
                else if (object instanceof RenderStateShard.OutputStateShard shard) {
                    setOutputState(shard);
                }
                else if (object instanceof RenderStateShard.TexturingStateShard shard) {
                    setTexturingState(shard);
                }
                else if (object instanceof RenderStateShard.WriteMaskStateShard shard) {
                    setWriteMaskState(shard);
                }
                else if (object instanceof RenderStateShard.LineStateShard shard) {
                    setLineState(shard);
                }
                else if (object instanceof RenderStateShard.ColorLogicStateShard shard) {
                    setColorLogicState(shard);
                }
            }
            return this;
        }

        public LodestoneCompositeStateBuilder setTextureState(ResourceLocation texture) {
            return setTextureState(new RenderStateShard.TextureStateShard(texture, false, false));
        }

        public LodestoneCompositeStateBuilder setShaderState(ShaderHolder shaderHolder) {
            return setShaderState(shaderHolder.getShard());
        }

        @Override
        public LodestoneCompositeStateBuilder setTextureState(EmptyTextureStateShard pTextureState) {
            return (LodestoneCompositeStateBuilder) super.setTextureState(pTextureState);
        }

        @Override
        public LodestoneCompositeStateBuilder setShaderState(ShaderStateShard pShaderState) {
            return (LodestoneCompositeStateBuilder) super.setShaderState(pShaderState);
        }

        @Override
        public LodestoneCompositeStateBuilder setTransparencyState(TransparencyStateShard pTransparencyState) {
            return (LodestoneCompositeStateBuilder) super.setTransparencyState(pTransparencyState);
        }

        @Override
        public LodestoneCompositeStateBuilder setDepthTestState(DepthTestStateShard pDepthTestState) {
            return (LodestoneCompositeStateBuilder) super.setDepthTestState(pDepthTestState);
        }

        @Override
        public LodestoneCompositeStateBuilder setCullState(CullStateShard pCullState) {
            return (LodestoneCompositeStateBuilder) super.setCullState(pCullState);
        }

        @Override
        public LodestoneCompositeStateBuilder setLightmapState(LightmapStateShard pLightmapState) {
            return (LodestoneCompositeStateBuilder) super.setLightmapState(pLightmapState);
        }

        @Override
        public LodestoneCompositeStateBuilder setOverlayState(OverlayStateShard pOverlayState) {
            return (LodestoneCompositeStateBuilder) super.setOverlayState(pOverlayState);
        }

        @Override
        public LodestoneCompositeStateBuilder setLayeringState(LayeringStateShard pLayerState) {
            return (LodestoneCompositeStateBuilder) super.setLayeringState(pLayerState);
        }

        @Override
        public LodestoneCompositeStateBuilder setOutputState(OutputStateShard pOutputState) {
            return (LodestoneCompositeStateBuilder) super.setOutputState(pOutputState);
        }

        @Override
        public LodestoneCompositeStateBuilder setTexturingState(TexturingStateShard pTexturingState) {
            return (LodestoneCompositeStateBuilder) super.setTexturingState(pTexturingState);
        }

        @Override
        public LodestoneCompositeStateBuilder setWriteMaskState(WriteMaskStateShard pWriteMaskState) {
            return (LodestoneCompositeStateBuilder) super.setWriteMaskState(pWriteMaskState);
        }

        @Override
        public LodestoneCompositeStateBuilder setLineState(LineStateShard pLineState) {
            return (LodestoneCompositeStateBuilder) super.setLineState(pLineState);
        }
    }
}