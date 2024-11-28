package team.lodestar.lodestone.data;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.datagen.providers.*;

import java.util.concurrent.CompletableFuture;

import static team.lodestar.lodestone.registry.common.tag.LodestoneItemTags.*;


public class LodestoneItemTagDatagen extends FabricTagProvider.ItemTagProvider {
    public LodestoneItemTagDatagen(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, FabricTagProvider.BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    public String getName() {
        return "Malum Item Tags";
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        getOrCreateTagBuilder(NUGGETS_COPPER);
        getOrCreateTagBuilder(INGOTS_COPPER).add(Items.COPPER_INGOT);
        getOrCreateTagBuilder(NUGGETS_LEAD);
        getOrCreateTagBuilder(INGOTS_LEAD);
        getOrCreateTagBuilder(NUGGETS_SILVER);
        getOrCreateTagBuilder(INGOTS_SILVER);
        getOrCreateTagBuilder(NUGGETS_ALUMINUM);
        getOrCreateTagBuilder(INGOTS_ALUMINUM);
        getOrCreateTagBuilder(NUGGETS_NICKEL);
        getOrCreateTagBuilder(INGOTS_NICKEL);
        getOrCreateTagBuilder(NUGGETS_URANIUM);
        getOrCreateTagBuilder(INGOTS_URANIUM);
        getOrCreateTagBuilder(NUGGETS_OSMIUM);
        getOrCreateTagBuilder(INGOTS_OSMIUM);
        getOrCreateTagBuilder(NUGGETS_ZINC);
        getOrCreateTagBuilder(INGOTS_ZINC);
        getOrCreateTagBuilder(NUGGETS_TIN);
        getOrCreateTagBuilder(INGOTS_TIN);
    }
}