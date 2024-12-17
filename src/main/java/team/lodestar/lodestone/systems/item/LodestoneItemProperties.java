package team.lodestar.lodestone.systems.item;

import io.github.fabricators_of_create.porting_lib.util.DeferredHolder;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;

import java.util.*;

public class LodestoneItemProperties extends Item.Properties {
    public static final Map<ResourceKey<CreativeModeTab>, List<ResourceLocation>> TAB_SORTING = new HashMap<>();

    public final ResourceKey<CreativeModeTab> tab;

    public LodestoneItemProperties(DeferredHolder<CreativeModeTab, CreativeModeTab> tab) {
        this(tab.getKey());
    }

    public LodestoneItemProperties(ResourceKey<CreativeModeTab> tab) {
        this.tab = tab;
    }

    public static void addToTabSorting(ResourceLocation itemId, Item.Properties properties) {
        if (properties instanceof LodestoneItemProperties lodestoneItemProperties) {
            TAB_SORTING.computeIfAbsent(lodestoneItemProperties.tab, (key) -> new ArrayList<>()).add(itemId);
        }
    }

    public static void populateItemGroups(CreativeModeTab tab, FabricItemGroupEntries entry) {
        Optional<ResourceKey<CreativeModeTab>> opt = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab);
        if (opt.isPresent()) {
            if (TAB_SORTING.containsKey(opt.get())) {
                TAB_SORTING.get(opt.get()).stream().map(BuiltInRegistries.ITEM::get).forEach(entry::accept);
            }
        }
    }
}