package team.lodestar.lodestone.systems.datagen.providers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class LodestoneItemModelProvider extends ItemModelProvider {

    private String texturePath = "";
    private Function<String, String> modelPathModifier;

    public LodestoneItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    public ItemModelBuilder getBuilder(String path) {
        if (modelPathModifier != null) {
            path = modelPathModifier.apply(path);
            modelPathModifier = null;
        }
        return super.getBuilder(path);
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getItemName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    public ResourceLocation getItemTexture(String path) {
        return modLoc("item/" + getTexturePath() + path);
    }

    public ResourceLocation getBlockTexture(String path) {
        return modLoc("block/" + LodestoneBlockStateProvider.getTexturePath() + path);
    }

    public ItemModelBuilder createGenericModel(Item item, ResourceLocation modelType, ResourceLocation textureLocation) {
        return withExistingParent(getItemName(item), modelType).texture("layer0", textureLocation);
    }

    public ResourceLocation getBlockTextureFromCache(String key) {
        return LodestoneBlockModelProvider.BLOCK_TEXTURE_CACHE.get(key);
    }
}