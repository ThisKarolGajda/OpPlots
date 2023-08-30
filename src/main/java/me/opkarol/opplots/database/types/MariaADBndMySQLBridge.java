package me.opkarol.opplots.database.types;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.SQLException;

public class MariaADBndMySQLBridge {

    public static Connection createConnection(String jdbc, ConfigurationSection connectionSettings) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:" + jdbc + "://" +
                connectionSettings.getString("host") + ":" +
                connectionSettings.getInt("port") + "/" +
                connectionSettings.getString("database"));
        config.setUsername(connectionSettings.getString("username"));
        config.setPassword(connectionSettings.getString("password"));
        config.setMaximumPoolSize(10);
        HikariDataSource dataSource = new HikariDataSource(config);

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dataSource.close();
        return null;
    }
}
