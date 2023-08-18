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

public class PvpChange extends PlotOptionsRegister {
    private static PvpChange instance;

    {
        instance = this;
    }

    protected PvpChange(List<Plot> registeredPlots) {
        super(registeredPlots);
    }

    public static PvpChange getInstance() {
        return VariableUtil.getOrDefault(instance, new PvpChange(new ArrayList<>()));
    }

    @EventHandler
    public void onSettingChange(SettingChangeCurrentEvent event) {
        if (event.getType() != PlotSettings.Type.PVP_CHANGE) {
            return;
        }

        WorldGuardFlags.setPvpFlag(event.getRegionName(), (Boolean) event.getSetObject());
    }
}
