package team.lodestar.lodestone.systems.rendering.rendeertype;

import net.minecraft.Util;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypes;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypes.*;
import team.lodestar.lodestone.systems.rendering.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

public class RenderTypeProvider {
    private final Function<RenderTypeToken, LodestoneRenderType> function;
    private final Function<RenderTypeToken, LodestoneRenderType> simpleCache;
    private final ConcurrentHashMap<RenderTypeProviderKey, LodestoneRenderType> complexCache = new ConcurrentHashMap<>();

    public RenderTypeProvider(Function<RenderTypeToken, LodestoneRenderType> function) {
        this.function = function;
        this.simpleCache = Util.memoize(function);
    }

    private LodestoneRenderType createRenderType(RenderTypeToken token) {
        return simpleCache.apply(token);
    }
    private LodestoneRenderType createRenderType(RenderTypeProviderKey key) {
        if (complexCache.containsKey(key)) {
            return complexCache.get(key);
        }
        if (key.modifier() != null) {
            LodestoneRenderTypes.addRenderTypeModifier(key.modifier());
        }
        LodestoneRenderType renderType = function.apply(key.token());
        if (key.uniformHandler() != null) {
            LodestoneRenderTypes.applyUniformChanges(renderType, key.uniformHandler());
        }
        complexCache.put(key, renderType);
        return renderType;
    }

    public LodestoneRenderType apply(RenderTypeToken token) {
        return createRenderType(token);
    }

    public LodestoneRenderType apply(RenderTypeToken token, ShaderUniformHandler uniformHandler) {
        return createRenderType(new RenderTypeProviderKey(token, uniformHandler));
    }

    public LodestoneRenderType apply(RenderTypeToken token, Consumer<LodestoneCompositeStateBuilder> modifier) {
        return createRenderType(new RenderTypeProviderKey(token, modifier));
    }

    public LodestoneRenderType apply(RenderTypeToken token, ShaderUniformHandler uniformHandler, Consumer<LodestoneCompositeStateBuilder> modifier) {
        return createRenderType(new RenderTypeProviderKey(token, uniformHandler, modifier));
    }

    @Deprecated(forRemoval = true)
    public LodestoneRenderType applyAndCache(RenderTypeToken token) {
        return apply(token);
    }

    @Deprecated(forRemoval = true)
    public LodestoneRenderType applyAndCache(RenderTypeToken token, ShaderUniformHandler uniformHandler) {
        return apply(token, uniformHandler);
    }

    @Deprecated(forRemoval = true)
    public LodestoneRenderType applyWithModifier(RenderTypeToken token, Consumer<LodestoneCompositeStateBuilder> modifier) {
        return apply(token, modifier);
    }

    @Deprecated(forRemoval = true)
    public LodestoneRenderType applyWithModifier(RenderTypeToken token, ShaderUniformHandler uniformHandler, Consumer<LodestoneCompositeStateBuilder> modifier) {
        return apply(token, uniformHandler, modifier);
    }

    @Deprecated(forRemoval = true)
    public LodestoneRenderType applyWithModifierAndCache(RenderTypeToken token, Consumer<LodestoneCompositeStateBuilder> modifier) {
        return apply(token, modifier);
    }

    @Deprecated(forRemoval = true)
    public LodestoneRenderType applyWithModifierAndCache(RenderTypeToken token, ShaderUniformHandler uniformHandler, Consumer<LodestoneCompositeStateBuilder> modifier) {
        return apply(token, uniformHandler, modifier);
    }

    public record RenderTypeProviderKey(RenderTypeToken token, ShaderUniformHandler uniformHandler,
                                        Consumer<LodestoneCompositeStateBuilder> modifier) {

        public RenderTypeProviderKey(RenderTypeToken token) {
            this(token, null, null);
        }

        public RenderTypeProviderKey(RenderTypeToken token, ShaderUniformHandler uniformHandler) {
            this(token, uniformHandler, null);
        }

        public RenderTypeProviderKey(RenderTypeToken token, Consumer<LodestoneCompositeStateBuilder> modifier) {
            this(token, null, modifier);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RenderTypeProviderKey that = (RenderTypeProviderKey) o;
            return Objects.equals(token, that.token) && Objects.equals(uniformHandler, that.uniformHandler) && Objects.equals(modifier, that.modifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(token, uniformHandler, modifier);
        }
    }
}