package team.lodestar.lodestone.systems.datagen.itemsmith;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneItemModelProvider;

import java.util.function.Supplier;

public class EmptyItemModelSmith extends ItemModelSmith{
    public EmptyItemModelSmith() {
        super(null);
    }

    @Override
    public ItemModelBuilder act(LodestoneItemModelProvider provider, Supplier<? extends Item> registryObject) {
        return null;
    }

    @Override
    public ItemModelBuilder act(LodestoneItemModelProvider provider, Supplier<? extends Item> registryObject, ItemModelModifier<ItemModelBuilder> modifier) {
        return null;
    }
}
