package me.opkarol.opplots.database.types;

import me.opkarol.opplots.database.handler.DatabaseHandler;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLDatabaseHandler extends DatabaseHandler {
    private Connection connection;
    private ConfigurationSection connectionSettings;

    @Override
    public void setConnectionSettings(ConfigurationSection connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    @Override
    public void connect() {
        connection = MariaADBndMySQLBridge.createConnection("mysql", connectionSettings);
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
