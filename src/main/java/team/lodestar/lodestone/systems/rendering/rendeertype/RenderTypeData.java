package team.lodestar.lodestone.systems.rendering.rendeertype;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import team.lodestar.lodestone.registry.client.*;
import team.lodestar.lodestone.systems.rendering.*;

/**
 * Stores all relevant data from a RenderType.
 */
public class RenderTypeData {
    public final String name;
    public final VertexFormat format;
    public final VertexFormat.Mode mode;
    public final RenderType.CompositeState state;

    public RenderTypeData(String name, VertexFormat format, VertexFormat.Mode mode, RenderType.CompositeState state) {
        this.name = name;
        this.format = format;
        this.mode = mode;
        this.state = state;
    }

    public RenderTypeData(String name, LodestoneRenderType type) {
        this(name, type.format(), type.mode(), type.state);
    }

    public RenderTypeData(LodestoneRenderType type) {
        this(type.name, type);
    }
}
