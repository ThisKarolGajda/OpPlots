package me.opkarol.opplots.plots.upgrades.types;

import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.worldguard.WorldGuardRegion;

import java.util.function.BiConsumer;

import static me.opkarol.opplots.plots.Plot.PLOTS_WORLD;

public class PlotSizeUpgrade {

    public static final BiConsumer<Integer, Plot> ON_LEVEL_UP_ACTION = (level, plot) -> {
        String mainRegionIdentifier = plot.getRegionIdentifier();
        String supportRegionIdentifier = plot.getSupportRegionIdentifier();

        int expand = level == 6 ? 20 : 10;
        WorldGuardRegion.expandAndReplaceRegion(mainRegionIdentifier, expand, 0, expand, PLOTS_WORLD);
        WorldGuardRegion.expandAndReplaceRegion(supportRegionIdentifier, expand, 0, expand, PLOTS_WORLD);
    };
}
