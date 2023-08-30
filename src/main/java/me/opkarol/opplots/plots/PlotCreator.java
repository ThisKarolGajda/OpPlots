package me.opkarol.opplots.plots;

import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.tools.location.OpSerializableLocation;
import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opc.api.wrappers.OpBossBar;
import me.opkarol.opc.api.wrappers.OpSound;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.effects.FireworkSpawn;
import me.opkarol.opplots.plots.name.PlotNameFactory;
import me.opkarol.opplots.plots.settings.PlotSettings;
import me.opkarol.opplots.plots.upgrades.PlotUpgrades;
import me.opkarol.opplots.utils.TimeUtils;
import me.opkarol.opplots.worldguard.WorldGuardAPI;
import me.opkarol.opplots.worldguard.WorldGuardUsers;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static me.opkarol.opplots.webhook.DiscordWebhooks.sendPlotCreatedWebhook;
import static me.opkarol.opplots.worldguard.WorldGuardFlags.*;

public class PlotCreator {

    private static final double MAIN_LENGTH = 41;
    private static final double SUPPORT_LENGTH = MAIN_LENGTH + 10;
    private static final double SAFE_AREA_LENGTH = 111;
    private static final OpBossBar bossBar;
    private static final OpSound crashSound;
    private static final OpSound successSound;
    private static final FireworkSpawn fireworkSpawn = new FireworkSpawn();
    private static final Set<UUID> usingCreator = new HashSet<>();

    static {
        // Effects
        bossBar = new OpBossBar(FormatUtils.formatMessage("#<447cfc>&l☁ &7Sprawdzanie terenu..."));
        bossBar.setBarColor(BarColor.WHITE);
        bossBar.setBarStyle(BarStyle.SEGMENTED_6);
        bossBar.build();
        bossBar.getBossBar().setProgress(0);
        crashSound = new OpSound(Sound.BLOCK_ANVIL_DESTROY);
        successSound = new OpSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
    }

    public static boolean isUsingCreator(UUID uuid) {
        return usingCreator.contains(uuid);
    }

    public static CompletableFuture<Tuple<Location, Plot>> creteAndRegisterEmptyPlot(Player player) {
        usingCreator.add(player.getUniqueId());
        String creationDate = getCurrentDateAndTime();
        UUID uuid = player.getUniqueId();
        Plot plot = new Plot(uuid, creationDate, PlotNameFactory.createPlotName(player.getName()), new ArrayList<>(), new ArrayList<>(), new PlotUpgrades(), new PlotSettings(null), getDefaultExpiration(), new OpSerializableLocation(player.getLocation()));
        String regionIdentifier = plot.getRegionIdentifier();
        String supportRegionIdentifier = plot.getSupportRegionIdentifier();
        String safeAreaRegionIdentifier = plot.getSafeAreaRegionIdentifier();
        Location playerLocation = player.getLocation();

        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();

        // Effects
        bossBar.display(player);

        CompletableFuture<Tuple<Location, Plot>> future = new CompletableFuture<>();
        new OpRunnable(() -> {
            // Safe Area region
            Location safeAreaLocation1 = playerLocation.clone().add(-SAFE_AREA_LENGTH, 0, -SAFE_AREA_LENGTH);
            safeAreaLocation1.setY(-64);
            Location safeAreaLocation2 = playerLocation.clone().add(SAFE_AREA_LENGTH, 0, SAFE_AREA_LENGTH);
            safeAreaLocation2.setY(320);

            checkAndCreateRegion(safeAreaRegionIdentifier, safeAreaLocation1, safeAreaLocation2, progress -> bossBar.getBossBar().setProgress(progress / 100d)).thenAcceptAsync(invalidLocation -> {
                bossBar.removeDisplay(player);
                bossBar.getBossBar().setProgress(0);
                usingCreator.remove(player.getUniqueId());
                if (invalidLocation == null) {
                    OpPlots.getInstance().getPluginManager().getPlotsDatabase().addPlot(plot);
                    future.complete(Tuple.of(null, plot));

                    // Main Region
                    createRegion(player, regionIdentifier, playerLocation, MAIN_LENGTH);

                    // Support region
                    createRegion(player, supportRegionIdentifier, playerLocation, SUPPORT_LENGTH);

                    // Set priorities
                    World world = player.getWorld();
                    worldGuardAPI.setPriority(world, regionIdentifier, 10);
                    worldGuardAPI.setPriority(world, supportRegionIdentifier, 9);
                    worldGuardAPI.setPriority(world, safeAreaRegionIdentifier, 9);

                    // Owner
                    WorldGuardUsers.addOwner(regionIdentifier, uuid);

                    // Flags
                    setBlockedSetHomeFlag(regionIdentifier);
                    setPvpFlag(regionIdentifier, true);
                    setMobSpawningFlag(regionIdentifier, true);
                    setBuildFlag(supportRegionIdentifier, true);
                    setBuildFlag(safeAreaRegionIdentifier, true);

                    // Effects
                    successSound.play(player);
                    fireworkSpawn.startFireworkShootout(playerLocation.add(new Vector(0, 10, 0)));
                    sendPlotCreatedWebhook(plot);
                } else {
                    future.complete(Tuple.of(invalidLocation, null));

                    // Effects
                    crashSound.play(player);
                }
            });
        }).runTaskAsynchronously();

        return future;
    }

    private static void createRegion(Player player, String regionIdentifier, Location playerLocation, double mainLength) {
        Location mainRegionLocation1 = playerLocation.clone().add(-mainLength, 0, -mainLength);
        mainRegionLocation1.setY(-64);
        Location mainRegionLocation2 = playerLocation.clone().add(mainLength, 0, mainLength);
        mainRegionLocation2.setY(320);
        addRegion(player.getWorld(), regionIdentifier, mainRegionLocation1, mainRegionLocation2);
    }

    public static String getCurrentDateAndTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        return currentDateTime.format(formatter);
    }

    public static CompletableFuture<Location> checkAndCreateRegion(String regionName, Location location1, Location location2, Consumer<Integer> progressCallback) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        CompletableFuture<Location> future = new CompletableFuture<>();

        new OpRunnable(() -> {
            // Check if region isn't occupied
            worldGuardAPI.findOccupiedRegion(location1, location2, progressCallback).thenAcceptAsync(location -> {
                if (location == null) {
                    worldGuardAPI.addRegion(location1.getWorld(), regionName, location1, location2);
                    future.complete(null);
                } else {
                    future.complete(location);
                }
            });

        }).runTaskAsynchronously();
        return future;
    }

    public static void addRegion(World world, String regionName, Location location1, Location location2) {
        WorldGuardAPI worldGuardAPI = OpPlots.getInstance().getPluginManager().getWorldGuardAPI();
        worldGuardAPI.addRegion(world, regionName, location1, location2);
    }

    public static long getDefaultExpiration() {
        return TimeUtils.addToCurrent(5, TimeUtils.TimeUnit.DAY);
    }

}
