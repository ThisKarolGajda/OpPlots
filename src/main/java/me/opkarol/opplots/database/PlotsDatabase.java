package me.opkarol.opplots.database;

import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.plots.Plot;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlotsDatabase {
    private final List<Plot> plotList;
    private final OpPlots plugin;

    public PlotsDatabase(OpPlots plugin) {
        this.plugin = plugin;
        this.plotList = plugin.getDatabaseManager().getDatabaseLoader().getDatabaseHandler().fetchData();
    }

    public void addPlot(Plot plot) {
        plotList.add(plot);
        plugin.getDatabaseManager().getDatabaseLoader().getDatabaseHandler().insertData(plot);
    }

    public void updatePlotInDatabase(Plot plot) {
        plugin.getDatabaseManager().getDatabaseLoader().getDatabaseHandler().insertData(plot);
    }

    public void removePlot(Plot plot) {
        plotList.removeIf(plot1 -> plot1.getOwnerUUID().equals(plot.getOwnerUUID()));
        plugin.getDatabaseManager().getDatabaseLoader().getDatabaseHandler().delete(plot);
    }

    public Optional<Plot> getOwnerPlot(UUID uuid) {
        return plotList.stream()
                .filter(plot -> plot.getOwnerUUID().equals(uuid))
                .findAny();
    }

    public Optional<Plot> getOwnerPlot(OfflinePlayer player) {
        return getOwnerPlot(player.getUniqueId());
    }

    public List<Plot> getAllPlotsFrom(UUID uuid) {
        return plotList.stream()
                .filter(plot -> plot.isAdded(uuid))
                .toList();
    }

    public List<Plot> getAllPlotsFrom(OfflinePlayer player) {
        return getAllPlotsFrom(player.getUniqueId());
    }

    public Optional<Plot> getPlotFromRegionIdentifier(String regionIdentifier) {
        return plotList.stream()
                .filter(plot -> plot.getRegionIdentifier().equals(regionIdentifier))
                .findAny();
    }

    public List<Plot> getPlotList() {
        return plotList;
    }
}
