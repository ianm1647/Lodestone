package team.lodestar.lodestone.systems.datagen;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import team.lodestar.lodestone.systems.datagen.itemsmith.EmptyItemModelSmith;
import team.lodestar.lodestone.systems.datagen.itemsmith.ItemModelSmith;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ItemModelSmithTypes {

    public static final ResourceLocation GENERATED = ResourceLocation.parse("item/generated");
    public static final ResourceLocation HANDHELD = ResourceLocation.parse("item/handheld");

    public static ItemModelSmith NO_MODEL = new EmptyItemModelSmith();

    public static Function<ResourceLocation, ItemModelSmith> PARENTED_ITEM = Util.memoize((parent) -> new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.createGenericModel(item, parent, provider.getItemTexture(name));
    })));
    public static BiFunction<ResourceLocation, String, ItemModelSmith> AFFIXED_ITEM = Util.memoize((parent, affix) -> new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.createGenericModel(item, parent, provider.getItemTexture(name + affix));
    })));

    public static ItemModelSmith HANDHELD_ITEM = PARENTED_ITEM.apply(HANDHELD);
    public static Function<String, ItemModelSmith> AFFIXED_HANDHELD_ITEM = Util.memoize((affix) -> AFFIXED_ITEM.apply(HANDHELD, affix));
    public static ItemModelSmith GENERATED_ITEM = PARENTED_ITEM.apply(GENERATED);
    public static Function<String, ItemModelSmith> AFFIXED_GENERATED_ITEM = Util.memoize((affix) -> AFFIXED_ITEM.apply(GENERATED, affix));

    public static ItemModelSmith BLOCK_TEXTURE_ITEM = new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.createGenericModel(item, GENERATED, provider.getBlockTexture(name));
    }));


    public static Function<String, ItemModelSmith> AFFIXED_BLOCK_TEXTURE_MODEL = Util.memoize((affix) -> new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.createGenericModel(item, GENERATED, provider.getBlockTexture(name + affix));
    })));

    public static ItemModelSmith BLOCK_MODEL_ITEM = new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.getBuilder(name).parent(new ModelFile.UncheckedModelFile(provider.modLoc("block/" + name)));
    }));

    public static Function<String, ItemModelSmith> AFFIXED_BLOCK_MODEL = Util.memoize((affix) -> new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.getBuilder(name).parent(new ModelFile.UncheckedModelFile(provider.modLoc("block/" + name + affix)));
    })));

    public static ItemModelSmith CROSS_MODEL_ITEM = new ItemModelSmith(((item, provider) -> provider.createGenericModel(item, GENERATED, provider.getBlockTextureFromCache("cross"))));

    public static ItemModelSmith WALL_ITEM = new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.wallInventory(name, provider.getBlockTextureFromCache("wall"));
    }));

    public static ItemModelSmith FENCE_ITEM = new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.fenceInventory(name, provider.getBlockTextureFromCache("texture"));
    }));
    public static ItemModelSmith BUTTON_ITEM = AFFIXED_BLOCK_MODEL.apply("_inventory");
    public static ItemModelSmith TRAPDOOR_ITEM = AFFIXED_BLOCK_MODEL.apply("_bottom");

}