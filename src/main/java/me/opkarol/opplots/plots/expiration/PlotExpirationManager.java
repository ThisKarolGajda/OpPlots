package me.opkarol.opplots.plots.expiration;

import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.managers.PluginManager;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.PlotRemover;
import me.opkarol.opplots.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

import static me.opkarol.opplots.webhook.DiscordWebhooks.sendPlotExpiredWebhook;

public class PlotExpirationManager {
    public PlotExpirationManager(PluginManager pluginManager) {
        // Remove plots on startup
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

        // Task to expire plots
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

        // Task to notice player that plot is expiring in less than 4 days
        new OpRunnable(() -> {
            pluginManager.getPlotsDatabase()
                    .getPlotList().stream()
                    .filter(plot -> TimeUtils.subtractFromTimestamp(plot.getExpiration(), 4, TimeUtils.TimeUnit.DAY) < TimeUtils.getCurrent())
                    .forEach(plot -> {
                        OfflinePlayer offlinePlayer = plot.getOwner();
                        if (offlinePlayer.isOnline()) {
                            offlinePlayer.getPlayer().sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7UWAGA! Twoja działka wygasa: " + plot.getExpirationLeftString() + "! Przedłuż ją lub bezpowrotnie wygaśnie!"));
                        }
                    });
        }).runTaskTimerAsynchronously(0, TimeUtils.TimeUnit.MINUTE.toSeconds() * 20L * 15L);
    }

    private void removePlot(Plot plot, PluginManager pluginManager) {
        PlotRemover.removePlot(plot, pluginManager);
        sendPlotExpiredWebhook(plot);
        String message = FormatUtils.formatMessage("#<447cfc>&l☁ &7Jakaś działka wygasła na lokalizacji: " + plot.getFamilyHomeLocation() + "!");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }

        if (plot.getOwner().isOnline()) {
            plot.getOwner().getPlayer().sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Twoja działka wygasła!"));
        }
    }
}
