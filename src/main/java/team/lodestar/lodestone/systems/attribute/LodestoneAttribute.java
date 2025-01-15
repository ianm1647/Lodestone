package team.lodestar.lodestone.systems.attribute;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class LodestoneAttribute extends Attribute {
    public final ResourceLocation id;

    protected LodestoneAttribute(ResourceLocation id, double defaultValue) {
        super("attribute.name." + id.getNamespace() + "." + id.getPath(), defaultValue);
        this.id = id;
    }
}
