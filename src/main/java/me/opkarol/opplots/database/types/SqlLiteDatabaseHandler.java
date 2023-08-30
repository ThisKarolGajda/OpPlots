package me.opkarol.opplots.database.types;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.database.handler.DatabaseHandler;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlLiteDatabaseHandler extends DatabaseHandler {
    private Connection connection;

    @Override
    public void setConnectionSettings(ConfigurationSection connectionSettings) {
    }

    @Override
    public void createTable() {
        try {
            PreparedStatement statement = getConnection().prepareStatement(
                    "CREATE TABLE IF NOT EXISTS plots (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "owner TEXT NOT NULL, " +
                    "creationDate TEXT NOT NULL, " +
                    "name TEXT, " +
                    "members TEXT, " +
                    "ignored TEXT, " +
                    "upgrades TEXT, " +
                    "settings TEXT, " +
                    "expiration TEXT NOT NULL, " +
                    "home TEXT" +
                    ");"
            );
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + OpPlots.getInstance().getDataFolder().getAbsolutePath() + "/plots.db");
        config.setMaximumPoolSize(10);
        HikariDataSource dataSource = new HikariDataSource(config);

        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If an exception occurs, close the dataSource
        dataSource.close();
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
