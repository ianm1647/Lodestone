package team.lodestar.lodestone.data;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.LodestoneLib;

import java.util.concurrent.CompletableFuture;

public class LodestoneBlockTagDatagen extends FabricTagProvider.BlockTagProvider {


    public LodestoneBlockTagDatagen(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Lodestone Block Tags";
    }


    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

    }
}