package pw.twpi.whitelistsync2.commands.whitelist;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import pw.twpi.whitelistsync2.WhitelistSync2;

public class WhitelistCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        // Register wl commands
        LiteralCommandNode<ServerCommandSource> cmdWl = dispatcher.register(
                CommandManager.literal("wl")
                    .then(CommandList.register(dispatcher, dedicated))
                    .then(CommandAdd.register(dispatcher, dedicated))
                    .then(CommandRemove.register(dispatcher, dedicated))
                    .then(CommandSync.register(dispatcher, dedicated))
                    .then(CommandCopyToDatabase.register(dispatcher, dedicated))
        );

        // Allow "whitelistsync2" as an alias
        dispatcher.register(CommandManager.literal(WhitelistSync2.MODID).redirect(cmdWl));
    }
}
