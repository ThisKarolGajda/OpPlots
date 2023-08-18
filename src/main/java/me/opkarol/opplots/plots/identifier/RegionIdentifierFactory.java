package me.opkarol.opplots.plots.identifier;

import me.opkarol.opplots.plots.Plot;

import java.util.function.Function;

public class RegionIdentifierFactory {
    private static final Function<Plot, String> transformPlotIntoRegionIdentifierFunction = plot -> plot.getOwnerUUID().toString() + plot.getCreationDate();

    public static String createRegionIdentifier(Plot plot) {
        return transformPlotIntoRegionIdentifierFunction.apply(plot);
    }

    public static String createSupportRegionIdentifier(Plot plot) {
        return transformPlotIntoRegionIdentifierFunction.apply(plot) + "s";
    }

    public static String createSafeAreaRegionIdentifier(Plot plot) {
        return transformPlotIntoRegionIdentifierFunction.apply(plot) + "a";
    }
}
