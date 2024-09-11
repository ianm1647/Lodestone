package team.lodestar.lodestone.registry.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import team.lodestar.lodestone.systems.worldgen.ChancePlacementFilter;
import team.lodestar.lodestone.systems.worldgen.DimensionPlacementFilter;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class LodestonePlacementFillers {

    public static PlacementModifierType<ChancePlacementFilter> CHANCE;
    public static PlacementModifierType<DimensionPlacementFilter> DIMENSION;

    @SubscribeEvent
    public static void registerTypes(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CHANCE = register("lodestone:chance", ChancePlacementFilter.CODEC);
            DIMENSION = register("lodestone:dimension", DimensionPlacementFilter.CODEC);
        });
    }

    private static <P extends PlacementModifier> PlacementModifierType<P> register(String pName, MapCodec<P> pCodec) {
        return Registry.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, pName, () -> pCodec);
    }
}