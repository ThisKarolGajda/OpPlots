package me.opkarol.plots.name;

import me.opkarol.plots.Plot;

import java.util.function.Function;

public class PlotNameFactory {
    private static final Function<Plot, String> createPlotName = plot -> "Działka gracza " + plot.getOwnerName();

    public static String createPlotName(Plot plot) {
        return createPlotName.apply(plot);
    }
}
