package me.opkarol.opplots.plots.settings.types;

import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opc.api.utils.VariableUtil;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.listener.PlotOptionsRegister;
import me.opkarol.opplots.plots.settings.PlotSettings;
import me.opkarol.opplots.plots.settings.events.SettingChangeCurrentEvent;
import me.opkarol.opplots.worldguard.events.RegionEnteredEvent;
import me.opkarol.opplots.worldguard.events.RegionLeftEvent;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.opkarol.opplots.plots.Plot.PLOTS_WORLD;

public class WeatherChangeSetting extends PlotOptionsRegister {
    private static final int MAX_TRIES = 100;

    private static WeatherChangeSetting instance;

    {
        instance = this;
    }

    protected WeatherChangeSetting(List<Plot> registeredPlots) {
        super(registeredPlots);
    }

    public static WeatherChangeSetting getInstance() {
        return VariableUtil.getOrDefault(instance, new WeatherChangeSetting(new ArrayList<>()));
    }

    @EventHandler
    public void onRegionEntered(RegionEnteredEvent event) {
        Optional<Plot> optional = getRegisteredPlots().stream()
                .filter(plot -> plot.getRegionIdentifier().equals(event.getRegionName()))
                .findAny();
        if (optional.isEmpty()) {
            return;
        }

        Plot plot = optional.get();
        Player player = event.getPlayer();
        Object object = plot.getSettings().getCurrent(PlotSettings.Type.WEATHER_CHANGE);
        if (!(object instanceof WeatherType weatherType)) {
            return;
        }

        player.setPlayerWeather(weatherType);
    }

    @EventHandler
    public void onRegionLeft(RegionLeftEvent event) {
        tryToResetPlayerWeather(event.getPlayer(), 0);
    }

    public void tryToResetPlayerWeather(Player player, int tries) {
        if (tries > MAX_TRIES) {
            return;
        }

        new OpRunnable(() -> getPlotInsideLocationAsync(player.getLocation())
                .thenAcceptAsync(plot -> {
            if (plot == null) {
                player.resetPlayerWeather();
            } else {
                tryToResetPlayerWeather(player, tries + 1);
            }
        })).runTaskLater(1L);
    }

    @EventHandler
    public void onSettingChange(SettingChangeCurrentEvent event) {
        if (event.getType() != PlotSettings.Type.WEATHER_CHANGE || !(event.getSetObject() instanceof WeatherType weatherType)) {
            return;
        }

        OpPlots.getInstance()
                .getPluginManager()
                .getWorldGuardAPI()
                .getPlayersInRegion(PLOTS_WORLD, event.getRegionName())
                .thenAcceptAsync(players -> {
            for (Player player : players) {
                player.setPlayerWeather(weatherType);
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getPlotInsideLocationAsync(player.getLocation())
                .thenAcceptAsync(plot -> {
            if (plot == null) {
                return;
            }

            Object object = plot.getSettings().getCurrent(PlotSettings.Type.WEATHER_CHANGE);
            if (!(object instanceof WeatherType weatherType)) {
                return;
            }

            player.setPlayerWeather(weatherType);
        });
    }
}
