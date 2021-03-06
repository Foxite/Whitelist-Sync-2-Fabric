package pw.twpi.whitelistsync2.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import pw.twpi.whitelistsync2.WhitelistSync2;
import pw.twpi.whitelistsync2.models.OppedPlayer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;


/**
 * Class to read json data from the server's ops.json file
 * @author Richard Nader, Jr. <rmnader@svsu.edu>
 */
public class OppedPlayersFileUtilities {
    
    private static JsonParser parser = new JsonParser();

    // Get Arraylist of opped players on server.
    public static ArrayList<OppedPlayer> getOppedPlayers(MinecraftServer server) {
        ArrayList<OppedPlayer> users = new ArrayList<>();

        // Get Json data
        getOppedPlayersFromFile(server).forEach((user) -> {
            String uuid = ((JsonObject) user).get("uuid").getAsString();
            String name = ((JsonObject) user).get("name").getAsString();

            // Create DTO
            OppedPlayer oppedPlayer = new OppedPlayer();
            oppedPlayer.setUuid(uuid);
            oppedPlayer.setName(name);
            oppedPlayer.setIsOp(true);

            
            users.add(oppedPlayer);
        });

        return users;
    }

    private static JsonArray getOppedPlayersFromFile(MinecraftServer server) {
        JsonArray oplist = null;
        try {
            // Read data as Json array from server directory
            oplist = (JsonArray) parser.parse(new FileReader(server.getRunDirectory().getPath() + "/ops.json"));
            
            // WhitelistSync2.LOGGER.debug("getOppedPlayersFromFile returned an array of " + oplist.size() + " entries.");
        } catch (FileNotFoundException e) {
            WhitelistSync2.LOGGER.error("ops.json file not found.");
            e.printStackTrace();
        } catch (JsonParseException e) {
            WhitelistSync2.LOGGER.error("ops.json parse error.");
            e.printStackTrace();
        }
        
        return oplist;
    }
    
}
