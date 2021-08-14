package pw.twpi.whitelistsync2.commands.op;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class OpCommands {
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        // Register wl commands
        LiteralCommandNode<ServerCommandSource> cmdWl = dispatcher.register(
                CommandManager.literal("wlop")
                    .then(CommandList.register(dispatcher, dedicated))
                    .then(CommandOp.register(dispatcher, dedicated))
                    .then(CommandDeop.register(dispatcher, dedicated))
                    .then(CommandSync.register(dispatcher, dedicated))
                    .then(CommandCopyToDatabase.register(dispatcher, dedicated))
        );

    }
}
