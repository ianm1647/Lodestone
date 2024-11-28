package team.lodestar.lodestone.events.types.worldevent;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.Level;
import team.lodestar.lodestone.systems.worldevent.WorldEventInstance;

public class WorldEventTickEvent extends WorldEventInstanceEvent implements CancellableEvent {
    public WorldEventTickEvent(WorldEventInstance worldEvent, Level level) {
        super(worldEvent, level);
    }


    public static final Event<WorldEventTickEvent.Post> EVENT = EventFactory.createArrayBacked(WorldEventTickEvent.Post.class, callbacks -> event -> {
        for (final WorldEventTickEvent.Post callback : callbacks)
            callback.post(event);
    });

    public interface Post {
        void post(WorldEventTickEvent event);
    }
}
