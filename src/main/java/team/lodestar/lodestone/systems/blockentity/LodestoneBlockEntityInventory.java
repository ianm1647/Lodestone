package team.lodestar.lodestone.systems.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import team.lodestar.lodestone.helpers.block.BlockStateHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A powerful ItemStackHandler designed to work with block entities
 */
public class LodestoneBlockEntityInventory extends ItemStackHandler {
    public LodestoneBlockEntity blockEntity;
    public final int slotCount;
    public final int allowedItemSize;
    public Predicate<ItemStack> inputPredicate;
    public boolean autoSync;

    public ArrayList<ItemStack> nonEmptyItemStacks = new ArrayList<>();
    public int emptyItemAmount;
    public int nonEmptyItemAmount;
    public int firstEmptyItemIndex;

    public LodestoneBlockEntityInventory(LodestoneBlockEntity blockEntity, int slotCount, int allowedItemSize) {
        super(slotCount);
        this.blockEntity = blockEntity;
        this.slotCount = slotCount;
        this.allowedItemSize = allowedItemSize;
    }

    public LodestoneBlockEntityInventory setInputPredicate(Predicate<ItemStack> inputPredicate) {
        this.inputPredicate = inputPredicate;
        return this;
    }

    public LodestoneBlockEntityInventory triggerBlockEntityUpdate() {
        this.autoSync = true;
        return this;
    }

    @Override
    public void onContentsChanged(int slot) {
        updateInventoryCaches();
        if (autoSync) {
            BlockStateHelper.updateState(blockEntity.getLevel(), blockEntity.getBlockPos());
        }
    }

    @Override
    public int getSlots() {
        return slotCount;
    }

    @Override
    public int getSlotLimit(int slot) {
        return allowedItemSize;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (!inputPredicate.test(stack)) {
            return false;
        }
        return super.isItemValid(slot, stack);
    }

    public void updateInventoryCaches() {
        NonNullList<ItemStack> stacks = getStacks();
        nonEmptyItemStacks = stacks.stream().filter(s -> !s.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
        nonEmptyItemAmount = nonEmptyItemStacks.size();
        emptyItemAmount = (int) stacks.stream().filter(ItemStack::isEmpty).count();
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (stack.isEmpty()) {
                firstEmptyItemIndex = i;
                return;
            }
        }
        firstEmptyItemIndex = -1;
    }

    public void load(HolderLookup.Provider provider, CompoundTag compound) {
        load(provider, compound, "inventory");
    }

    public void load(HolderLookup.Provider provider, CompoundTag compound, String name) {
        deserializeNBT(provider, compound.getCompound(name));
        if (stacks.size() != slotCount) {
            int missing = slotCount - stacks.size();
            for (int i = 0; i < missing; i++) {
                stacks.add(ItemStack.EMPTY);
            }
        }
        updateInventoryCaches();
    }

    public void save(HolderLookup.Provider provider, CompoundTag compound) {
        save(provider, compound, "inventory");
    }

    public void save(HolderLookup.Provider provider, CompoundTag compound, String name) {
        compound.put(name, serializeNBT(provider));
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    public boolean isEmpty() {
        return nonEmptyItemAmount == 0;
    }

    public void clear() {
        for (int i = 0; i < slotCount; i++) {
            setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public void dumpItems(Level level, BlockPos pos) {
        dumpItems(level, pos.getCenter());
    }

    public void dumpItems(Level level, Vec3 pos) {
        for (int i = 0; i < slotCount; i++) {
            if (!getStackInSlot(i).isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, pos.x(), pos.y(), pos.z(), getStackInSlot(i)));
            }
            setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public ItemStack interact(ServerLevel level, Player player, InteractionHand handIn) {
        updateInventoryCaches();
        var heldStack = player.getItemInHand(handIn);
        int size = nonEmptyItemStacks.size() - 1;
        if ((heldStack.isEmpty() || firstEmptyItemIndex == -1) && size > 1) {
            var takeOutStack = nonEmptyItemStacks.get(size);
            if (takeOutStack.is(heldStack.getItem())) {
                return insertItem(player, heldStack);
            }
            var extractedStack = extractItem(level, heldStack, player);
            if (!extractedStack.isEmpty()) {
                insertItem(player, heldStack);
            }
            return extractedStack;
        } else {
            return insertItem(player, heldStack);
        }
    }

    public ItemStack extractItem(ServerLevel level, ItemStack heldStack, Player player) {
        if (!level.isClientSide) {
            List<ItemStack> nonEmptyStacks = this.nonEmptyItemStacks;
            if (nonEmptyStacks.isEmpty()) {
                return heldStack;
            }
            ItemStack takeOutStack = nonEmptyStacks.getLast();
            int slot = stacks.indexOf(takeOutStack);
            if (extractItem(slot, takeOutStack.getCount(), true).equals(ItemStack.EMPTY)) {
                return heldStack;
            }
            extractItem(player, takeOutStack, slot);
            return takeOutStack;
        }
        return ItemStack.EMPTY;
    }

    public void extractItem(Player playerEntity, ItemStack stack, int slot) {
        ItemHandlerHelper.giveItemToPlayer(playerEntity, stack);
        setStackInSlot(slot, ItemStack.EMPTY);
    }

    public ItemStack insertItem(Player playerEntity, ItemStack stack) {
        return insertItem(stack);
    }
    public ItemStack insertItem(ItemStack stack) {
        if (!stack.isEmpty()) {
            ItemStack simulate = insertItem(stack, true);
            if (simulate.equals(stack)) {
                return ItemStack.EMPTY;
            }
            int count = stack.getCount() - simulate.getCount();
            if (count > allowedItemSize) {
                count = allowedItemSize;
            }
            ItemStack input = stack.split(count);
            insertItem(input, false);
            return input;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack insertItem(ItemStack stack, boolean simulate) {
        return ItemHandlerHelper.insertItem(this, stack, simulate);
    }
}