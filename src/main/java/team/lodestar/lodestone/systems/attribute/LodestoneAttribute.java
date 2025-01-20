package team.lodestar.lodestone.systems.attribute;

import net.minecraft.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;

public class LodestoneAttribute extends Attribute {
    private final ResourceLocation id;
    private final boolean isBase;
    private final boolean forcePercentage;

    public static LodestoneAttributeBuilder create(ResourceLocation id, double defaultValue) {
        return new LodestoneAttributeBuilder(id, defaultValue);
    }
    protected LodestoneAttribute(ResourceLocation id, double defaultValue, boolean isBase, boolean forcePercentage) {
        super("attribute.name." + id.getNamespace() + "." + id.getPath(), defaultValue);
        this.id = id;
        this.isBase = isBase;
        this.forcePercentage = forcePercentage;
    }

    @Override
    @Nullable
    public ResourceLocation getBaseId() {
        return isBase ? id : null;
    }

    @Override
    public MutableComponent toValueComponent(@Nullable AttributeModifier.Operation op, double value, TooltipFlag flag) {
        if (forcePercentage) {
            return Component.translatable("neoforge.value.percent", FORMAT.format(value * 100));
        }
        return Component.translatable("neoforge.value.flat", FORMAT.format(value));
    }

}