package team.lodestar.lodestone.systems.item;

import net.minecraft.core.component.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

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

    @SuppressWarnings("DataFlowIssue")
    public Item.Properties mergeAttributes(ItemAttributeModifiers attributes) {
        if (components != null && components.build().has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            ItemAttributeModifiers existing = components.build().get(DataComponents.ATTRIBUTE_MODIFIERS);
            var builder = ItemAttributeModifiers.builder();
            for (ItemAttributeModifiers.Entry entry : existing.modifiers()) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
            for (ItemAttributeModifiers.Entry entry : attributes.modifiers()) {
                builder.add(entry.attribute(), entry.modifier(), entry.slot());
            }
            return attributes(builder.build());
        }
        return attributes(attributes);
    }

    public static void populateItemGroups(BuildCreativeModeTabContentsEvent event) {
        final ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
        if (TAB_SORTING.containsKey(tabKey)) {
            TAB_SORTING.get(tabKey).stream().map(BuiltInRegistries.ITEM::get)
                    .filter(s -> !event.getParentEntries().contains(s.getDefaultInstance()))
                    .forEach(event::accept);
        }
    }
}