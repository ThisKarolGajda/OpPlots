package me.opkarol.managers;

import me.opkarol.OpPlots;
import me.opkarol.database.DatabaseLoader;

public final class DatabaseManager {
    private final OpPlots plugin;
    private final DatabaseLoader databaseLoader;

    public DatabaseManager(OpPlots plugin) {
        this.plugin = plugin;
        this.databaseLoader = new DatabaseLoader(plugin.getFilesManager().getDatabaseConfigFile().getConfiguration());
    }

    public OpPlots getPlugin() {
        return plugin;
    }

    public DatabaseLoader getDatabaseLoader() {
        return databaseLoader;
    }
}
