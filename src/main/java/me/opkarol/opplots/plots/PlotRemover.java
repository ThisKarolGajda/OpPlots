package me.opkarol.opplots.plots;

import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.managers.PluginManager;

import java.util.UUID;

import static me.opkarol.opplots.plots.Plot.PLOTS_WORLD;
import static me.opkarol.opplots.webhook.DiscordWebhooks.sendPlotRemovedWebhook;

public class PlotRemover {

    public static void removePlotFromOwner(UUID uuid) {
        OpPlots.getInstance()
                .getPluginManager()
                .getPlotsDatabase()
                .getOwnerPlot(uuid)
                .ifPresent(PlotRemover::removePlot);
    }

    public static void removePlot(Plot plot, PluginManager pluginManager) {
        removePlot(plot, pluginManager, false);
    }

    public static void removePlot(Plot plot, PluginManager pluginManager, boolean sendWebhook) {
        if (sendWebhook) {
            sendPlotRemovedWebhook(plot);
        }

        plot.getSettings().removeData(plot);
        plot.getUpgrades().removeData(plot);
        pluginManager.getPlotsDatabase().removePlot(plot);
        pluginManager.getWorldGuardAPI().removeRegion(PLOTS_WORLD, plot.getRegionIdentifier());
        pluginManager.getWorldGuardAPI().removeRegion(PLOTS_WORLD, plot.getSupportRegionIdentifier());
        pluginManager.getWorldGuardAPI().removeRegion(PLOTS_WORLD, plot.getSafeAreaRegionIdentifier());
    }

    public static void removePlot(Plot plot) {
        PluginManager pluginManager = OpPlots.getInstance().getPluginManager();
        removePlot(plot, pluginManager, true);
    }
}
