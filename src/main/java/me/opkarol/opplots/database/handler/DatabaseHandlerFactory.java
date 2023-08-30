package me.opkarol.opplots.database.handler;

import me.opkarol.opplots.database.DatabaseType;
import me.opkarol.opplots.database.types.H2DatabaseHandler;
import me.opkarol.opplots.database.types.MariaDBDatabaseHandler;
import me.opkarol.opplots.database.types.MySQLDatabaseHandler;
import me.opkarol.opplots.database.types.SqlLiteDatabaseHandler;

public class DatabaseHandlerFactory {
    public static DatabaseHandler create(DatabaseType type) {
        return switch (type) {
            case H2 -> new H2DatabaseHandler();
            case MYSQL -> new MySQLDatabaseHandler();
            case MARIADB -> new MariaDBDatabaseHandler();
            case SQLITE -> new SqlLiteDatabaseHandler();
        };
    }
}