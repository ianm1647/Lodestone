package team.lodestar.lodestone.systems.datagen.statesmith;

import net.minecraft.world.level.block.Block;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.datagen.ItemModelSmithTypes;
import team.lodestar.lodestone.systems.datagen.itemsmith.ItemModelSmith;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneBlockStateProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class BlockStateSmith<T extends Block> extends AbstractBlockStateSmith<T> {

    public final SmithStateSupplier<T> stateSupplier;
    public final ItemModelSmith itemModelSmith;

    public BlockStateSmith(Class<T> blockClass, SmithStateSupplier<T> stateSupplier) {
        this(blockClass, ItemModelSmithTypes.BLOCK_MODEL_ITEM, stateSupplier);
    }

    public BlockStateSmith(Class<T> blockClass, ItemModelSmith itemModelSmith, SmithStateSupplier<T> stateSupplier) {
        super(blockClass);
        this.stateSupplier = stateSupplier;
        this.itemModelSmith = itemModelSmith;
    }

    @SafeVarargs
    public final void act(StateSmithData data, Supplier<? extends Block>... blocks) {
        for (Supplier<? extends Block> block : blocks) {
            act(data, block);
        }
        List.of(blocks).forEach(data.consumer);
    }

    public void act(StateSmithData data, Collection<Supplier<? extends Block>> blocks) {
        blocks.forEach(r -> act(data, r));
        new ArrayList<>(blocks).forEach(data.consumer);
    }

    private void act(StateSmithData data, Supplier<? extends Block> registryObject) {
        Block block = registryObject.get();
        if (blockClass.isInstance(block)) {
            stateSupplier.act(blockClass.cast(block), data.provider);
            if (!itemModelSmith.equals(ItemModelSmithTypes.NO_DATAGEN)) {
                itemModelSmith.act(data.provider.itemModelProvider, block::asItem);
            }
        } else {
            LodestoneLib.LOGGER.warn("Block does not match the state smith it was assigned: {}", registryObject.get().toString());
        }
    }

    public interface SmithStateSupplier<T extends Block> {
        void act(T block, LodestoneBlockStateProvider provider);
    }
}