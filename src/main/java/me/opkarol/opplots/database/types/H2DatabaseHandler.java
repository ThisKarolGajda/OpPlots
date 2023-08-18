package me.opkarol.opplots.database.types;

import me.opkarol.opplots.database.handler.DatabaseHandler;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2DatabaseHandler extends DatabaseHandler {
    private Connection connection;
    private ConfigurationSection connectionSettings;

    @Override
    public void setConnectionSettings(ConfigurationSection connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    @Override
    public void connect() {
        try {
            Class.forName("org.h2.Driver"); // Load the H2 JDBC driver
            String jdbcUrl = "jdbc:h2:" + connectionSettings.getString("databasePath");
            connection = DriverManager.getConnection(jdbcUrl,
                    connectionSettings.getString("username"),
                    connectionSettings.getString("password"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
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
