package team.lodestar.lodestone.events.types.worldevent;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.Level;
import team.lodestar.lodestone.systems.worldevent.WorldEventInstance;

public class WorldEventCreationEvent extends WorldEventInstanceEvent implements CancellableEvent {
    public WorldEventCreationEvent(WorldEventInstance worldEvent, Level level) {
        super(worldEvent, level);
    }

    public static final Event<WorldEventCreationEvent.Post> EVENT = EventFactory.createArrayBacked(WorldEventCreationEvent.Post.class, callbacks -> event -> {
        for (final WorldEventCreationEvent.Post callback : callbacks)
            callback.post(event);
    });

    public interface Post {
        void post(WorldEventCreationEvent event);
    }
}
