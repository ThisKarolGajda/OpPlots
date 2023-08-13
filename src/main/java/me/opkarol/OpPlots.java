package me.opkarol;

import me.opkarol.managers.DatabaseManager;
import me.opkarol.managers.FilesManager;
import me.opkarol.managers.PluginManager;
import me.opkarol.opc.api.plugins.OpPlugin;

public class OpPlots extends OpPlugin {
    private static OpPlots instance;
    private final PluginManager pluginManager;
    private final DatabaseManager databaseManager;
    private final FilesManager filesManager;

    {
        instance = this;
    }

    public OpPlots() {
        filesManager = new FilesManager(this);
        databaseManager = new DatabaseManager(this);
        pluginManager = new PluginManager(this);
    }

    public static OpPlots getInstance() {
        return instance;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public FilesManager getFilesManager() {
        return filesManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}