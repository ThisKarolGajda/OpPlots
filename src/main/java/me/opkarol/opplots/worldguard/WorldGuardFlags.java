package me.opkarol.opplots.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.opkarol.opplots.OpPlots;

import java.util.HashSet;

import static me.opkarol.opplots.plots.Plot.PLOTS_WORLD;

public class WorldGuardFlags {

    public static void setPvpFlag(String regionId, boolean allow) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        RegionManager regionManager = worldGuardAPI.getRegionManager(PLOTS_WORLD);
        ProtectedRegion region = regionManager.getRegion(regionId);
        StateFlag flag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get("pvp");
        region.setFlag(flag, allow ? StateFlag.State.ALLOW : StateFlag.State.DENY);
        try {
            regionManager.saveChanges();
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setMobSpawningFlag(String regionId, boolean allow) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        RegionManager regionManager = worldGuardAPI.getRegionManager(PLOTS_WORLD);
        ProtectedRegion region = regionManager.getRegion(regionId);
        StateFlag flag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get("mob-spawning");
        region.setFlag(flag, allow ? StateFlag.State.ALLOW : StateFlag.State.DENY);
        try {
            regionManager.saveChanges();
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setBlockedSetHomeFlag(String regionId) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        RegionManager regionManager = worldGuardAPI.getRegionManager(PLOTS_WORLD);
        ProtectedRegion region = regionManager.getRegion(regionId);
        SetFlag flag = (SetFlag) WorldGuard.getInstance().getFlagRegistry().get("blocked-cmds");
        HashSet<String> set = new HashSet<>();
        set.add("/sethome");
        region.setFlag(flag, set);
        try {
            regionManager.saveChanges();
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setBuildFlag(String regionId, boolean allow) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        RegionManager regionManager = worldGuardAPI.getRegionManager(PLOTS_WORLD);
        ProtectedRegion region = regionManager.getRegion(regionId);
        StateFlag flag = (StateFlag) WorldGuard.getInstance().getFlagRegistry().get("build");
        region.setFlag(flag, allow ? StateFlag.State.ALLOW : StateFlag.State.DENY);
        try {
            regionManager.saveChanges();
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }
}
