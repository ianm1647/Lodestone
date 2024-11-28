package team.lodestar.lodestone.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;

public interface LodestoneCameraEvent {
    Event<Setup> EVENT = EventFactory.createArrayBacked(Setup.class, callbacks -> {
        return (camera, renderPartialTicks, yaw, pitch) -> {
            for (var callback : callbacks) {
                return callback.setup(camera, renderPartialTicks, yaw, pitch);
            }
            return new Data(camera, renderPartialTicks, yaw, pitch);
        };
    });

    @FunctionalInterface
    interface Setup {
        Data setup(Camera camera, double renderPartialTicks, float yaw, float pitch);
    }

    record Data(Camera camera, double renderPartialTicks, float yaw, float pitch){}
}