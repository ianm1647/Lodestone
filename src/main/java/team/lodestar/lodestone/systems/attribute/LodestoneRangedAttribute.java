package team.lodestar.lodestone.systems.attribute;

import net.minecraft.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;

import javax.annotation.*;

public class LodestoneRangedAttribute extends RangedAttribute {
    private final ResourceLocation id;
    private final boolean isBase;
    private final boolean forcePercentage;

    public static LodestoneAttributeBuilder create(ResourceLocation id, double defaultValue, double min, double max) {
        return new LodestoneAttributeBuilder(id, defaultValue, min, max);
    }
    protected LodestoneRangedAttribute(ResourceLocation id, double defaultValue, double min, double max, boolean isBase, boolean forcePercentage) {
        super("attribute.name." + id.getNamespace() + "." + id.getPath(), defaultValue, min, max);
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
        return super.toValueComponent(op, value, flag);
    }

}