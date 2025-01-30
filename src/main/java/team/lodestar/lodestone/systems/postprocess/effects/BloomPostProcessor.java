package team.lodestar.lodestone.systems.postprocess.effects;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.postprocess.PostProcessor;

public class BloomPostProcessor extends PostProcessor {
    private final RenderTarget bloomTarget;
    private final RenderStateShard.OutputStateShard bloomOutput;

    private EffectInstance bloomMask;

    private boolean forceDisabled;

    public BloomPostProcessor() {
        var window = Minecraft.getInstance().getWindow();
        this.bloomTarget = new TextureTarget(window.getWidth(), window.getHeight(), true, Minecraft.ON_OSX);
        this.bloomTarget.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.bloomOutput = new RenderStateShard.OutputStateShard("bloomTarget",
                () -> this.bloomTarget.bindWrite(false),
                () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false)
        );
        this.setActive(false);
    }
    @Override
    public ResourceLocation getPostChainLocation() {
        return LodestoneLib.lodestonePath("bloom");
    }

    @Override
    public void init() {
        super.init();
        if (this.postChain != null) {
            this.bloomMask = effects[0];
        }
    }

    @Override
    public void beforeProcess(Matrix4f viewModelMatrix) {
        this.bloomMask.setSampler("BloomMaskSampler", this.bloomTarget::getColorTextureId);
    }

    @Override
    public void afterProcess() {
        this.bloomTarget.clear(Minecraft.ON_OSX);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.bloomTarget.resize(width, height, Minecraft.ON_OSX);
    }

    public void forceDisable() {
        this.forceDisabled = true;
        this.setActive(false);
    }

    @Override
    public void setActive(boolean active) {
        if (this.forceDisabled) active = false;
        super.setActive(active);
    }

    public RenderStateShard.OutputStateShard getBloomOutput() {
        return bloomOutput;
    }
}
