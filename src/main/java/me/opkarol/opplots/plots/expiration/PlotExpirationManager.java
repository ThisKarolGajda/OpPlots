package me.opkarol.opplots.plots.expiration;

import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.managers.PluginManager;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.PlotRemover;
import me.opkarol.opplots.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static me.opkarol.opplots.webhook.DiscordWebhooks.sendPlotExpiredWebhook;

public class PlotExpirationManager {
    public PlotExpirationManager(PluginManager pluginManager) {
        List<Plot> toRemove = pluginManager.getPlotsDatabase().getPlotList()
                .stream()
                .filter(plot -> TimeUtils.hasTimePassed(plot.getExpiration()))
                .toList();

        if (toRemove.size() != 0) {
            for (Plot plot : toRemove) {
                if (plot != null) {
                    removePlot(plot, pluginManager);
                }
            }
        }

        TimeUtils.TimeUnit unit = TimeUtils.TimeUnit.HOUR;
        new OpRunnable(() -> pluginManager.getPlotsDatabase()
                .getPlotList().stream()
                .filter(plot -> TimeUtils.willTimePass(plot.getExpiration(), 1, unit))
                .forEach(plot -> new OpRunnable(() -> {
                    if (TimeUtils.hasTimePassed(plot.getExpiration())) {
                        removePlot(plot, pluginManager);
                    }
                }).runTaskLaterAsynchronously(TimeUtils.subtractFromCurrent(plot.getExpiration() + 10) / 50))
        ).runTaskTimerAsynchronously(0, unit.toSeconds() * 20L);
    }

    private void removePlot(Plot plot, PluginManager pluginManager) {
        PlotRemover.removePlot(plot, pluginManager);
        sendPlotExpiredWebhook(plot);
        String message = FormatUtils.formatMessage("#<447cfc>☁ &7Jakaś działka wygasła na lokalizacji: " + plot.getFamilyHomeLocation() + "!");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }

        if (plot.getOwner().isOnline()) {
            plot.getOwner().getPlayer().sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Twoja działka wygasła!"));
        }
    }
}
