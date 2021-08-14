package pw.twpi.whitelistsync2.commands.op;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import pw.twpi.whitelistsync2.WhitelistSync2;

import java.util.Collection;

public class CommandDeop implements Command<ServerCommandSource> {
    private static final CommandDeop CMD = new CommandDeop();

    // Name of the command
    private static final String commandName = "deop";
    private static final int permissionLevel = 4;

    // Errors
    private static final DynamicCommandExceptionType DB_ERROR
            = new DynamicCommandExceptionType(name -> new LiteralMessage(String.format("Error removing %s from the op database, please check console for details.", name)));

    // Initial command "checks"
    public static ArgumentBuilder<ServerCommandSource, ?> register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        return CommandManager.literal(commandName)
                .requires(cs -> cs.hasPermissionLevel(permissionLevel))
                .then(CommandManager.argument("players", GameProfileArgumentType.gameProfile())
                        /* // TODO only suggest opped players
                        .suggests((context, suggestionsBuilder) -> {
                            return ISuggestionProvider.suggest(context.getSource().getServer().getPlayerList().getOpNames(), suggestionsBuilder);
                        })
                        //*/
                        .executes(CMD));
    }

    // Command action
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgumentType.getProfileArgument(context, "players");
        PlayerManager playerManager = context.getSource().getMinecraftServer().getPlayerManager();

        int i = 0;

        for (GameProfile gameProfile : players) {

            Text playerName = net.minecraft.text.Texts.toText(gameProfile);

            if (playerManager.isOperator(gameProfile)) {
                if(WhitelistSync2.whitelistService.removeOppedPlayer(gameProfile)) {
                    playerManager.removeFromOperators(gameProfile);
                    context.getSource().sendFeedback(new LiteralText(String.format("Deopped %s from database.", playerName)), true);
                    ++i;
                    // Everything is kosher
                } else {
                    // If something happens with the database stuff
                    throw DB_ERROR.create(playerName);
                }
            } else {
                // Player is not whitelisted
                context.getSource().sendFeedback(new LiteralText(String.format("%s is not opped.", playerName)), true);
            }
        }

        if (i > 0) {
            context.getSource().getMinecraftServer().kickNonWhitelistedPlayers(context.getSource());
        }
        return i;
    }
}
