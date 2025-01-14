package team.lodestar.lodestone.systems.datagen.itemsmith;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneItemModelProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemModelSmith extends AbstractItemModelSmith {

    public final ItemModelSupplier modelSupplier;

    public ItemModelSmith(ItemModelSupplier modelSupplier) {
        this.modelSupplier = modelSupplier;
    }

    @SafeVarargs
    public final List<ItemModelBuilder> act(ItemModelSmithData data, Supplier<? extends Item>... items) {
        return act(data, Arrays.stream(items).toList());
    }

    public List<ItemModelBuilder> act(ItemModelSmithData data, Collection<Supplier<? extends Item>> items) {
        return items.stream().peek(data.consumer).map(s -> act(data, s)).toList();
    }

    private ItemModelBuilder act(ItemModelSmithData data, Supplier<? extends Item> registryObject) {
        return act(data.provider, registryObject);
    }

    public ItemModelBuilder act(LodestoneItemModelProvider provider, Supplier<? extends Item> registryObject) {
        Item item = registryObject.get();
        return modelSupplier.act(item, provider);
    }

    @SafeVarargs
    public final List<ItemModelBuilder> act(ItemModelSmithData data, ItemModelModifier<ItemModelBuilder> modifier, Supplier<? extends Item>... items) {
        return act(data, modifier, Arrays.stream(items).toList());
    }

    public List<ItemModelBuilder> act(ItemModelSmithData data, ItemModelModifier<ItemModelBuilder> modifier, Collection<Supplier<? extends Item>> items) {
        return items.stream().peek(data.consumer).map(s -> act(data, modifier, s)).toList();
    }

    private ItemModelBuilder act(ItemModelSmithData data, ItemModelModifier<ItemModelBuilder> modifier, Supplier<? extends Item> registryObject) {
        return act(data.provider, registryObject, modifier);
    }

    public ItemModelBuilder act(LodestoneItemModelProvider provider, Supplier<? extends Item> registryObject, ItemModelModifier<ItemModelBuilder> modifier) {
        Item item = registryObject.get();
        ItemModelBuilder model = modelSupplier.act(item, provider);
        modifier.act(model, item, provider);
        return model;
    }

    public interface ItemModelSupplier {
        ItemModelBuilder act(Item item, LodestoneItemModelProvider provider);
    }


    public interface ItemModelModifier<T extends ModelBuilder<T>> {
        ItemModelModifierTemplate<ItemModelBuilder, ItemLayerModelBuilder<ItemModelBuilder>> FACE_DATA = (c) -> (b, i, p) -> {
            var faceBuilder = ItemLayerModelBuilder.begin(b, p.existingFileHelper);
            c.accept(faceBuilder);
        };
        void act(T builder, Item item, LodestoneItemModelProvider provider);

    }

    public interface ItemModelModifierTemplate<T extends ModelBuilder<T>, K extends CustomLoaderBuilder<T>> {
        ItemModelModifierTemplate<ItemModelBuilder, SeparateTransformsModelBuilder<ItemModelBuilder>> SEPARATE_TRANSFORMS = (c) -> (b, i, p) -> {
            var faceBuilder = SeparateTransformsModelBuilder.begin(b, p.existingFileHelper);
            c.accept(faceBuilder);
        };

        ItemModelModifier<T> apply(Consumer<K> behavior);
    }
}