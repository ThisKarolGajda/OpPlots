package me.opkarol.opplots.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.opkarol.opc.api.tools.runnable.OpRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static me.opkarol.opplots.plots.listener.PlotOutsideBorderListener.REGEX_SUPPORT_IDENTIFIER;

public record WorldGuardAPI(RegionContainer regionContainer) {

    public RegionManager getRegionManager(World world) {
        return regionContainer().get(BukkitAdapter.adapt(world));
    }

    public void addRegion(World world, ProtectedRegion region) {
        RegionManager regionManager = getRegionManager(world);
        regionManager.addRegion(region);
    }

    public void addRegion(World world, String id, Location location1, Location location2) {
        addRegion(world, new ProtectedCuboidRegion(id, fromLocation(location1), fromLocation(location2)));
    }

    public void addRegion(World world, String id, BlockVector3 location1, BlockVector3 location2) {
        addRegion(world, new ProtectedCuboidRegion(id, location1, location2));
    }

    public BlockVector3 fromLocation(Location location) {
        return BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }

    public BlockVector3 addFromLocation(Location location, double x, double y, double z) {
        return fromLocation(location).add(BlockVector3.at(x, y, z));
    }

    public ApplicableRegionSet getApplicableRegionSet(Location location) {
        RegionQuery query = regionContainer().createQuery();
        return query.getApplicableRegions(BukkitAdapter.adapt(location));
    }

    public CompletableFuture<Location> findOccupiedRegion(Location location1, Location location2, Consumer<Integer> progressCallback) {
        int minX = Math.min(location1.getBlockX(), location2.getBlockX());
        int minY = Math.min(location1.getBlockY(), location2.getBlockY());
        int minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        int maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        int maxY = Math.max(location1.getBlockY(), location2.getBlockY());
        int maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int totalBlocks = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        final int[] processedBlocks = {0};

        return CompletableFuture.supplyAsync(() -> {
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Location location = new Location(location1.getWorld(), x, y, z);
                        if (hasOccupiedRegion(location)) {
                            executor.shutdown();
                            return location;
                        }
                        processedBlocks[0]++;
                        if (processedBlocks[0] % 100000 == 0) {
                            progressCallback.accept(processedBlocks[0] * 100 / totalBlocks);
                        }
                    }
                }
            }
            executor.shutdown();
            return null;
        }, executor);
    }

    private boolean hasOccupiedRegion(Location location) {
        ApplicableRegionSet regions = getApplicableRegionSet(location);
        if (regions.size() == 1) {
            ProtectedRegion region = (ProtectedRegion) regions.getRegions().toArray()[0];
            String regionName = region.getId();
            Matcher matcher = REGEX_SUPPORT_IDENTIFIER.matcher(regionName);
            if (!matcher.matches()) {
                return true;
            }
        }
        return regions.size() > 0;
    }


    public void removeRegion(World world, String id) {
        getRegionManager(world).removeRegion(id);
    }


    public void setPriority(World world, String region, int priority) {
        getRegionManager(world).getRegion(region).setPriority(priority);
    }

    public void setParentRegion(String childId, String parentId, World world) {
        RegionManager regionManager = getRegionManager(world);
        try {
            regionManager.getRegion(childId).setParent(regionManager.getRegion(parentId));
        } catch (ProtectedRegion.CircularInheritanceException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<List<Player>> getPlayersInRegion(World world, String regionId) {
        CompletableFuture<List<Player>> future = new CompletableFuture<>();

        new OpRunnable(() -> {
            List<Player> playersInRegion = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getWorld().equals(world))
                    .collect(Collectors.toSet())) {
                ApplicableRegionSet regions = getApplicableRegionSet(player.getLocation());
                if (regions.getRegions().stream().anyMatch(region -> region.getId().equals(regionId))) {
                    playersInRegion.add(player);
                }
            }
            future.complete(playersInRegion);
        }).runTaskAsynchronously();

        return future;
    }
}