package me.opkarol.database;

import me.opkarol.opc.api.file.Configuration;
import org.bukkit.plugin.Plugin;

public class DatabaseConfigFile {
    private final Configuration configuration;

    public DatabaseConfigFile(Plugin plugin) {
        configuration = new Configuration(plugin, "database.yml");
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
