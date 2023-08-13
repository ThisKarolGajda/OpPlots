package me.opkarol.database.types;

import me.opkarol.database.handler.DatabaseHandler;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class H2DatabaseHandler implements DatabaseHandler {
    private Connection connection;
    private ConfigurationSection connectionSettings;

    @Override
    public void setConnectionSettings(ConfigurationSection connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    @Override
    public void connect() {
        String jdbcUrl = "jdbc:h2:" + connectionSettings.getString("databasePath");

        try {
            connection = DriverManager.getConnection(jdbcUrl,
                    connectionSettings.getString("username"),
                    connectionSettings.getString("password"));
        } catch (SQLException e) {
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
    public void createTable() {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS my_table (id INT AUTO_INCREMENT PRIMARY KEY, data VARCHAR(255))"
            );
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertData(String data) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO my_table (data) VALUES (?)");
            statement.setString(1, data);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> fetchData() {
        List<String> dataList = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT data FROM my_table");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dataList.add(resultSet.getString("data"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
