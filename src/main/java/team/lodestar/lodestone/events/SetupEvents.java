package team.lodestar.lodestone.events;

import team.lodestar.lodestone.handlers.ThrowawayBlockDataHandler;
import team.lodestar.lodestone.registry.common.LodestoneCommandArgumentTypes;
import team.lodestar.lodestone.systems.item.LodestoneItemProperties;

public class SetupEvents {

    public static void registerCommon() {
        LodestoneCommandArgumentTypes.registerArgumentTypes();
    }

    public static void lateSetup() {
        ThrowawayBlockDataHandler.wipeCache();
    }


    public static void buildCreativeTabs() {
        LodestoneItemProperties.populateItemGroups();
    }
}