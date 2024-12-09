package team.lodestar.lodestone.registry.common;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import team.lodestar.lodestone.LodestoneLib;

public class LodestoneAttributes {

    public static final Holder<Attribute> MAGIC_RESISTANCE = register("magic_resistance", new RangedAttribute("lodestone.magic_resistance", 1.0D, 0.0D, 2048.0D).setSyncable(true));
    public static final Holder<Attribute> MAGIC_PROFICIENCY = register("magic_proficiency", new RangedAttribute("lodestone.magic_proficiency", 1.0D, 0.0D, 2048.0D).setSyncable(true));
    public static final Holder<Attribute> MAGIC_DAMAGE = register("magic_damage", new RangedAttribute("lodestone.magic_damage", 0.0D, 0.0D, 2048.0D).setSyncable(true));

    public static Holder<Attribute> register(String id, Attribute attribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, LodestoneLib.lodestonePath(id), attribute);
    }

    public static void init() {
    }
}