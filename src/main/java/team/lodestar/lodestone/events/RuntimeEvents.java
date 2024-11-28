package team.lodestar.lodestone.events;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDeathEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingHurtEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import team.lodestar.lodestone.handlers.ItemEventHandler;
import team.lodestar.lodestone.handlers.LodestoneAttributeEventHandler;
import team.lodestar.lodestone.handlers.WorldEventHandler;

public class RuntimeEvents {

    public static void onHurt() {
        LivingHurtEvent.EVENT.register(LodestoneAttributeEventHandler::processAttributes);
        LivingHurtEvent.EVENT.register(ItemEventHandler::triggerHurtResponses);
    }

    public static void onDeath() {
        ServerLivingEntityEvents.ALLOW_DEATH.register(ItemEventHandler::triggerDeathResponses);
    }

    public static void entityJoin(EntityJoinLevelEvent event) {
        WorldEventHandler.playerJoin(event);
    }

    public static void worldTick(LevelTickEvent.Post event) {
        WorldEventHandler.worldTick(event);
    }
}