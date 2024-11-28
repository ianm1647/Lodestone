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

    public static void registerParticleFactory() {
        registerParticleProviders();
        LodestoneScreenParticleTypes.registerParticleFactory();
    }

    public static void clientSetup() {
        RenderHandler.onClientSetup();
        ParticleEmitterHandler.registerParticleEmitters();
        ThrowawayBlockDataHandler.setRenderLayers();
    }

    private static void registerParticleProviders() {
        ParticleFactoryRegistry.getInstance().register(WISP_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SMOKE_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SPARKLE_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TWINKLE_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        ParticleFactoryRegistry.getInstance().register(STAR_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);

        ParticleFactoryRegistry.getInstance().register(SPARK_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);


        ParticleFactoryRegistry.getInstance().register(LodestoneParticleTypes.EXTRUDING_SPARK_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LodestoneParticleTypes.THIN_EXTRUDING_SPARK_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);

        ParticleFactoryRegistry.getInstance().register(TERRAIN_PARTICLE.get(), s -> new team.lodestar.lodestone.systems.particle.world.type.LodestoneTerrainParticleType.Factory());
        ParticleFactoryRegistry.getInstance().register(ITEM_PARTICLE.get(), s -> new LodestoneItemCrumbsParticleType.Factory());
    }
}