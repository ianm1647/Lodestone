package team.lodestar.lodestone.systems.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import team.lodestar.lodestone.systems.blockentity.*;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CachedBlockEntitySoundInstance<T extends LodestoneBlockEntity> extends LodestoneBlockEntitySoundInstance<T> {
    private static final Map<BlockPos, CachedBlockEntitySoundInstance<?>> ACTIVE_SOUNDS = new WeakHashMap<>();

    public CachedBlockEntitySoundInstance(T blockEntity, Supplier<SoundEvent> soundEvent, float volume, float pitch) {
        super(blockEntity, soundEvent.get(), volume, pitch);
        var pos = blockEntity.getBlockPos();
        this.x = pos.getX() + 0.5f;
        this.y = pos.getY() + 0.5f;
        this.z = pos.getZ() + 0.5f;
    }

    @Override
    public void tick() {
        super.tick();
        if (isStopped()) {
            ACTIVE_SOUNDS.remove(blockEntity.getBlockPos());
        }
    }

    public static void playSound(LodestoneBlockEntity blockEntity, CachedBlockEntitySoundInstance<?> sound) {
        var blockPos = blockEntity.getBlockPos();
        boolean success = false;
        if (ACTIVE_SOUNDS.containsKey(blockPos)) {
            var existingSound = ACTIVE_SOUNDS.get(blockPos);
            if (!existingSound.location.equals(sound.location)) {
                existingSound.stop();
                ACTIVE_SOUNDS.put(blockPos, sound);
                success = true;
            }
        }
        else {
            ACTIVE_SOUNDS.put(blockPos, sound);
            success = true;
        }
        if (success) {
            Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
        }
    }
}