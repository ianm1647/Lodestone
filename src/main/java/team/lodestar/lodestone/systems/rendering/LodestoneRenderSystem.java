package team.lodestar.lodestone.systems.rendering;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import team.lodestar.lodestone.LodestoneLib;

public class LodestoneRenderSystem extends RenderSystem {
    public static void wrap(RenderCall renderCall) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(renderCall);
        } else {
            renderCall.execute();
        }
    }
}
