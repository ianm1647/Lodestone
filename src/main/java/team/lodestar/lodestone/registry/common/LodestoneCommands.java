package team.lodestar.lodestone.registry.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import team.lodestar.lodestone.command.*;

import static team.lodestar.lodestone.LodestoneLib.LODESTONE;

public class LodestoneCommands {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(Commands.literal("lode")
                        .then(DevWorldSetupCommand.register())
                        .then(Commands.literal("worldevent")
                                .then(RemoveActiveWorldEventsCommand.register())
                                .then(ListActiveWorldEventsCommand.register())
                                .then(GetDataWorldEventCommand.register())
                                .then(FreezeActiveWorldEventsCommand.register())
                                .then(UnfreezeActiveWorldEventsCommand.register())
                        )
        );
        dispatcher.register(Commands.literal(LODESTONE)
                .redirect(cmd));
    }
}