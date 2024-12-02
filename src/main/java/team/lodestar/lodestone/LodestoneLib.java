package team.lodestar.lodestone;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.events.RuntimeEvents;
import team.lodestar.lodestone.events.SetupEvents;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import team.lodestar.lodestone.helpers.ColorHelper;
import team.lodestar.lodestone.helpers.RandomHelper;
import team.lodestar.lodestone.helpers.ShadersHelper;
import team.lodestar.lodestone.registry.common.LodestoneAttachmentTypes;
import team.lodestar.lodestone.compability.*;
import team.lodestar.lodestone.registry.common.*;
import team.lodestar.lodestone.registry.common.particle.*;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.item.LodestoneItemProperties;
import team.lodestar.lodestone.systems.particle.SimpleParticleOptions;
import team.lodestar.lodestone.systems.particle.builder.ScreenParticleBuilder;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.screen.ScreenParticleHolder;

import java.awt.*;

import static net.minecraft.util.Mth.nextFloat;

public class LodestoneLib implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String LODESTONE = "lodestone";
    public static final RandomSource RANDOM = RandomSource.create();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(LodestoneCommands::registerCommands);

        LodestoneBlockEntities.BLOCK_ENTITY_TYPES.register();
        LodestoneParticleTypes.PARTICLES.register();
        LodestoneAttributes.init();
        LodestoneRecipeSerializers.RECIPE_SERIALIZERS.register();
        LodestoneAttachmentTypes.register();
        LodestonePlacementFillers.MODIFIERS.register();
        LodestoneWorldEventTypes.WORLD_EVENT_TYPES.register();
        SetupEvents.registerCommon();
        RuntimeEvents.entityJoin();
        RuntimeEvents.worldTick();

        CuriosCompat.init();

        RuntimeEvents.onDeath();
        RuntimeEvents.onHurt();
        ShadersHelper.init();
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register(LodestoneItemProperties::populateItemGroups);

        SetupEvents.lateSetup();
    }

    public static ResourceLocation lodestonePath(String path) {
        return ResourceLocation.fromNamespaceAndPath(LODESTONE, path);
    }

     public static class DemoBlockitem extends BlockItem implements ParticleEmitterHandler.ItemParticleSupplier {

        public DemoBlockitem(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        public void spawnLateParticles(ScreenParticleHolder target, Level level, float partialTick, ItemStack stack, float x, float y) {
            var rand = Minecraft.getInstance().level.getRandom();
            var color = Color.BLUE;
            var endColor = Color.green;
            ScreenParticleBuilder.create(LodestoneScreenParticleTypes.SPARKLE, target)
                    .setTransparencyData(GenericParticleData.create(0.04f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                    .setScaleData(GenericParticleData.create(0.8f + rand.nextFloat() * 0.1f, 0).setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build())
                    .setColorData(ColorParticleData.create(color, endColor).setCoefficient(2f).build())
                    .setLifetime(10 + rand.nextInt(10))
                    .setRandomOffset(0.05f)
                    .setRandomMotion(0.05f, 0.05f)
                    .spawnOnStack(0, 0);

            ScreenParticleBuilder.create(LodestoneScreenParticleTypes.WISP, target)
                    .setTransparencyData(GenericParticleData.create(0.03f, 0f).setEasing(Easing.SINE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(nextFloat(rand, 0.2f, 0.4f)).setEasing(Easing.EXPO_OUT).build())
                    .setScaleData(GenericParticleData.create(0.6f + rand.nextFloat() * 0.4f, 0).setEasing(Easing.EXPO_OUT).build())
                    .setColorData(ColorParticleData.create(color, endColor).setCoefficient(1.25f).build())
                    .setLifetime(20 + rand.nextInt(8))
                    .setRandomOffset(0.1f)
                    .setRandomMotion(0.4f, 0.4f)
                    .spawnOnStack(0, 0);
        }
    }

    public static DemoBlock register(DemoBlock block, String name, boolean shouldRegisterItem) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(LODESTONE, name);

        if (shouldRegisterItem) {
            DemoBlockitem blockItem = new DemoBlockitem(block, new Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, id, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(LODESTONE, path), blockEntityType);
    }

    public static class DemoBlock extends BaseEntityBlock {
        public DemoBlock(Properties settings) {
            super(settings);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
            return (currentLevel, pos, currentState, blockEntity) -> {
                if (blockEntity instanceof DemoBlockEntity cradleBlockEntity) {
                    cradleBlockEntity.tick();
                }
            };
        }

        @Override
        protected MapCodec<? extends BaseEntityBlock> codec() {
            return codec();
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new DemoBlockEntity(pos, state);
        }

        @Override
        protected RenderShape getRenderShape(BlockState state) {
            return RenderShape.INVISIBLE;
        }
    }

    public static class DemoBlockEntity extends BlockEntity {
        public DemoBlockEntity(BlockPos pos, BlockState state) {
            super(DEMO_BLOCK, pos, state);
        }

        public static Color firstColor = Color.BLUE;
        public static Color secondColor = Color.RED;



        public void tick() {
            if (level.isClientSide) {
                System.out.println("tick");
                final RandomSource random = level.random;
                Block block = getBlockState().getBlock();
                Color firstColor = ColorHelper.darker(this.firstColor, 1);
                Color secondColor = this.secondColor == null ? firstColor : ColorHelper.brighter(this.secondColor, 1);
                double x = worldPosition.getX() + 0.5f;
                double y = worldPosition.getY() + 0.5f;
                double z = worldPosition.getZ() + 0.5f;


                if (level.getGameTime() % 4L == 0) {
                    final long gameTime = level.getGameTime();
                    float scale = RandomHelper.randomBetween(random, 0.6f, 0.75f);
                    float velocity = RandomHelper.randomBetween(random, 0f, 0.02f);
                    float angle = ((gameTime % 24) / 24f) * (float) Math.PI * 2f;
                    Vec3 offset = new Vec3(Math.sin(angle), 0, Math.cos(angle)).normalize();
                    Vec3 offsetPosition = new Vec3(x + offset.x * 0.075f, y-0.05f, z + offset.z * 0.075f);
                    WorldParticleBuilder.create(LodestoneParticleTypes.TWINKLE_PARTICLE)
                            .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                            .setScaleData(GenericParticleData.create(scale * 0.75f, scale, 0).build())
                            .setColorData(ColorParticleData.create(firstColor, secondColor).setEasing(Easing.CIRC_IN_OUT).setCoefficient(2.5f).build())
                            .setTransparencyData(GenericParticleData.create(0f, 1f, 0).setEasing(Easing.SINE_IN, Easing.QUAD_IN).setCoefficient(3.5f).build())
                            .addMotion(0, velocity, 0)
                            .addTickActor(p -> p.setParticleSpeed(p.getParticleSpeed().scale(1f - random.nextFloat() * 0f)))
                            .enableNoClip()
                            .setDiscardFunction(SimpleParticleOptions.ParticleDiscardFunctionType.ENDING_CURVE_INVISIBLE)
                            .spawn(level, offsetPosition.x, offsetPosition.y, offsetPosition.z);
                }
            }
        }
    }


    public static final DemoBlock CONDENSED_DIRT = register(
            new DemoBlock(BlockBehaviour.Properties.of()),
            "condensed_dirt",
            true
    );


    public static final BlockEntityType<DemoBlockEntity> DEMO_BLOCK = register(
            "demo_block",
            BlockEntityType.Builder.of(DemoBlockEntity::new, CONDENSED_DIRT).build()
    );

}