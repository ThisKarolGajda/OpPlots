package me.opkarol.opplots.managers;

import me.opkarol.opc.api.file.Configuration;
import org.bukkit.plugin.Plugin;

public class ConfigFile {
    private final Configuration configuration;

    public ConfigFile(Plugin plugin) {
        configuration = new Configuration(plugin, "config.yml");
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}

