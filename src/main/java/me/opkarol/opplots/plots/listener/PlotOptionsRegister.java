package me.opkarol.opplots.plots.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.opkarol.opc.api.misc.listeners.BasicListener;
import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.worldguard.WorldGuardAPI;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class PlotOptionsRegister extends BasicListener {
    private final List<Plot> registeredPlots;

    public PlotOptionsRegister(List<Plot> registeredPlots) {
        this.registeredPlots = registeredPlots;
        runListener();
    }

    public void registerPlot(Plot plot) {
        if (!getRegisteredPlots().contains(plot)) {
            getRegisteredPlots().add(plot);
        }
    }

    public void unregisterPlot(Plot plot) {
        getRegisteredPlots().remove(plot);
    }

    public CompletableFuture<Plot> getPlotInsideLocationAsync(Location location) {
        CompletableFuture<Plot> futurePlot = new CompletableFuture<>();

        new OpRunnable(() -> {
            WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
            ApplicableRegionSet applicableRegionSet = worldGuardAPI.getApplicableRegionSet(location);
            Optional<ProtectedRegion> protectedRegionOptional = applicableRegionSet.getRegions().stream()
                    .filter(region -> getRegisteredPlots().stream().map(Plot::getRegionIdentifier)
                            .anyMatch(string -> string.equals(region.getId())))
                    .findAny();

            if (protectedRegionOptional.isPresent()) {
                Optional<Plot> optional = OpPlots.getInstance()
                        .getPluginManager()
                        .getPlotsDatabase()
                        .getPlotFromRegionIdentifier(protectedRegionOptional.get().getId());
                if (optional.isPresent()) {
                    futurePlot.complete(optional.get());
                    return;
                }
            }
            futurePlot.complete(null);
        }).runTaskAsynchronously();

        return futurePlot;
    }

    private boolean isLocationInsideRegion(Location location, String regionIdentifier) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(location.getWorld()));

        if (regionManager != null) {
            BlockVector3 blockVector3 = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            ProtectedRegion region = regionManager.getRegion(regionIdentifier);

            if (region != null) {
                return region.contains(blockVector3);
            }
        }

        return false;
    }

    public List<Plot> getRegisteredPlots() {
        return registeredPlots;
    }
}
