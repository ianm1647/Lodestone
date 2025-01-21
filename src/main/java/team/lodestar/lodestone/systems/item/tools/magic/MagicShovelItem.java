package team.lodestar.lodestone.systems.item.tools.magic;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.registry.common.LodestoneAttributes;
import team.lodestar.lodestone.systems.item.*;
import team.lodestar.lodestone.systems.item.tools.LodestoneShovelItem;

import java.util.ArrayList;
import java.util.List;

public class MagicShovelItem extends LodestoneShovelItem {

    public MagicShovelItem(Tier tier, float attackDamage, float attackSpeed, float magicDamage, LodestoneItemProperties properties) {
        super(tier, attackDamage, attackSpeed, properties);
        properties.mergeAttributes(
                ItemAttributeModifiers.builder()
                        .add(LodestoneAttributes.MAGIC_DAMAGE, new AttributeModifier(LodestoneAttributes.MAGIC_DAMAGE.getId(), magicDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                        .build());
    }
}