package me.opkarol.database.handler;

import me.opkarol.database.DatabaseType;
import me.opkarol.database.types.H2DatabaseHandler;
import me.opkarol.database.types.MariaDBDatabaseHandler;
import me.opkarol.database.types.MySQLDatabaseHandler;

public class DatabaseHandlerFactory {
    public static DatabaseHandler create(DatabaseType type) {
        return switch (type) {
            case H2 -> new H2DatabaseHandler();
            case MYSQL -> new MySQLDatabaseHandler();
            case MARIADB -> new MariaDBDatabaseHandler();
        };
    }
}