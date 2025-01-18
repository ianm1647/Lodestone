package team.lodestar.lodestone.systems.datagen.itemsmith;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneItemModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A class responsible for generating item models when used with an ItemModelProvider
 */
public class ItemModelSmith {

    private final ItemModelSupplier modelSupplier;

    public ItemModelSmith(ItemModelSupplier modelSupplier) {
        this.modelSupplier = modelSupplier;
    }

    public ItemModelSmithConfiguration modifyResult(Consumer<ItemModelSmithResult> modifier) {
        return configure().modifyResult(modifier);
    }

    public ItemModelSmithConfiguration addModelNameAffix(String affix) {
        return configure().addModelNameAffix(affix);
    }

    public ItemModelSmithConfiguration modifyModelName(Function<String, String> modelNameModifier) {
        return configure().modifyModelName(modelNameModifier);
    }

    public ItemModelSmithConfiguration addTextureNameAffix(String affix) {
        return configure().addTextureNameAffix(affix);
    }

    public ItemModelSmithConfiguration modifyTextureName(Function<String, String> textureNameModifier) {
        return configure().modifyTextureName(textureNameModifier);
    }

    protected ItemModelSmithConfiguration configure() {
        return new ItemModelSmithConfiguration(modelSupplier);
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

    public ItemModelSmithResult act(ItemModelSmithData data, Supplier<? extends Item> itemSupplier) {
        data.consumer.accept(itemSupplier);
        return act(data.provider, itemSupplier);
    }

    public ItemModelSmithResult act(LodestoneItemModelProvider provider, Supplier<? extends Item> itemSupplier) {
        var item = itemSupplier.get();
        preDatagen(provider, item);
        ItemModelBuilder model = modelSupplier.act(item, provider);
        ItemModelSmithResult result = new ItemModelSmithResult(provider, item, model);
        postDatagen(result);
        return result;
    }

    protected void preDatagen(LodestoneItemModelProvider provider, Item item) {

    }

    protected void postDatagen(ItemModelSmithResult result) {

    }

    public interface ItemModelSupplier {
        ItemModelBuilder act(Item item, LodestoneItemModelProvider provider);
    }
}