package team.lodestar.lodestone.registry.common;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.attachment.WorldEventAttachment;


public class LodestoneAttachmentTypes {

    public static final AttachmentType<WorldEventAttachment> WORLD_EVENT_DATA =
            AttachmentRegistry.<WorldEventAttachment>builder()
                    .persistent(WorldEventAttachment.CODEC)
                    .initializer(WorldEventAttachment::new)
                    .buildAndRegister(LodestoneLib.lodestonePath("world_event_data"));

    public static void register() {
    }
}
