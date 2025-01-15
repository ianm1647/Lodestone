package team.lodestar.lodestone.systems.rendering.rendeertype;

import net.minecraft.client.renderer.*;
import net.minecraft.resources.*;

import java.util.*;
import java.util.function.*;

public class RenderTypeToken implements Supplier<RenderStateShard.EmptyTextureStateShard> {

    private static final HashMap<ResourceLocation, RenderTypeToken> CACHED_TEXTURE_TOKENS = new HashMap<>();
    private static final HashMap<RenderStateShard.EmptyTextureStateShard, RenderTypeToken> CACHED_STATE_TOKENS = new HashMap<>();

    private final UUID identifier;
    private final RenderStateShard.EmptyTextureStateShard texture;

    protected RenderTypeToken(ResourceLocation texture) {
        this(new RenderStateShard.TextureStateShard(texture, false, false));
    }

    protected RenderTypeToken(RenderStateShard.EmptyTextureStateShard texture) {
        this.identifier = UUID.randomUUID();
        this.texture = texture;
    }

    public static RenderTypeToken createToken(ResourceLocation texture) {
        return CACHED_TEXTURE_TOKENS.computeIfAbsent(texture, RenderTypeToken::new);
    }

    public static RenderTypeToken createToken(RenderStateShard.EmptyTextureStateShard texture) {
        return CACHED_STATE_TOKENS.computeIfAbsent(texture, RenderTypeToken::new);
    }

    @Deprecated(forRemoval = true)
    public static RenderTypeToken createCachedToken(ResourceLocation texture) {
        return createToken(texture);
    }

    @Deprecated(forRemoval = true)
    public static RenderTypeToken createCachedToken(RenderStateShard.EmptyTextureStateShard texture) {
        return createToken(texture);
    }

    @Override
    public RenderStateShard.EmptyTextureStateShard get() {
        return texture;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, texture);
    }
}