package team.lodestar.lodestone.systems.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.systems.block.LodestoneEntityBlock;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * A simple block entity with various methods normally found inside of Block delegated here from {@link LodestoneEntityBlock}
 */
public class LodestoneBlockEntity extends BlockEntity {

    private final Collection<Consumer<Level>> loadWithLevel = new ArrayList<>();

    public LodestoneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return this.saveWithoutMetadata(pRegistries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        handleUpdateTag(getUpdatePacket().getTag(), lookupProvider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadWithLevel(this::update);
    }

    public void onBreak(@Nullable Player player) {
    }

    public void onPlace(LivingEntity placer, ItemStack stack) {
    }

    public void onNeighborUpdate(BlockState state, BlockPos pos, BlockPos neighbor) {
    }

    public ItemStack onClone(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return ItemStack.EMPTY;
    }

    public ItemInteractionResult onUse(Player pPlayer, InteractionHand pHand) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public InteractionResult onUseWithoutItem(Player pPlayer) {
        return InteractionResult.PASS;
    }

    public ItemInteractionResult onUseWithItem(Player pPlayer, ItemStack pStack, InteractionHand pHand) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void onEntityInside(BlockState state, Level level, BlockPos pos, Entity entity) {

    }

    /**
     * Runs after the block entity is loaded from memory or synced to the client.
     * Effectively the same as loadAdditional but runs the next tick and with a valid level.
     */
    public void update(@Nonnull Level level) {

    }

    public void tick() {
    }

    /**
     * Call from {@link LodestoneBlockEntity#loadAdditional(CompoundTag, HolderLookup.Provider)} for any network syncing that requires a non-null level
     */
    public void loadWithLevel(Consumer<Level> levelConsumer) {
        loadWithLevel.add(levelConsumer);
    }

    public final void triggerLevelConsumers() {
        if (level != null && !loadWithLevel.isEmpty()) {
            for (Consumer<Level> levelConsumer : loadWithLevel) {
                levelConsumer.accept(level);
            }
            loadWithLevel.clear();
        }
    }
}