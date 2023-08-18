package me.opkarol.opplots;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import me.opkarol.opplots.database.PlotsDatabase;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.worldguard.WorldGuardAPI;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class OpPlotsAPI {
    private static final OpPlots plugin = OpPlots.getInstance();

    public static boolean isOwner(UUID uuid) {
        return plugin.getPluginManager()
                .getPlotsDatabase()
                .getOwnerPlot(uuid)
                .stream().findAny()
                .isPresent();
    }

    public static boolean isOwner(OfflinePlayer player) {
        return isOwner(player.getUniqueId());
    }

    public static boolean isAdded(UUID uuid) {
        return plugin.getPluginManager()
                .getPlotsDatabase()
                .getPlotList()
                .stream()
                .anyMatch(plot -> plot.isAdded(uuid));
    }

    public static boolean isAdded(OfflinePlayer offlinePlayer) {
        return isAdded(offlinePlayer.getUniqueId());
    }

    public static Optional<Plot> getOwnerPlot(UUID uuid) {
        return plugin.getPluginManager()
                .getPlotsDatabase()
                .getOwnerPlot(uuid)
                .stream().findAny();
    }

    public static Optional<Plot> getOwnerPlot(OfflinePlayer player) {
        return getOwnerPlot(player.getUniqueId());
    }

    public static void displayBorder(Plot plot, Player player) {
        plot.displayBorder(player, 10);
    }

    public static Optional<Plot> getPlotFromLocation(Location location) {
        WorldGuardAPI worldGuardAPI = plugin.getPluginManager().getWorldGuardAPI();
        PlotsDatabase plotsDatabase = plugin.getPluginManager().getPlotsDatabase();
        ApplicableRegionSet regions = worldGuardAPI.getApplicableRegionSet(location);

        return regions.getRegions().stream()
                .map(region -> plotsDatabase.getPlotFromRegionIdentifier(region.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }
}
