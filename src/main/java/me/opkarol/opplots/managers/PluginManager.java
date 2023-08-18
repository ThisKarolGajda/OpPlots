package me.opkarol.opplots.managers;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.WorldGuard;
import me.opkarol.opc.api.extensions.Vault;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.database.PlotsDatabase;
import me.opkarol.opplots.plots.expiration.PlotExpirationManager;
import me.opkarol.opplots.plots.listener.PlayerRegionChangingListener;
import me.opkarol.opplots.plots.listener.PlotOutsideBorderListener;
import me.opkarol.opplots.worldguard.WorldGuardAPI;
import me.opkarol.opplots.worldguard.events.WorldGuardEntry;
import org.bukkit.Bukkit;

public final class PluginManager {
    private final OpPlots plugin;
    private final PlotsDatabase plotsDatabase;
    private final WorldGuardAPI worldGuardAPI;
    private final Vault vault;
    private final Essentials essentials;

    public PluginManager(OpPlots plugin) {
        this.plugin = plugin;
        this.plotsDatabase = new PlotsDatabase(plugin);
        this.worldGuardAPI = new WorldGuardAPI(WorldGuard.getInstance().getPlatform().getRegionContainer());
        this.vault = new Vault();
        this.essentials = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

        WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(WorldGuardEntry.factory, null);

        new PlotOutsideBorderListener().runListener();
        new PlayerRegionChangingListener().runListener();
        new PlotExpirationManager(this);
    }

    public OpPlots getPlugin() {
        return plugin;
    }

    public PlotsDatabase getPlotsDatabase() {
        return plotsDatabase;
    }

    public WorldGuardAPI getWorldGuardAPI() {
        return worldGuardAPI;
    }

    public Vault getVault() {
        return vault;
    }

    public Essentials getEssentials() {
        return essentials;
    }
}
