package me.opkarol.opplots.database.handler;

import me.opkarol.opc.api.tools.location.OpSerializableLocation;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.settings.PlotSettings;
import me.opkarol.opplots.plots.upgrades.PlotUpgrades;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DatabaseHandler {
    public abstract void setConnectionSettings(ConfigurationSection connectionSettings);

    public abstract void connect();

    public abstract void disconnect();

    public abstract Connection getConnection();

    public void createTable() {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS plots (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "owner VARCHAR(36) NOT NULL, " +
                            "creationDate VARCHAR(255) NOT NULL, " +
                            "name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
                            "members MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
                            "ignored MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
                            "upgrades VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
                            "settings VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                            "expiration VARCHAR(255) NOT NULL," +
                            "home VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci" +
                            ");"
            );
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertData(Plot plot) {
        Connection connection = getConnection();
        try {
            String ownerUUID = plot.getOwnerUUID().toString();
            String membersUUIDs = joinUUIDList(plot.getMembers());
            String ignoredUUIDs = joinUUIDList(plot.getIgnored());
            String upgradesJSON = plot.getUpgrades().toString();
            String settingsJSON = plot.getSettings().toString();
            String homeLocation = plot.getHomeLocation().toString();
            String expiration = String.valueOf(plot.getExpiration());

            PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE plots SET " +
                            "creationDate = ?, " +
                            "name = ?, " +
                            "members = ?, " +
                            "ignored = ?, " +
                            "upgrades = ?, " +
                            "settings = ?, " +
                            "expiration = ?, " +
                            "home = ? " +
                            "WHERE owner = ?;");
            updateStatement.setString(1, plot.getCreationDate());
            updateStatement.setString(2, plot.getName());
            updateStatement.setString(3, membersUUIDs);
            updateStatement.setString(4, ignoredUUIDs);
            updateStatement.setString(5, upgradesJSON);
            updateStatement.setString(6, settingsJSON);
            updateStatement.setString(7, expiration);
            updateStatement.setString(8, homeLocation);
            updateStatement.setString(9, ownerUUID);

            if (updateStatement.executeUpdate() == 0) {
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO plots " +
                                "(owner, creationDate, name, members, ignored, upgrades, settings, expiration, home) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
                insertStatement.setString(1, ownerUUID);
                insertStatement.setString(2, plot.getCreationDate());
                insertStatement.setString(3, plot.getName());
                insertStatement.setString(4, membersUUIDs);
                insertStatement.setString(5, ignoredUUIDs);
                insertStatement.setString(6, upgradesJSON);
                insertStatement.setString(7, settingsJSON);
                insertStatement.setString(8, expiration);
                insertStatement.setString(9, homeLocation);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<Plot> fetchData() {
        List<Plot> plotList = new ArrayList<>();
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM plots;");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UUID owner = UUID.fromString(resultSet.getString("owner"));
                String creationDate = resultSet.getString("creationDate");
                String name = resultSet.getString("name");
                List<UUID> members = splitUUIDList(resultSet.getString("members"));
                List<UUID> ignored = splitUUIDList(resultSet.getString("ignored"));
                PlotUpgrades upgrades = PlotUpgrades.fromString(resultSet.getString("upgrades"));
                PlotSettings settings = PlotSettings.fromString(resultSet.getString("settings"));
                long expiration = Long.parseLong(resultSet.getString("expiration"));
                OpSerializableLocation homeLocation = new OpSerializableLocation(resultSet.getString("home"));

                Plot plot = new Plot(owner, creationDate, name, members, ignored, upgrades, settings, expiration, homeLocation);
                upgrades.registerListeners(plot);
                settings.registerListeners(plot);

                plotList.add(plot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plotList;
    }

    public void fetchDummyData() {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM plots where owner = ?;");
            statement.setString(1, "");
            statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Plot plot) {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM plots WHERE owner = ?;");
            statement.setString(1, plot.getOwnerUUID().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<UUID> splitUUIDList(String uuidListString) {
        List<UUID> uuidList = new ArrayList<>();
        String[] uuidStrings = uuidListString.split(",");
        for (String uuidString : uuidStrings) {
            if (uuidString.isEmpty()) {
                continue;
            }
            uuidList.add(UUID.fromString(uuidString));
        }
        return uuidList;
    }

    private String joinUUIDList(List<UUID> uuidList) {
        List<String> uuidStringList = new ArrayList<>();
        for (UUID uuid : uuidList) {
            uuidStringList.add(uuid.toString());
        }
        return String.join(",", uuidStringList);
    }
}