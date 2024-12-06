package team.lodestar.lodestone.registry.common.tag;

import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.tags.*;
import net.minecraft.world.damagesource.*;

public class LodestoneDamageTypeTags {

    public static final TagKey<DamageType> IS_MAGIC = cTag("is_magic");
    public static final TagKey<DamageType> CAN_TRIGGER_MAGIC = cTag("can_trigger_magic_damage");
    public static final TagKey<DamageType> IGNORES_MAGIC_ATTACK_COOLDOWN_SCALAR = cTag("ignores_magic_attack_cooldown_scalar");

    public static TagKey<DamageType> cTag(String path) {
        return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("c", path));
    }
}