package team.lodestar.lodestone.systems.datagen.itemsmith;

import net.minecraft.world.item.Item;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneItemModelProvider;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A configured instance of an ItemModelSmith
 */
public class ItemModelSmithConfiguration extends ItemModelSmith {

    private Consumer<ItemModelSmithResult> modifier;
    private Function<String, String> modelNameModifier;
    private Function<String, String> textureNameModifier;

    public ItemModelSmithConfiguration(ItemModelSupplier modelSupplier) {
        super(modelSupplier);
    }

    public ItemModelSmithConfiguration modifyResult(Consumer<ItemModelSmithResult> modifier) {
        this.modifier = modifier;
        return this;
    }

    public ItemModelSmithConfiguration addModelNameAffix(String affix) {
        return modifyModelName(s -> s + affix);
    }

    public ItemModelSmithConfiguration modifyModelName(Function<String, String> modelNameModifier) {
        this.modelNameModifier = modelNameModifier;
        return this;
    }

    public ItemModelSmithConfiguration addTextureNameAffix(String affix) {
        return modifyTextureName(s -> s + affix);
    }

    public ItemModelSmithConfiguration modifyTextureName(Function<String, String> textureNameModifier) {
        this.textureNameModifier = textureNameModifier;
        return this;
    }

    @Override
    protected void preDatagen(LodestoneItemModelProvider provider, Item item) {
        provider.setModelNameModifier(modelNameModifier);
        provider.setTextureNameModifier(textureNameModifier);
    }

    @Override
    protected void postDatagen(ItemModelSmithResult result) {
        if (modifier != null) {
            result.applyModifier(modifier);
        }
    }
}