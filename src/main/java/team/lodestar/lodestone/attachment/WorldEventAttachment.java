package team.lodestar.lodestone.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.lodestar.lodestone.systems.worldevent.WorldEventInstance;

import java.util.ArrayList;

public class WorldEventAttachment {
    public final ArrayList<WorldEventInstance> activeWorldEvents = new ArrayList<>();
    public final ArrayList<WorldEventInstance> inboundWorldEvents = new ArrayList<>();

    public static final Codec<WorldEventAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WorldEventInstance.CODEC.listOf().fieldOf("activeWorldEvents").forGetter(attachment -> attachment.activeWorldEvents),
            WorldEventInstance.CODEC.listOf().fieldOf("inboundWorldEvents").forGetter(attachment -> attachment.inboundWorldEvents)
    ).apply(instance, (active, inbound) -> {
        WorldEventAttachment attachment = new WorldEventAttachment();
        attachment.activeWorldEvents.addAll(active);
        attachment.inboundWorldEvents.addAll(inbound);
        return attachment;
    }));
}