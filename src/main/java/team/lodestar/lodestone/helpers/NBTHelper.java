package team.lodestar.lodestone.helpers;

import net.minecraft.core.*;
import net.minecraft.nbt.*;

import java.util.*;

import static team.lodestar.lodestone.LodestoneLib.LOGGER;

public class NBTHelper {

    public static BlockPos readBlockPos(CompoundTag tag) {
        return BlockPos.CODEC.parse(NbtOps.INSTANCE, tag).resultOrPartial(LOGGER::error).orElse(null);
    }

    public static CompoundTag saveBlockPos(BlockPos pos) {
        return saveBlockPos(new CompoundTag(), pos);
    }

    public static CompoundTag saveBlockPos(CompoundTag tag, BlockPos pos) {
        BlockPos.CODEC
                .encodeStart(NbtOps.INSTANCE, pos)
                .resultOrPartial(LOGGER::error)
                .ifPresent(p -> tag.put("raid_omen_position", p));
        return tag;
    }
}
