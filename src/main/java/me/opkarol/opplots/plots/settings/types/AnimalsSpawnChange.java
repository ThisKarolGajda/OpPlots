package me.opkarol.opplots.plots.settings.types;

import me.opkarol.opc.api.utils.VariableUtil;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.listener.PlotOptionsRegister;
import me.opkarol.opplots.plots.settings.PlotSettings;
import me.opkarol.opplots.plots.settings.events.SettingChangeCurrentEvent;
import me.opkarol.opplots.worldguard.WorldGuardFlags;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class AnimalsSpawnChange extends PlotOptionsRegister {
    private static AnimalsSpawnChange instance;

    {
        instance = this;
    }

    protected AnimalsSpawnChange(List<Plot> registeredPlots) {
        super(registeredPlots);
    }

    public static AnimalsSpawnChange getInstance() {
        return VariableUtil.getOrDefault(instance, new AnimalsSpawnChange(new ArrayList<>()));
    }

    @EventHandler
    public void onSettingChange(SettingChangeCurrentEvent event) {
        if (event.getType() != PlotSettings.Type.ANIMALS_SPAWN_CHANGE) {
            return;
        }

        WorldGuardFlags.setMobSpawningFlag(event.getRegionName(), (Boolean) event.getSetObject());
    }
}
