package team.lodestar.lodestone.systems.datagen.itemsmith;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneItemModelProvider;

public class ItemModelSmithResult {
    public final LodestoneItemModelProvider provider;
    public final Item item;
    public final ItemModelBuilder builder;

    public ItemModelSmithResult(LodestoneItemModelProvider provider, Item item, ItemModelBuilder builder) {
        this.provider = provider;
        this.item = item;
        this.builder = builder;
    }

    public ModifiedItemModelSmithResult applyModifier(ItemModelModifier modifier) {
        return new ModifiedItemModelSmithResult(this, modifier);
    }

    public interface ItemModelModifier {
        void act(ItemModelSmithResult result);
    }

    public static class ModifiedItemModelSmithResult extends ItemModelSmithResult {

        public ModifiedItemModelSmithResult(ItemModelSmithResult parent, ItemModelModifier modifier) {
            super(parent.provider, parent.item, parent.builder);
            modifier.act(this);
        }

        public ModifiedItemModelSmithResult(LodestoneItemModelProvider provider, Item item, ItemModelBuilder result) {
            super(provider, item, result);
        }

        public ItemLayerModelBuilder<ItemModelBuilder> makeItemLayerBuilder() {
            return ItemLayerModelBuilder.begin(builder, provider.existingFileHelper);
        }

        public SeparateTransformsModelBuilder<ItemModelBuilder> makeSeparateTransformBuilder() {
            return SeparateTransformsModelBuilder.begin(builder, provider.existingFileHelper);
        }
    }
}