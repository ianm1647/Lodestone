package team.lodestar.lodestone.systems.datagen.itemsmith;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneItemModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A class responsible for generating item models when used with an ItemModelProvider
 */
public class ItemModelSmith {

    private final ItemModelSupplier modelSupplier;
    private final Consumer<ItemModelSmithResult> modifier;

    public ItemModelSmith(ItemModelSupplier modelSupplier) {
        this(modelSupplier, null);
    }

    public ItemModelSmith(ItemModelSmith modelSmith, Consumer<ItemModelSmithResult> modifier) {
        this(modelSmith.modelSupplier, modifier);
    }

    public ItemModelSmith(ItemModelSupplier modelSupplier, Consumer<ItemModelSmithResult> modifier) {
        this.modelSupplier = modelSupplier;
        this.modifier = modifier;
    }

    @SafeVarargs
    public final List<ItemModelSmithResult> act(ItemModelSmithData data, Supplier<? extends Item>... items) {
        return act(data, Arrays.stream(items).toList());
    }

    public final List<ItemModelSmithResult> act(ItemModelSmithData data, Collection<Supplier<? extends Item>> items) {
        var copy = new ArrayList<>(items);
        List<ItemModelSmithResult> result = new ArrayList<>();
        for (Supplier<? extends Item> item : copy) {
            result.add(act(data, item));
        }
        return result;
    }

    public ItemModelSmithResult act(ItemModelSmithData data, Supplier<? extends Item> registryObject) {
        data.consumer.accept(registryObject);
        return act(data.provider, registryObject);
    }

    public ItemModelSmithResult act(LodestoneItemModelProvider provider, Supplier<? extends Item> registryObject) {
        var item = registryObject.get();
        ItemModelBuilder model = modelSupplier.act(item, provider);
        ItemModelSmithResult result = new ItemModelSmithResult(provider, item, model);
        if (modifier != null) {
            result.applyModifier(modifier);
        }
        return result;
    }

    public interface ItemModelSupplier {
        ItemModelBuilder act(Item item, LodestoneItemModelProvider provider);
    }

}