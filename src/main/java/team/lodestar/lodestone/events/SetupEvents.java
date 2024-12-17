package team.lodestar.lodestone.events;

import team.lodestar.lodestone.handlers.ThrowawayBlockDataHandler;
import team.lodestar.lodestone.registry.common.LodestoneCommandArgumentTypes;

public class SetupEvents {

    public static void registerCommon() {
        LodestoneCommandArgumentTypes.registerArgumentTypes();
    }

    public static void lateSetup() {
        ThrowawayBlockDataHandler.wipeCache();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void buildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        LodestoneItemProperties.populateItemGroups(event);
    }
}