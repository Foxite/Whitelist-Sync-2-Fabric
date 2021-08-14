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

public class CommandAdd implements Command<ServerCommandSource> {
    private static final CommandAdd CMD = new CommandAdd();

    // Name of the command
    private static final String commandName = "add";
    private static final int permissionLevel = 4;

    // Errors
    private static final DynamicCommandExceptionType DB_ERROR
            = new DynamicCommandExceptionType(name -> new LiteralMessage(String.format("Error adding %s to the whitelist database, please check console for details.", name)));

    // Initial command "checks"
    public static ArgumentBuilder<ServerCommandSource, ?> register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        return CommandManager.literal(commandName)
                .requires(cs -> cs.hasPermissionLevel(permissionLevel))
                .then(CommandManager.argument("players", GameProfileArgumentType.gameProfile())
                    /* // TODO only suggest non-whitelisted players
                    .suggests((context, suggestionsBuilder) -> {
                        // Get server playerlist
                        PlayerList playerlist = context.getSource().getServer().getPlayerList();


                        return ISuggestionProvider.suggest(playerlist.getPlayers().stream()
                        // Filter by players in playerlist who are not whitelisted
                        .filter((playerEntity) -> {
                            return !playerlist.getWhiteList().isWhiteListed(playerEntity.getGameProfile());

                        // Map player names from returned filtered collection
                        }).map((playerEntity) -> {
                            return playerEntity.getGameProfile().getName();
                        }), suggestionsBuilder);
                    })//*/
                    .executes(CMD));
    }

    // Command action
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> players = GameProfileArgumentType.getProfileArgument(context, "players");
        Whitelist whiteList = context.getSource().getMinecraftServer().getPlayerManager().getWhitelist();

        int i = 0;

        for(GameProfile gameProfile : players) {

            Text playerName = net.minecraft.text.Texts.toText(gameProfile);

            if(!whiteList.isAllowed(gameProfile)) {
                // Add player to whitelist service
                if(WhitelistSync2.whitelistService.addWhitelistPlayer(gameProfile)) {
                    WhitelistEntry whitelistentry = new WhitelistEntry(gameProfile);
                    whiteList.add(whitelistentry);

                    context.getSource().sendFeedback(new LiteralText(String.format("Added %s to whitelist database.", playerName)), true);
                    ++i;
                    // Everything is kosher!
                } else {
                    // If something happens with the database stuff
                    throw DB_ERROR.create(playerName);
                }
            } else {
                // Player already whitelisted
                context.getSource().sendFeedback(new LiteralText(String.format("%s is already whitelisted.", playerName)), true);
            }
        }

        return i;
    }
}
