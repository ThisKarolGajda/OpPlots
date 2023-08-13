package me.opkarol.plots.identifier;

import me.opkarol.plots.Plot;

import java.util.function.Function;

public class RegionIdentifierFactory {
    private static final Function<Plot, String> transformPlotIntoRegionIdentifierFunction = plot -> plot.getOwner().toString() + plot.getCreationDate();

    public static String createRegionIdentifier(Plot plot) {
        return transformPlotIntoRegionIdentifierFunction.apply(plot);
    }
}
