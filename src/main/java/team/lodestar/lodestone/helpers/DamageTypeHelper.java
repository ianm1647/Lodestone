package team.lodestar.lodestone.helpers;

import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.*;

import javax.annotation.*;

public class DamageTypeHelper {

    public static DamageSource create(Level level, ResourceKey<DamageType> damageType) {
        return create(level, damageType, null, null);
    }

    public static DamageSource create(ResourceKey<DamageType> damageType, Entity source) {
        return create(source.level(), damageType, source);
    }

    public static DamageSource create(Level level, ResourceKey<DamageType> damageType, @Nullable Entity source) {
        return create(level, damageType, source, source);
    }

    public static DamageSource create(ResourceKey<DamageType> damageType, @Nullable Entity direct, Entity source) {
        return create(source.level(), damageType, direct, source);
    }

    public static DamageSource create(Level level, ResourceKey<DamageType> damageType, @Nullable Entity direct, @Nullable Entity source) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), direct, source);
    }
}
