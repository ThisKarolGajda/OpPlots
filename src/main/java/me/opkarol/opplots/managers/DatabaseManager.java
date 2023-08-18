package me.opkarol.opplots.managers;

import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.database.DatabaseLoader;

public final class DatabaseManager {
    private final OpPlots plugin;
    private final DatabaseLoader databaseLoader;

    public DatabaseManager(OpPlots plugin) {
        this.plugin = plugin;
        this.databaseLoader = new DatabaseLoader(plugin.getFilesManager().getConfigFile().getConfiguration());
    }

    public OpPlots getPlugin() {
        return plugin;
    }

    public DatabaseLoader getDatabaseLoader() {
        return databaseLoader;
    }
}
