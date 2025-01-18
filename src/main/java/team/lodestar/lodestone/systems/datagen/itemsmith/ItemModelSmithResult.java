package team.lodestar.lodestone.systems.datagen.itemsmith;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneItemModelProvider;

import java.util.function.Consumer;

public class ItemModelSmithResult {
    public final LodestoneItemModelProvider provider;
    public final Item item;
    public final ItemModelBuilder builder;

    public ItemModelSmithResult(LodestoneItemModelProvider provider, Item item, ItemModelBuilder builder) {
        this.provider = provider;
        this.item = item;
        this.builder = builder;
    }

    public ItemLayerModelBuilder<ItemModelBuilder> makeItemLayerBuilder() {
        return ItemLayerModelBuilder.begin(builder, provider.existingFileHelper);
    }

    public SeparateTransformsModelBuilder<ItemModelBuilder> makeSeparateTransformBuilder() {
        return SeparateTransformsModelBuilder.begin(builder, provider.existingFileHelper);
    }

    public ItemModelSmithResult applyModifier(Consumer<ItemModelSmithResult> modifier) {
        modifier.accept(this);
        return this;
    }
}