package team.lodestar.lodestone.systems.datagen.itemsmith;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneItemModelProvider;

import java.util.function.Consumer;

public class ItemModelSmithResult {
    private final LodestoneItemModelProvider provider;
    private final Item item;
    private final ItemModelBuilder builder;

    public ItemModelSmithResult(LodestoneItemModelProvider provider, Item item, ItemModelBuilder builder) {
        this.provider = provider;
        this.item = item;
        this.builder = builder;
    }

    public LodestoneItemModelProvider getProvider() {
        return provider;
    }

    public Item getItem() {
        return item;
    }

    public ItemModelBuilder getBuilder() {
        return builder;
    }

    public ItemLayerModelBuilder<ItemModelBuilder> addModelLayerData() {
        return builder.customLoader(ItemLayerModelBuilder::begin);
    }

    public SeparateTransformsModelBuilder<ItemModelBuilder> addSeparateTransformData() {
        return builder.customLoader(SeparateTransformsModelBuilder::begin);
    }
    public ItemModelSmithResult applyModifier(Consumer<ItemModelSmithResult> modifier) {
        modifier.accept(this);
        return this;
    }
}