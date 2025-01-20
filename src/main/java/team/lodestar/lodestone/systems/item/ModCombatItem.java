package team.lodestar.lodestone.systems.item;

import net.minecraft.core.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

/**
 * A simple copy of a sword, without actually being a sword.
 * Minecraft has some hardcoded instanceof SwordItem checks, which we use this to avoid.
 */

public class ModCombatItem extends TieredItem {
    private final float attackDamage;
    private final float attackSpeed;
    private ItemAttributeModifiers attributes;

    public ModCombatItem(Tier tier, float attackDamage, float attackSpeed, Properties builderIn) {
        super(tier, builderIn);
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        if (attributes == null) {
            attributes = createExtraAttributes()
                    .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                    .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                    .build();
        }

        return attributes;
    }

    public ItemAttributeModifiers.Builder createExtraAttributes() {
        return ItemAttributeModifiers.builder();
    }

    public float getDamage() {
        return this.attackDamage;
    }

    public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player p_43294_) {
        return !p_43294_.isCreative();
    }

    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if (pState.is(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            return pState.is(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
        }
    }

    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, EquipmentSlot.MAINHAND);
        return true;
    }

    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntity) {
        if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
            pStack.hurtAndBreak(2, pEntity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    public boolean isCorrectToolForDrops(BlockState p_43298_) {
        return p_43298_.is(Blocks.COBWEB);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return itemAbility.equals(ItemAbilities.SWORD_DIG);
    }
}