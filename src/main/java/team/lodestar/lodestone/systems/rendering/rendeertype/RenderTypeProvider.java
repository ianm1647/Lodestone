package team.lodestar.lodestone.systems.rendering.rendeertype;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry.*;
import team.lodestar.lodestone.systems.rendering.*;

import java.util.function.*;

public class RenderTypeProvider {
    private final Function<RenderTypeToken, LodestoneRenderType> function;
    private final Function<RenderTypeToken, LodestoneRenderType> memorizedFunction;

    public RenderTypeProvider(Function<RenderTypeToken, LodestoneRenderType> function) {
        this.function = function;
        this.memorizedFunction = Util.memoize(function);
    }

    public LodestoneRenderType apply(RenderTypeToken texture) {
        return function.apply(texture);
    }

    public LodestoneRenderType apply(RenderTypeToken texture, ShaderUniformHandler uniformHandler) {
        return LodestoneRenderTypeRegistry.applyUniformChanges(function.apply(texture), uniformHandler);
    }

    public LodestoneRenderType applyAndCache(RenderTypeToken texture) {
        return this.memorizedFunction.apply(texture);
    }

    public LodestoneRenderType applyAndCache(RenderTypeToken texture, ShaderUniformHandler uniformHandler) {
        return LodestoneRenderTypeRegistry.applyUniformChanges(this.memorizedFunction.apply(texture), uniformHandler);
    }

    public LodestoneRenderType applyWithModifier(RenderTypeToken texture, Consumer<LodestoneCompositeStateBuilder> modifier) {
        LodestoneRenderTypeRegistry.addRenderTypeModifier(modifier);
        return apply(texture);
    }

    public LodestoneRenderType applyWithModifier(RenderTypeToken texture, ShaderUniformHandler uniformHandler, Consumer<LodestoneCompositeStateBuilder> modifier) {
        LodestoneRenderTypeRegistry.addRenderTypeModifier(modifier);
        return apply(texture, uniformHandler);
    }

    public LodestoneRenderType applyWithModifierAndCache(RenderTypeToken texture, Consumer<LodestoneCompositeStateBuilder> modifier) {
        LodestoneRenderTypeRegistry.addRenderTypeModifier(modifier);
        return applyAndCache(texture);
    }

    public LodestoneRenderType applyWithModifierAndCache(RenderTypeToken texture, ShaderUniformHandler uniformHandler, Consumer<LodestoneCompositeStateBuilder> modifier) {
        LodestoneRenderTypeRegistry.addRenderTypeModifier(modifier);
        return LodestoneRenderTypeRegistry.applyUniformChanges(applyAndCache(texture), uniformHandler);
    }
}
