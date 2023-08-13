package me.opkarol.managers;

import me.opkarol.OpPlots;

public final class PluginManager {
    private final OpPlots plugin;

    public PluginManager(OpPlots plugin) {
        this.plugin = plugin;
    }

    public OpPlots getPlugin() {
        return plugin;
    }

}
