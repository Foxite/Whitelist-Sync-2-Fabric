package pw.twpi.whitelistsync2.commands.whitelist;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import pw.twpi.whitelistsync2.WhitelistSync2;

import java.util.Collection;

public class CommandRemove implements Command<ServerCommandSource> {
    private static final CommandRemove CMD = new CommandRemove();

    // Name of the command
    private static final String commandName = "remove";
    private static final int permissionLevel = 4;

    // Errors
    private static final DynamicCommandExceptionType DB_ERROR
            = new DynamicCommandExceptionType(name -> new LiteralMessage(String.format("Error removing %s from the whitelist database, please check console for details.", name)));

    // Initial command "checks"
    public static ArgumentBuilder<ServerCommandSource, ?> register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        return CommandManager.literal(commandName)
                .requires(cs -> cs.hasPermissionLevel(permissionLevel))
                .then(CommandManager.argument("players", GameProfileArgumentType.gameProfile())
                    /* // TODO only suggest whitelisted players
                    .suggests((context, suggestionsBuilder) -> {
                        return ISuggestionProvider.suggest(context.getSource().getServer().getPlayerList().getWhiteList().getUserList(), suggestionsBuilder);
                    })//*/
                    .executes(CMD));
    }

    // Command action
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgumentType.getProfileArgument(context, "players");
        Whitelist whiteList = context.getSource().getMinecraftServer().getPlayerManager().getWhitelist();

        int i = 0;

        for (GameProfile gameProfile : players) {

            Text playerName = net.minecraft.text.Texts.toText(gameProfile);

            if (whiteList.isAllowed(gameProfile)) {
                if(WhitelistSync2.whitelistService.removeWhitelistPlayer(gameProfile)) {
                    WhitelistEntry whitelistentry = new WhitelistEntry(gameProfile);
                    whiteList.remove(whitelistentry);
                    context.getSource().sendFeedback(new LiteralText(String.format("Removed %s from whitelist database.", playerName)), true);
                    ++i;
                    // Everything is kosher
                } else {
                    // If something happens with the database stuff
                    throw DB_ERROR.create(playerName);
                }
            } else {
                // Player is not whitelisted
                context.getSource().sendFeedback(new LiteralText(String.format("%s is not whitelisted.", playerName)), true);
            }
        }

        if (i > 0) {
            context.getSource().getMinecraftServer().kickNonWhitelistedPlayers(context.getSource());
        }
        return i;
    }
}
