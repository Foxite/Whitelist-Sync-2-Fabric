package pw.twpi.whitelistsync2.commands.whitelist;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import pw.twpi.whitelistsync2.WhitelistSync2;

public class CommandSync implements Command<ServerCommandSource> {
    private static final CommandSync CMD = new CommandSync();

    // Name of the command
    private static final String commandName = "sync";
    private static final int permissionLevel = 4;

    // Errors
    private static final SimpleCommandExceptionType DB_ERROR
            = new SimpleCommandExceptionType(new LiteralText("Error syncing whitelist database, please check console for details."));

    // Initial command "checks"
    public static ArgumentBuilder<ServerCommandSource, ?> register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        return CommandManager.literal(commandName)
                .requires(cs -> cs.hasPermissionLevel(permissionLevel))
                .executes(CMD);
    }

    // Command action
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        if(WhitelistSync2.whitelistService.copyDatabaseWhitelistedPlayersToLocal(context.getSource().getMinecraftServer())) {
            context.getSource().sendFeedback(new LiteralText("Local whitelist up to date with database."), false);
        } else {
            throw DB_ERROR.create();
        }

        return 0;
    }
}
