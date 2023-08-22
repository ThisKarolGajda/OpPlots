package me.opkarol.opplots.worldguard;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.opkarol.opplots.OpPlots;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Objects;

public class WorldGuardBorder {
    private static final int Y_HEIGHT_DIFFERENCE = 10;
    private static final double Z_GAP = 2;
    private static final double X_GAP = 2;
    private static final double PLAYER_DISTANCE = 50;

    public static void spawnBorderParticles(Player player, String regionName, IPlayerParticle playerParticle, int seconds) {
        World world = player.getWorld();
        ProtectedRegion region = OpPlots.getInstance().getPluginManager().getWorldGuardAPI()
                .getRegionManager(world)
                .getRegion(regionName);

        if (region == null) {
            return;
        }

        ChunkOutlineEntry entry = new ChunkOutlineEntry(player, region, player.getLocation().getBlockY(), playerParticle);
        entry.display(seconds);
    }

    public static void spawnBorderParticles(Player player, String regionName, IPlayerParticle playerParticle) {
        spawnBorderParticles(player, regionName, playerParticle, 10);
    }

    public interface IPlayerParticle {
        void sendParticle(Player player, double x, double y, double z);
    }

    public record ChunkOutlineEntry(Player player, ProtectedRegion region, int yHeight, IPlayerParticle playerParticle) {

        public void display(int seconds) {
            BlockVector3 min = region.getMinimumPoint();
            BlockVector3 max = region.getMaximumPoint();
            Location defaultLocation = player.getLocation();

            for (int i = 0; i < seconds; i++) {
                int delayTicks = i * 20;
                Bukkit.getScheduler().runTaskLater(OpPlots.getInstance(), () -> onParticle(min, max, defaultLocation), delayTicks);
            }
        }

        private void onParticle(BlockVector3 min, BlockVector3 max, Location defaultLocation) {
            if (!Objects.requireNonNull(defaultLocation.getWorld()).equals(player.getWorld())) {
                return;
            }

            if (defaultLocation.distanceSquared(player.getLocation()) > 250) {
                return;
            }

            for (int y = yHeight - Y_HEIGHT_DIFFERENCE; y <= yHeight + Y_HEIGHT_DIFFERENCE; y++) {
                for (int x = min.getBlockX(), xCount = 0; x <= max.getBlockX(); x++, xCount++) {
                    if (xCount % X_GAP == 0) {
                        spawnParticle(x, y, min.getBlockZ());
                        spawnParticle(x, y, max.getBlockZ());
                    }
                }

                for (int z = min.getBlockZ(), zCount = 0; z <= max.getBlockZ(); z++, zCount++) {
                    if (zCount % Z_GAP == 0) {
                        spawnParticle(min.getBlockX(), y, z);
                        spawnParticle(max.getBlockX(), y, z);
                    }
                }
            }
        }

        private void spawnParticle(double x, double y, double z) {
            Location particleLocation = new Location(player.getWorld(), x + 0.5, y + 0.5, z + 0.5);
            double playerDistance = player.getLocation().distance(particleLocation);

            if (playerDistance <= PLAYER_DISTANCE) {
                playerParticle.sendParticle(player, x + 0.5, y + 0.5, z + 0.5);
            }
        }
    }

    public static class GreenParticle implements IPlayerParticle {

        @Override
        public void sendParticle(Player player, double x, double y, double z) {
            player.spawnParticle(Particle.REDSTONE, x, y, z, 0, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(0x48, 0xE6, 0x05), 1.5f));
        }
    }

    public static class RedParticle implements IPlayerParticle {

        @Override
        public void sendParticle(Player player, double x, double y, double z) {
            player.spawnParticle(Particle.REDSTONE, x, y, z, 0, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 1.5f));
        }
    }

    public static class BlueParticle implements IPlayerParticle {

        @Override
        public void sendParticle(Player player, double x, double y, double z) {
            player.spawnParticle(Particle.REDSTONE, x, y, z, 0, 0, 0, 0, 0, new Particle.DustOptions(Color.BLUE, 1.5f));
        }
    }

}