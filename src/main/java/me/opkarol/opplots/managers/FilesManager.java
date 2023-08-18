package me.opkarol.opplots.managers;

import me.opkarol.opplots.OpPlots;

public final class FilesManager {
    private final OpPlots plugin;
    private final ConfigFile configFile;

    public FilesManager(OpPlots plugin) {
        this.plugin = plugin;
        configFile = new ConfigFile(plugin);
    }

    public OpPlots getPlugin() {
        return plugin;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }
}
