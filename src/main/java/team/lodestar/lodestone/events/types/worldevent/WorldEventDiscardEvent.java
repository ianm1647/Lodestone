package team.lodestar.lodestone.events.types.worldevent;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.Level;
import team.lodestar.lodestone.systems.worldevent.WorldEventInstance;

public class WorldEventDiscardEvent extends WorldEventInstanceEvent {
    public WorldEventDiscardEvent(WorldEventInstance worldEvent, Level level) {
        super(worldEvent, level);
    }

    public static final Event<WorldEventDiscardEvent.Post> EVENT = EventFactory.createArrayBacked(WorldEventDiscardEvent.Post.class, callbacks -> event -> {
        for (final WorldEventDiscardEvent.Post callback : callbacks)
            callback.post(event);
    });

    public interface Post {
        void post(WorldEventDiscardEvent event);
    }
}
