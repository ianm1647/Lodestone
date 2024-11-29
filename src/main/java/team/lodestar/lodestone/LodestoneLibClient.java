package team.lodestar.lodestone;

import io.github.fabricators_of_create.porting_lib.config.ConfigRegistry;
import io.github.fabricators_of_create.porting_lib.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import team.lodestar.lodestone.config.ClientConfig;
import team.lodestar.lodestone.events.ClientRuntimeEvents;
import team.lodestar.lodestone.events.ClientSetupEvents;
import team.lodestar.lodestone.events.LodestoneRenderEvents;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import team.lodestar.lodestone.registry.client.LodestoneOBJModels;
import team.lodestar.lodestone.registry.client.LodestoneShaders;
import team.lodestar.lodestone.registry.common.LodestoneBlockEntities;
import team.lodestar.lodestone.systems.postprocess.PostProcessHandler;

public class LodestoneLibClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ConfigRegistry.registerConfig(LodestoneLib.LODESTONE, ModConfig.Type.CLIENT, ClientConfig.clientSpec);

        LodestoneBlockEntities.ClientOnly.registerRenderer();
        ParticleEmitterHandler.registerParticleEmitters();
        LodestoneShaders.init();
        LodestoneOBJModels.loadModels();

        ClientSetupEvents.registerParticleFactory();
        ClientSetupEvents.clientSetup();
        ClientRuntimeEvents.cameraSetup();

        WorldRenderEvents.LAST.register(PostProcessHandler::onWorldRenderLast);
        ClientTickEvents.END_CLIENT_TICK.register(ClientRuntimeEvents::clientTick);

        LodestoneRenderEvents.AFTER_SKY.register(ClientRuntimeEvents::renderStages);
        LodestoneRenderEvents.AFTER_PARTICLES.register(ClientRuntimeEvents::renderStages);
        LodestoneRenderEvents.AFTER_WEATHER.register(ClientRuntimeEvents::renderStages);
        LodestoneRenderEvents.BEFORE_CLEAR.register(PostProcessHandler::onAfterSolidBlocks);
        LodestoneRenderEvents.AFTER_LEVEL.register(ClientRuntimeEvents::renderStages);
    }
}
