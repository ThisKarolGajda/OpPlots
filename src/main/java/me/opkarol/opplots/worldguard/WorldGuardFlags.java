package me.opkarol.opplots.worldguard;

import com.sk89q.worldedit.world.entity.EntityType;
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

    public static void setMobSpawningFlag(String regionId, boolean deny) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        RegionManager regionManager = worldGuardAPI.getRegionManager(PLOTS_WORLD);
        ProtectedRegion region = regionManager.getRegion(regionId);
        SetFlag flag = (SetFlag) WorldGuard.getInstance().getFlagRegistry().get("deny-spawn");
        if (!deny) {
            HashSet<EntityType> entityTypes = new HashSet<>();
            entityTypes.add(EntityType.REGISTRY.get("minecraft:creeper"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:skeleton"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:spider"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:husk"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:slime"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:ghast"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:zombie_villager"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:stray"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:wither_skeleton"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:phantom"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:drowned"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:evoker"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:vex"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:pillager"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:ravager"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:witch"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:wither"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:enderman"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:zoglin"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:hoglin"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:piglin"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:piglin_brute"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:strider"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:zombified_piglin"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:wither_skull"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:endermite"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:ender_dragon"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:vindicator"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:illusioner"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:phantom"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:piglin_brute"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:husk"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:shulker"));
            entityTypes.add(EntityType.REGISTRY.get("minecraft:endermite"));
            region.setFlag(flag, entityTypes);
        } else {
            region.setFlag(flag, new HashSet<>());
        }
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
