package team.lodestar.lodestone.events;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.handlers.ThrowawayBlockDataHandler;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import team.lodestar.lodestone.registry.common.particle.*;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneItemCrumbsParticleType;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneTerrainParticleType;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType;

import static team.lodestar.lodestone.registry.common.particle.LodestoneParticleTypes.*;
import static team.lodestar.lodestone.registry.common.particle.LodestoneParticleTypes.ITEM_PARTICLE;

public class ClientSetupEvents {

    public static void clientSetup() {
        RenderHandler.onClientSetup();
        ParticleEmitterHandler.registerParticleEmitters();
        ThrowawayBlockDataHandler.setRenderLayers();
    }
}