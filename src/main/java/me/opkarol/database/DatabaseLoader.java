package me.opkarol.database;

import me.opkarol.database.handler.DatabaseHandler;
import me.opkarol.database.handler.DatabaseHandlerFactory;
import me.opkarol.opc.api.file.Configuration;
import me.opkarol.opc.api.tools.autostart.IDisable;
import me.opkarol.opc.api.tools.autostart.OpAutoDisable;

public class DatabaseLoader implements IDisable {
    private final DatabaseHandler databaseHandler;

    public DatabaseLoader(Configuration config) {
        OpAutoDisable.add(this);

        DatabaseType databaseType = DatabaseType.valueOf(config.getString("databaseType"));

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
}
