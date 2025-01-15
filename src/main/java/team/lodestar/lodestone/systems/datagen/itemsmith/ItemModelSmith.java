package team.lodestar.lodestone.systems.datagen.itemsmith;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import org.apache.logging.log4j.util.TriConsumer;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneItemModelProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * A class responsible for generating item models when used with an ItemModelProvider
 */
public class ItemModelSmith {

    private final ItemModelSupplier modelSupplier;
    private final ItemModelModifier<ItemModelBuilder> modifier;

    public ItemModelSmith(ItemModelSupplier modelSupplier) {
        this(modelSupplier, null);
    }

    public ItemModelSmith(ItemModelSmith modelSmith, ItemModelModifier<ItemModelBuilder> modifier) {
        this(modelSmith.modelSupplier, modifier);
    }

    public ItemModelSmith(ItemModelSupplier modelSupplier, ItemModelModifier<ItemModelBuilder> modifier) {
        this.modelSupplier = modelSupplier;
        this.modifier = modifier;
    }

    @SafeVarargs
    public final List<ItemModelBuilder> act(ItemModelSmithData data, Supplier<? extends Item>... items) {
        return act(data, Arrays.stream(items).toList());
    }

    public final List<ItemModelBuilder> act(ItemModelSmithData data, Collection<Supplier<? extends Item>> items) {
        return items.stream().map(s -> act(data, s)).toList();
    }

    public ItemModelBuilder act(ItemModelSmithData data, Supplier<? extends Item> registryObject) {
        data.consumer.accept(registryObject);
        return act(data.provider, registryObject);
    }

    @SafeVarargs
    public final List<ItemModelBuilder> act(ItemModelSmithData data, ItemModelModifier<ItemModelBuilder> modifier, Supplier<? extends Item>... items) {
        return act(data, modifier, Arrays.stream(items).toList());
    }

    public final List<ItemModelBuilder> act(ItemModelSmithData data, ItemModelModifier<ItemModelBuilder> modifier, Collection<Supplier<? extends Item>> items) {
        return items.stream().map(s -> act(data, s, modifier)).toList();
    }

    public ItemModelBuilder act(ItemModelSmithData data, Supplier<? extends Item> registryObject, ItemModelModifier<ItemModelBuilder> modifier) {
        Item item = registryObject.get();
        data.consumer.accept(registryObject);
        ItemModelBuilder model = act(data.provider, registryObject);
        modifier.act(model, item, data.provider);
        return model;
    }

    public ItemModelBuilder act(LodestoneItemModelProvider provider, Supplier<? extends Item> registryObject) {
        Item item = registryObject.get();
        ItemModelBuilder model = modelSupplier.act(item, provider);
        if (modifier != null) {
            modifier.act(model, item, provider);
        }
        return model;
    }

    public interface ItemModelSupplier {
        ItemModelBuilder act(Item item, LodestoneItemModelProvider provider);
    }

    public interface ItemModelModifier<T extends ModelBuilder<T>> {
        void act(T builder, Item item, LodestoneItemModelProvider provider);
    }

    /**
     * This thing is horribly overengineered haha
     * */
    public interface ItemModelModifierTemplate<T extends ModelBuilder<T>, K extends CustomLoaderBuilder<T>> {
        ItemModelModifierTemplate<ItemModelBuilder, ItemLayerModelBuilder<ItemModelBuilder>> FACE_DATA = (c) -> (b, i, p) -> {
            var faceBuilder = ItemLayerModelBuilder.begin(b, p.existingFileHelper);
            c.accept(faceBuilder, i, p);
        };
        ItemModelModifierTemplate<ItemModelBuilder, SeparateTransformsModelBuilder<ItemModelBuilder>> SEPARATE_TRANSFORMS = (c) -> (b, i, p) -> {
            var faceBuilder = SeparateTransformsModelBuilder.begin(b, p.existingFileHelper);
            c.accept(faceBuilder, i, p);
        };

        ItemModelModifier<T> apply(TriConsumer<K, Item, LodestoneItemModelProvider> data);
    }
}