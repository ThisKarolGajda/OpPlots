package me.opkarol.opplots.database;

import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opplots.database.handler.DatabaseHandler;

public class KeepAliveRunnable {
    public KeepAliveRunnable(DatabaseHandler databaseHandler) {
        // Run every 15 minutes = 18,000 ticks
        new OpRunnable(databaseHandler::fetchDummyData)
                .runTaskTimerAsynchronously(18000L);
    }
}
