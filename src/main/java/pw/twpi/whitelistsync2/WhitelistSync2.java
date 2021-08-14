package pw.twpi.whitelistsync2;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pw.twpi.whitelistsync2.commands.op.OpCommands;
import pw.twpi.whitelistsync2.commands.whitelist.WhitelistCommands;
import pw.twpi.whitelistsync2.services.*;

public class WhitelistSync2 implements ModInitializer, DedicatedServerModInitializer {
	public static final String MODID = "whitelistsync2";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static BaseService whitelistService;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");
	}

	@Override
	public void onInitializeServer() {
		boolean setupSuccessful = true;

		// Load config
		ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();


		// Server filepath
		//net.fabricmc.loader.api.FabricLoader.getInstance().getGameInstance()

		LOGGER.info("----------------------------------------------");
		LOGGER.info("---------------WHITELIST SYNC 2---------------");
		LOGGER.info("----------------------------------------------");

		switch (config.databaseMode) {
			case SQLITE:
				whitelistService = new SqLiteService();
				break;
			case MYSQL:
				whitelistService = new MySqlService();
				break;
			case POSTGRESQL:
				whitelistService = new PostgreSqlService();
				break;
			default:
				LOGGER.error("Please check what WHITELIST_MODE is set in the config and make sure it is set to a supported mode.");
				setupSuccessful = false;
				break;
		}

		if(!whitelistService.initializeDatabase() || !setupSuccessful) {
			LOGGER.error("Error initializing whitelist sync database. Disabling mod functionality. Please correct errors and restart.");
		} else {
			// Database is setup!

			// Check if whitelisting is enabled.
			ServerTickEvents.END_SERVER_TICK.register(new SyncThread(whitelistService, config));
//			if (!event.getServer().getPlayerList().isUsingWhitelist()) {
//				LOGGER.info("Oh no! I see whitelisting isn't enabled in the server properties. "
//						+ "I assume this is not intentional, I'll enable it for you!");
//				event.getServer().getPlayerList().setUsingWhiteList(true);
//			}

			//StartSyncThread(event.getServer(), whitelistService, config);

			CommandRegistrationCallback.EVENT.register(WhitelistCommands::register);

			if(config.syncOpList) {
				LOGGER.info("Opped Player Sync is enabled");
				CommandRegistrationCallback.EVENT.register(OpCommands::register);
			} else {
				LOGGER.info("Opped Player Sync is disabled");
			}
		}


		LOGGER.info("----------------------------------------------");
		LOGGER.info("----------------------------------------------");
		LOGGER.info("----------------------------------------------");
	}
}
