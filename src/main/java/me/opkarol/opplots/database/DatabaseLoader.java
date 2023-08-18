package me.opkarol.opplots.database;

import me.opkarol.opc.api.file.Configuration;
import me.opkarol.opc.api.tools.autostart.IDisable;
import me.opkarol.opc.api.tools.autostart.OpAutoDisable;
import me.opkarol.opc.api.utils.VariableUtil;
import me.opkarol.opplots.database.handler.DatabaseHandler;
import me.opkarol.opplots.database.handler.DatabaseHandlerFactory;

public class DatabaseLoader implements IDisable {
    private final DatabaseHandler databaseHandler;

    public DatabaseLoader(Configuration config) {
        OpAutoDisable.add(this);

        DatabaseType databaseType;
        try {
            databaseType = DatabaseType.valueOf(VariableUtil.getOrDefault(config.getString("databaseType"), "MYSQL").toUpperCase());
        } catch (IllegalArgumentException ignore) {
            databaseType = DatabaseType.MYSQL;
        }

        databaseHandler = DatabaseHandlerFactory.create(databaseType);
        databaseHandler.setConnectionSettings(config.getConfig().getConfigurationSection("connectionSettings"));
        databaseHandler.connect();
        databaseHandler.createTable();
    }

    @Override
    public void onDisable() {
        if (databaseHandler != null) {
            databaseHandler.disconnect();
        }
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }
}
