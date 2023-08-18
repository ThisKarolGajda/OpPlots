package me.opkarol.opplots.worldguard;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.opkarol.opplots.OpPlots;

import java.util.UUID;

import static me.opkarol.opplots.plots.Plot.PLOTS_WORLD;

public class WorldGuardUsers {

    public static void addMember(String regionId, UUID uuid) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        ProtectedRegion region = worldGuardAPI.getRegionManager(PLOTS_WORLD).getRegion(regionId);
        DefaultDomain defaultDomain = region.getMembers();
        defaultDomain.addPlayer(uuid);
    }

    public static void removeMember(String regionId, UUID uuid) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        ProtectedRegion region = worldGuardAPI.getRegionManager(PLOTS_WORLD).getRegion(regionId);
        DefaultDomain defaultDomain = region.getMembers();
        defaultDomain.removePlayer(uuid);
    }

    public static void addOwner(String regionId, UUID uuid) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        ProtectedRegion region = worldGuardAPI.getRegionManager(PLOTS_WORLD).getRegion(regionId);
        DefaultDomain defaultDomain = region.getOwners();
        defaultDomain.addPlayer(uuid);
    }

    public static void removeOwner(String regionId, UUID uuid) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        ProtectedRegion region = worldGuardAPI.getRegionManager(PLOTS_WORLD).getRegion(regionId);
        DefaultDomain defaultDomain = region.getOwners();
        defaultDomain.removePlayer(uuid);
    }
}
