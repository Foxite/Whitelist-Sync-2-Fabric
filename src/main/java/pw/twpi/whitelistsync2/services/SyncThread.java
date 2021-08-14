package pw.twpi.whitelistsync2.services;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import pw.twpi.whitelistsync2.ModConfig;
import pw.twpi.whitelistsync2.WhitelistSync2;

/**
 * 
 * @author Richard Nader, Jr. <rmnader@svsu.edu>
 */
public class SyncThread implements ServerTickEvents.EndTick {
    
    private final BaseService service;
    private final ModConfig config;

    private int tickCounter;
    private int tickInterval;

    public SyncThread(BaseService service, ModConfig config) {
        this.service = service;
        this.config = config;

        // synctimer is in milliseconds.
        // So convert that to ticks, or 0.05-second intervals.
        this.tickInterval = config.syncTimer * 20 / 1000;
    }
    
    @Override
    public void onEndTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter >= tickInterval) {
            tickCounter = 0;
            syncLists(server);
        }
    }

    private void syncLists(MinecraftServer server) {
        service.copyDatabaseWhitelistedPlayersToLocal(server);

        if (config.syncOpList) {
            service.copyDatabaseOppedPlayersToLocal(server);
        }
    }

}
