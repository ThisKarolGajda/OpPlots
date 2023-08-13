package me.opkarol.database.handler;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public interface DatabaseHandler {
    void setConnectionSettings(ConfigurationSection connectionSettings);

    void connect();

    void disconnect();

    void createTable();

    void insertData(String data);

    List<String> fetchData();
}