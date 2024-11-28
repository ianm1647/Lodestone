package team.lodestar.lodestone.events.types.worldevent;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.minecraft.world.level.Level;
import team.lodestar.lodestone.systems.worldevent.WorldEventInstance;

public class WorldEventInstanceEvent extends BaseEvent {
    private WorldEventInstance worldEvent;
    private Level level;

    public WorldEventInstanceEvent(WorldEventInstance worldEvent, Level level) {
        this.worldEvent = worldEvent;
        this.level = level;
    }

    public WorldEventInstance getWorldEvent() {
        return worldEvent;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public void sendEvent() {

    }
}
