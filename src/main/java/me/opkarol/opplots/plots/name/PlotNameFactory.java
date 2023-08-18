package me.opkarol.opplots.plots.name;

import me.opkarol.opplots.plots.Plot;

import java.util.function.Function;

public class PlotNameFactory {
    private static final Function<String, String> createPlotName = name -> "Działka gracza " + name;

    public static String createPlotName(Plot plot) {
        return createPlotName.apply(plot.getOwnerName());
    }

    public static String createPlotName(String name) {
        return createPlotName.apply(name);
    }
}
