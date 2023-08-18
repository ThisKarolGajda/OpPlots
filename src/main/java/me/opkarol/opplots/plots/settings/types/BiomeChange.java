package me.opkarol.opplots.plots.settings.types;

import me.opkarol.opc.api.utils.VariableUtil;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.listener.PlotOptionsRegister;
import me.opkarol.opplots.plots.settings.PlotSettings;
import me.opkarol.opplots.plots.settings.events.SettingChangeCurrentEvent;
import me.opkarol.opplots.worldedit.BiomeChanger;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class BiomeChange extends PlotOptionsRegister {
    private static BiomeChange instance;

    {
        instance = this;
    }

    protected BiomeChange(List<Plot> registeredPlots) {
        super(registeredPlots);
    }

    public static BiomeChange getInstance() {
        return VariableUtil.getOrDefault(instance, new BiomeChange(new ArrayList<>()));
    }

    @EventHandler
    public void onSettingChange(SettingChangeCurrentEvent event) {
        if (event.getType() != PlotSettings.Type.BIOME_CHANGE) {
            return;
        }

        BiomeChanger.changeBiome(event.getRegionName(), (String) event.getSetObject());
    }
}
