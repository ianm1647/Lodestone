package team.lodestar.lodestone.systems.datagen;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import team.lodestar.lodestone.systems.datagen.itemsmith.EmptyItemModelSmith;
import team.lodestar.lodestone.systems.datagen.itemsmith.ItemModelSmith;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ItemModelSmithTypes {

    public static final ResourceLocation GENERATED = ResourceLocation.parse("item/generated");
    public static final ResourceLocation HANDHELD = ResourceLocation.parse("item/handheld");

    public static ItemModelSmith NO_DATAGEN = new EmptyItemModelSmith();

    public static Function<ResourceLocation, ItemModelSmith> PARENTED_ITEM = Util.memoize((parent) -> new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.createGenericModel(item, parent, provider.getItemTexture(name));
    })));
    public static ItemModelSmith HANDHELD_ITEM = PARENTED_ITEM.apply(HANDHELD);
    public static ItemModelSmith GENERATED_ITEM = PARENTED_ITEM.apply(GENERATED);

    public static ItemModelSmith BLOCK_TEXTURE_ITEM = new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.createGenericModel(item, GENERATED, provider.getBlockTexture(name));
    }));

    public static ItemModelSmith BLOCK_MODEL_ITEM = new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.getBuilder(name).parent(new ModelFile.UncheckedModelFile(provider.getBlockTexture("block/" + name)));
    }));

    public static ItemModelSmith CROSS_MODEL_ITEM = new ItemModelSmith(((item, provider) -> provider.createGenericModel(item, GENERATED, provider.getBlockTextureFromCache("cross"))));
    public static ItemModelSmith WALL_ITEM = new ItemModelSmith(((item, provider) -> provider.wallInventory(provider.getItemName(item), provider.getBlockTextureFromCache("wall"))));
    public static ItemModelSmith FENCE_ITEM = new ItemModelSmith(((item, provider) -> provider.fenceInventory(provider.getItemName(item), provider.getBlockTextureFromCache("texture"))));

    public static ItemModelSmith BUTTON_ITEM = BLOCK_MODEL_ITEM.addTextureNameAffix("_inventory");
    public static ItemModelSmith TRAPDOOR_ITEM = BLOCK_MODEL_ITEM.addTextureNameAffix("_bottom");
}