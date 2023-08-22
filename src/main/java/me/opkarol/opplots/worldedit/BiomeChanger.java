package me.opkarol.opplots.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.opkarol.opc.OpAPI;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.worldguard.WorldGuardAPI;

import static me.opkarol.opplots.plots.Plot.PLOTS_WORLD;

@Deprecated
public class BiomeChanger {

    public static void changeBiome(String regionId, String biome) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        ProtectedRegion region = worldGuardAPI.getRegionManager(PLOTS_WORLD).getRegion(regionId);
        Region region1 = new CuboidRegion(BukkitAdapter.adapt(PLOTS_WORLD), region.getMinimumPoint(), region.getMaximumPoint());
        BiomeType biomeType = BiomeType.REGISTRY.get(biome);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(PLOTS_WORLD))) {
            for (BlockVector3 block : region1) {
                editSession.setBiome(block, biomeType);
                OpAPI.logInfo(block.toString());
            }
        }
    }
}
