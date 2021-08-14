package pw.twpi.whitelistsync2.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import pw.twpi.whitelistsync2.WhitelistSync2;
import pw.twpi.whitelistsync2.models.WhitelistedPlayer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;


/**
 * Class to read json data from the server's whitelist.json file
 * @author Richard Nader, Jr. <rmnader@svsu.edu>
 */
public class WhitelistedPlayersFileUtilities {

    private static JsonParser parser = new JsonParser();

    // Get Arraylist of whitelisted players on server.
    public static ArrayList<WhitelistedPlayer> getWhitelistedPlayers(MinecraftServer server) {
        ArrayList<WhitelistedPlayer> users = new ArrayList<>();

        // Get Json data
        getWhitelistedPlayersFromFile(server).forEach((user) -> {
            String uuid = ((JsonObject) user).get("uuid").getAsString();
            String name = ((JsonObject) user).get("name").getAsString();

            // Create DTO
            WhitelistedPlayer whitelistedPlayer = new WhitelistedPlayer();
            whitelistedPlayer.setUuid(uuid);
            whitelistedPlayer.setName(name);
            whitelistedPlayer.setWhitelisted(true);


            users.add(whitelistedPlayer);
        });

        return users;
    }

    private static JsonArray getWhitelistedPlayersFromFile(MinecraftServer server) {
        JsonArray whitelist = null;
        try {
            // Read data as Json array from server directory
            whitelist = (JsonArray) parser.parse(new FileReader(server.getRunDirectory().getPath() + "/whitelist.json"));

            // WhitelistSync2.LOGGER.debug("getWhitelistedPlayersFromFile returned an array of " + whitelist.size() + " entries.");
        } catch (FileNotFoundException e) {
            WhitelistSync2.LOGGER.error("whitelist.json file not found.");
            e.printStackTrace();
        } catch (JsonParseException e) {
            WhitelistSync2.LOGGER.error("whitelist.json parse error.");
            e.printStackTrace();
        }

        return whitelist;
    }

}
