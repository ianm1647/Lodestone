package team.lodestar.lodestone.helpers;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.function.Function;

import static net.minecraft.nbt.Tag.TAG_INT;
import static team.lodestar.lodestone.LodestoneLib.LOGGER;

public class NBTHelper {

    public static BlockPos readBlockPos(CompoundTag tag, String listTagName) {
        return readBlockPos(tag.getList(listTagName, TAG_INT));
    }
    public static BlockPos readBlockPos(ListTag tag) {
        return BlockPos.CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial(LOGGER::error).orElse(null);
    }

    public static Tag saveBlockPos(BlockPos pos) {
        return BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).resultOrPartial(LOGGER::error).orElseThrow();
    }
}
