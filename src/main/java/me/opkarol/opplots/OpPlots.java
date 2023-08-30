package me.opkarol.opplots;

import me.opkarol.opc.api.plugins.OpPlugin;
import me.opkarol.opplots.commands.PlotCommand;
import me.opkarol.opplots.database.KeepAliveRunnable;
import me.opkarol.opplots.managers.DatabaseManager;
import me.opkarol.opplots.managers.FilesManager;
import me.opkarol.opplots.managers.PluginManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;

import java.util.concurrent.Callable;

public class OpPlots extends OpPlugin {
    private static OpPlots instance;
    private PluginManager pluginManager;
    private DatabaseManager databaseManager;
    private FilesManager filesManager;

    {
        instance = this;
    }

    public static OpPlots getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        filesManager = new FilesManager(this);
        databaseManager = new DatabaseManager(this);
        pluginManager = new PluginManager(this);

        Metrics metrics = new Metrics(this, 19615);
        metrics.addCustomChart(new SingleLineChart("plots", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return getPluginManager().getPlotsDatabase().getPlotList().size();
            }
        }));

        new KeepAliveRunnable(getDatabaseManager().getDatabaseLoader().getDatabaseHandler());

    }

    @Override
    public Object[] registerCommands() {
        return new Object[]{new PlotCommand(),};
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public FilesManager getFilesManager() {
        return filesManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}