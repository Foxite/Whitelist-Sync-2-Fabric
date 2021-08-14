package pw.twpi.whitelistsync2.commands.op;

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

public class CommandCopyToDatabase implements Command<ServerCommandSource> {
    private static final CommandCopyToDatabase CMD = new CommandCopyToDatabase();

    // Name of the command
    private static final String commandName = "copyServerToDatabase";
    private static final int permissionLevel = 4;

    // Errors
    private static final SimpleCommandExceptionType DB_ERROR
            = new SimpleCommandExceptionType(new LiteralText("Error syncing local op list to database, please check console for details."));


    public static ArgumentBuilder<ServerCommandSource, ?> register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        return CommandManager.literal(commandName)
                .requires(cs -> cs.hasPermissionLevel(permissionLevel))
                .executes(CMD);
    }

    // Command action
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(WhitelistSync2.whitelistService.copyLocalOppedPlayersToDatabase()) {
            context.getSource().sendFeedback(new LiteralText("Pushed local op list to database."), false);
        } else {
            throw DB_ERROR.create();
        }

        return 0;
    }
}
