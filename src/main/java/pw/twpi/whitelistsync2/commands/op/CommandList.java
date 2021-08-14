package pw.twpi.whitelistsync2.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import pw.twpi.whitelistsync2.Utilities;
import pw.twpi.whitelistsync2.WhitelistSync2;

public class CommandList implements Command<ServerCommandSource> {
    private static final CommandList CMD = new CommandList();

    // Name of the command
    private static final String commandName = "list";
    private static final int permissionLevel = 4;

    // Initial command "checks"
    public static ArgumentBuilder<ServerCommandSource, ?> register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        return CommandManager.literal(commandName)
                .requires(cs -> cs.hasPermissionLevel(permissionLevel))
                .executes(CMD);
    }

    // Command action
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(new LiteralText(Utilities.FormatOppedPlayersOutput(WhitelistSync2.whitelistService.getOppedPlayersFromDatabase())), false);
        return 0;
    }
}
