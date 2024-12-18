package team.lodestar.lodestone.events;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityJoinLevelEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDamageEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDeathEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingHurtEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import team.lodestar.lodestone.handlers.ItemEventHandler;
import team.lodestar.lodestone.handlers.LodestoneAttributeEventHandler;
import team.lodestar.lodestone.handlers.WorldEventHandler;

public class RuntimeEvents {

    public static void onHurt() {
        LivingHurtEvent.EVENT.register(LodestoneAttributeEventHandler::processAttributes);
        LivingDamageEvent.DAMAGE.register(ItemEventHandler::triggerHurtResponses);
        LivingDamageEvent.DAMAGE.register(LodestoneAttributeEventHandler::triggerMagicDamage);
    }


    public static void onDeath() {
        LivingDeathEvent.EVENT.register(ItemEventHandler::triggerDeathResponses);
    }

    public static void entityJoin() {
        EntityJoinLevelEvent.EVENT.register(WorldEventHandler::playerJoin);
    }

    public static void worldTick() {
        ServerTickEvents.END_WORLD_TICK.register(WorldEventHandler::worldTick);
    }
}