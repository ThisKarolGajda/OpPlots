package me.opkarol.opplots.plots.listener;

import me.opkarol.opc.api.map.OpMap;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.misc.listeners.BasicListener;
import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opc.api.wrappers.OpBossBar;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.worldguard.events.IChangeEvent;
import me.opkarol.opplots.worldguard.events.RegionEnteredEvent;
import me.opkarol.opplots.worldguard.events.RegionLeftEvent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class PlayerRegionChangingListener extends BasicListener {
    private final OpMap<UUID, Tuple<OpBossBar, OpRunnable>> activeBossBars = new OpMap<>();

    @EventHandler
    public void regionEnteredEvent(RegionEnteredEvent event) {
        useBossBar(event, plot -> "#<447cfc>☁ &7Wchodzisz na: " + plot.getName() + " &8(" + plot.getOwnerName() + ")");
    }

    @EventHandler
    public void regionLeftEvent(RegionLeftEvent event) {
        useBossBar(event, plot -> "#<447cfc>☁ &7Wychodzisz z: " + plot.getName() + " &8(" + plot.getOwnerName() + ")");
    }

    private void useBossBar(IChangeEvent event, Function<Plot, String> title) {
        String regionName = event.getRegionName();
        Optional<Plot> optional = OpPlots.getInstance().getPluginManager().getPlotsDatabase().getPlotFromRegionIdentifier(regionName);
        if (optional.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = event.getUUID();

        OpBossBar enteredBossBar = new OpBossBar();
        enteredBossBar.setBarStyle(BarStyle.SOLID);
        enteredBossBar.setBarColor(BarColor.WHITE);
        Plot plot = optional.get();
        enteredBossBar.setTitle(FormatUtils.formatMessage(title.apply(plot)));
        enteredBossBar.display(player);

        if (activeBossBars.containsKey(uuid)) {
            Tuple<OpBossBar, OpRunnable> tuple = activeBossBars.unsafeGet(uuid);
            tuple.first().removeDisplay(player);
            tuple.second().cancelTask();
        }

        OpRunnable runnable = new OpRunnable(() -> enteredBossBar.removeDisplay(player))
                .runTaskLater(100);
        activeBossBars.set(uuid, Tuple.of(enteredBossBar, runnable));
    }
}
