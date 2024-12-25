package team.lodestar.lodestone.helpers;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.Block;

import java.util.*;

import static team.lodestar.lodestone.LodestoneLib.LOGGER;

public class NBTHelper {

    public static BlockPos readBlockPos(Tag tag) {
        return BlockPos.CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial(LOGGER::error).orElse(null);
    }

    public static Tag saveBlockPos(BlockPos pos) {
        return BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).resultOrPartial(LOGGER::error).orElseThrow();
    }
}
