package team.lodestar.lodestone;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingHurtEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;
import team.lodestar.lodestone.events.RuntimeEvents;
import team.lodestar.lodestone.events.SetupEvents;
import team.lodestar.lodestone.handlers.LodestoneAttributeEventHandler;
import team.lodestar.lodestone.helpers.ShadersHelper;
import team.lodestar.lodestone.registry.common.LodestoneAttachmentTypes;
import team.lodestar.lodestone.compability.*;
import team.lodestar.lodestone.registry.common.*;
import team.lodestar.lodestone.registry.common.particle.*;
import team.lodestar.lodestone.systems.item.LodestoneItemProperties;

public class LodestoneLib implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String LODESTONE = "lodestone";
    public static final RandomSource RANDOM = RandomSource.create();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(LodestoneCommands::registerCommands);

        LodestoneBlockEntities.BLOCK_ENTITY_TYPES.register();
        LodestoneParticleTypes.PARTICLES.register();
        LodestoneAttributes.init();
        LodestoneRecipeSerializers.RECIPE_SERIALIZERS.register();
        LodestoneAttachmentTypes.register();
        LodestonePlacementFillers.MODIFIERS.register();
        LodestoneWorldEventTypes.WORLD_EVENT_TYPES.register();
        SetupEvents.registerCommon();

        CuriosCompat.init();

        RuntimeEvents.onDeath();
        RuntimeEvents.onHurt();
        ShadersHelper.init();
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register(LodestoneItemProperties::populateItemGroups);

        SetupEvents.lateSetup();
    }

    public static ResourceLocation lodestonePath(String path) {
        return ResourceLocation.fromNamespaceAndPath(LODESTONE, path);
    }
}