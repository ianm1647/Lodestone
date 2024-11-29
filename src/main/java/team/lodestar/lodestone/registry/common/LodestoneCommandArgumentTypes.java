package team.lodestar.lodestone.registry.common;

import com.mojang.brigadier.arguments.ArgumentType;
import io.github.fabricators_of_create.porting_lib.util.DeferredRegister;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.command.arguments.WorldEventInstanceArgument;
import team.lodestar.lodestone.command.arguments.WorldEventTypeArgument;

import java.util.function.Supplier;

public class LodestoneCommandArgumentTypes {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, LodestoneLib.LODESTONE);

    public static final Supplier<ArgumentTypeInfo<WorldEventTypeArgument, ?>> WORLD_EVENT_TYPE_ARG = COMMAND_ARGUMENT_TYPES.register("world_event_type_arg",
            () -> registerByClass(WorldEventTypeArgument.class, SingletonArgumentInfo.contextFree(WorldEventTypeArgument::worldEventType)));

    public static final Supplier<ArgumentTypeInfo<WorldEventInstanceArgument, ?>> WORLD_EVENT_INSTANCE_ARG = COMMAND_ARGUMENT_TYPES.register("world_event_instance_arg",
            () -> registerByClass(WorldEventInstanceArgument.class, SingletonArgumentInfo.contextFree(WorldEventInstanceArgument::worldEventInstance)));

    public static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> I registerByClass(Class<A> infoClass, I argumentTypeInfo) {
        ArgumentTypeInfos.BY_CLASS.put(infoClass, argumentTypeInfo);
        return argumentTypeInfo;
    }

    public static void registerArgumentTypes() {
        COMMAND_ARGUMENT_TYPES.register();
    }
}
