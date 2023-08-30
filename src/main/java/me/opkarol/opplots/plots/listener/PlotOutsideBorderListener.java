package me.opkarol.opplots.plots.listener;

import me.opkarol.opc.api.map.OpMap;
import me.opkarol.opc.api.misc.listeners.BasicListener;
import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.permissions.PlayerPermissions;
import me.opkarol.opplots.worldguard.WorldGuardBorder;
import me.opkarol.opplots.worldguard.events.RegionEnteredEvent;
import me.opkarol.opplots.worldguard.events.RegionLeftEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlotOutsideBorderListener extends BasicListener {
    public final static Pattern REGEX_SUPPORT_IDENTIFIER = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w+s\\b");
    public final static Pattern REGEX_MAIN_IDENTIFIER = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w+\\b");
    public final static Pattern REGEX_SAFE_AREA_IDENTIFIER = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w+a\\b");
    private static final int MAX_SAFE_TIME = 45;
    private final OpMap<UUID, OpRunnable> inSupportRegion = new OpMap<>();

    public static WorldGuardBorder.IPlayerParticle getParticle(Plot plot, Player player) {
        if (plot.isOwner(player)) {
            return new WorldGuardBorder.BlueParticle();
        }

        if (plot.isIgnored(player)) {
            return new WorldGuardBorder.RedParticle();
        }

        return new WorldGuardBorder.GreenParticle();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (inSupportRegion.containsKey(uuid)) {
            inSupportRegion.remove(uuid);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void regionEnteredEvent(RegionEnteredEvent event) {
        String regionName = event.getRegionName();
        if (regionName.length() < 48 || REGEX_SAFE_AREA_IDENTIFIER.matcher(regionName).matches()) {
            return;
        }

        final boolean[] ignored = {false};
        Player player = event.getPlayer();
        if (REGEX_MAIN_IDENTIFIER.matcher(event.getRegionName()).matches() && event.getRegionName().length() == 48) {
            if (!player.hasPermission(PlayerPermissions.ADMIN)) {
                // Player is entering main region and is blocked!?
                OpPlots.getInstance()
                        .getPluginManager()
                        .getPlotsDatabase()
                        .getPlotFromRegionIdentifier(regionName)
                        .ifPresent(plot -> {
                    if (plot.isIgnored(player)) {
                        ignored[0] = true;
                        event.setCancelled(true);
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Nie możesz wejść na tą działkę!"));
                    }
                });
            }
        }

        UUID uuid = event.getUUID();
        Matcher matcher = REGEX_SUPPORT_IDENTIFIER.matcher(event.getRegionName());

        if (!ignored[0] && matcher.matches()) {
            // Player is entering support region from whatever region
            OpPlots.getInstance().getPluginManager().getPlotsDatabase().getPlotFromRegionIdentifier(regionName.substring(0, regionName.length() - 1)).ifPresent(plot -> {
                OpRunnable runnable = spawnParticles(plot, player);
                inSupportRegion.set(uuid, runnable);
            });
        } else {
            inSupportRegion.getByKey(uuid).ifPresent(OpRunnable::cancelTask);
            inSupportRegion.remove(uuid);
        }
    }

    private OpRunnable spawnParticles(Plot plot, Player player) {
        return spawnParticles(plot, player, 0);
    }

    private OpRunnable spawnParticles(Plot plot, Player player, int secondsAlready) {
        if (secondsAlready == MAX_SAFE_TIME) {
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Wyłączono obecne pokazywanie granicy. Wejdź i wyjdź ze swojej działki aby zobaczyć granice ponownie lub wpisz /dzialka granica."));
            UUID uuid = player.getUniqueId();
            inSupportRegion.getByKey(uuid).ifPresent(OpRunnable::cancelTask);
            inSupportRegion.remove(uuid);
            return null;
        }
        plot.displayBorder(player, 1);
        return new OpRunnable((runnable) -> {
            if (inSupportRegion.containsKey(player.getUniqueId())) {
                spawnParticles(plot, player, secondsAlready + 1);
            } else {
                runnable.cancelTask();
            }
        }).runTaskLater(20L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void regionLeftEvent(RegionLeftEvent event) {
        String regionName = event.getRegionName();
        Player player = event.getPlayer();
        UUID uuid = event.getUUID();
        Matcher matcher = REGEX_MAIN_IDENTIFIER.matcher(regionName);
        Matcher matcherSupport = REGEX_SUPPORT_IDENTIFIER.matcher(regionName);
        Matcher matcherSafeArea = REGEX_SAFE_AREA_IDENTIFIER.matcher(regionName);
        if (matcher.matches() && !matcherSupport.matches() && !matcherSafeArea.matches()) {
            // Player is leaving main region and entering support region
            OpPlots.getInstance().getPluginManager().getPlotsDatabase().getPlotFromRegionIdentifier(regionName).ifPresent(plot -> {
                OpRunnable runnable = spawnParticles(plot, player);
                inSupportRegion.set(uuid, runnable);
            });
        } else if (!matcherSafeArea.matches()) {
            inSupportRegion.getByKey(uuid).ifPresent(OpRunnable::cancelTask);
            inSupportRegion.remove(uuid);
        }
    }
}