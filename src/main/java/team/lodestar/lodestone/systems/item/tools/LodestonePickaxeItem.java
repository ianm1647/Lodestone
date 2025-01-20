package team.lodestar.lodestone.systems.item.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.ArrayList;
import java.util.List;

public class LodestonePickaxeItem extends PickaxeItem {
    private ItemAttributeModifiers attributes;

    public LodestonePickaxeItem(Tier material, int damage, float speed, Properties properties) {
        super(material, properties.durability(material.getUses()).attributes(createAttributes(material, damage + 1, speed - 2.8f)));
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        if (attributes == null) {
            var builder  = createExtraAttributes();
            ItemAttributeModifiers modifiers = super.getDefaultAttributeModifiers(stack);
            List<ItemAttributeModifiers.Entry> entries = modifiers.modifiers();
            for (ItemAttributeModifiers.Entry entry : entries) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
            attributes = builder.build();
        }

        return attributes;
    }

    public ItemAttributeModifiers.Builder createExtraAttributes() {
        return ItemAttributeModifiers.builder();
    }

}

