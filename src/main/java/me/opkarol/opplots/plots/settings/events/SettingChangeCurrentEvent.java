package me.opkarol.opplots.plots.settings.events;

import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.settings.PlotSettings;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SettingChangeCurrentEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String regionName;
    private final Plot plot;
    private final PlotSettings.Type type;
    private final Object setObject;

    public SettingChangeCurrentEvent(String regionName, Plot plot, PlotSettings.Type type, Object setObject) {
        this.regionName = regionName;
        this.plot = plot;
        this.type = type;
        this.setObject = setObject;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Plot getPlot() {
        return plot;
    }

    public String getRegionName() {
        return regionName;
    }

    public PlotSettings.Type getType() {
        return type;
    }

    public Object getSetObject() {
        return setObject;
    }
}
