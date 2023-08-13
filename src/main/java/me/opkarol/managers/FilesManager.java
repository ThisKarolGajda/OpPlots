package me.opkarol.managers;

import me.opkarol.OpPlots;
import me.opkarol.database.DatabaseConfigFile;

public final class FilesManager {
    private final OpPlots plugin;
    private final DatabaseConfigFile databaseConfigFile;

    public FilesManager(OpPlots plugin) {
        this.plugin = plugin;
        databaseConfigFile = new DatabaseConfigFile(plugin);
    }

    public OpPlots getPlugin() {
        return plugin;
    }

    public DatabaseConfigFile getDatabaseConfigFile() {
        return databaseConfigFile;
    }
}
