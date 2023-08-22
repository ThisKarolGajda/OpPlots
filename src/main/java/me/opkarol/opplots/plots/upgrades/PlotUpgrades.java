package me.opkarol.opplots.plots.upgrades;

import me.opkarol.opc.api.file.Configuration;
import me.opkarol.opc.api.map.OpMap;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.listener.PlotOptionsRegister;
import me.opkarol.opplots.plots.permissions.PlayerPermissions;
import me.opkarol.opplots.plots.upgrades.types.AnimalsGrowthUpgrade;
import me.opkarol.opplots.plots.upgrades.types.PlantsGrowthUpgrade;
import me.opkarol.opplots.plots.upgrades.types.PlotSizeUpgrade;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlotUpgrades {
    private static final int START_LEVEL = 0;
    private static final double CHEAPER_UPGRADES = OpPlots.getInstance().getFilesManager().getConfigFile().getConfiguration().getDouble("special.cheaperUpgrades");
    private final OpMap<Type, Integer> levels;

    public PlotUpgrades(OpMap<Type, Integer> levels, Plot plot) {
        this.levels = levels;
        registerListeners(plot);
    }

    public PlotUpgrades(OpMap<Type, Integer> levels) {
        this.levels = levels;
    }

    public PlotUpgrades(Plot plot) {
        OpMap<Type, Integer> levels = new OpMap<>();
        for (Type type : Type.values()) {
            levels.set(type, START_LEVEL);
        }
        this.levels = levels;
        registerListeners(plot);
    }

    public PlotUpgrades() {
        OpMap<Type, Integer> levels = new OpMap<>();
        for (Type type : Type.values()) {
            levels.set(type, START_LEVEL);
        }
        this.levels = levels;
    }

    public static PlotUpgrades fromString(String input) {
        String[] parts = input.substring(input.indexOf("[") + 1, input.indexOf("]")).split(",");
        OpMap<Type, Integer> levels = new OpMap<>();

        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                Type type = Type.valueOf(keyValue[0]);
                int level = Integer.parseInt(keyValue[1]);
                levels.set(type, level);
            }
        }

        return new PlotUpgrades(levels);
    }

    public int getLevel(Type type) {
        return levels.getOrDefault(type, START_LEVEL);
    }

    public boolean canLevelUp(Plot plot, Type type) {
        if (type.equals(Type.PLOT_SIZE_UPGRADE)) {
            return plot.getOwner().getPlayer().hasPermission(PlayerPermissions.ADDITIONAL_PLOT_SIZE_UPGRADE) && getLevel(type) < type.levelLimit;
        }
        return getLevel(type) < type.levelLimit;
    }

    public void increaseLevel(Plot plot, Type type) {
        int level = getLevel(type) + 1;
        levels.set(type, level);

        if (type.onLevelUp != null) {
            type.onLevelUp.accept(level, plot);
        }

        if (type.register != null) {
            type.register.registerPlot(plot);
        }
        plot.updateToDatabase();
    }

    public void registerListeners(Plot plot) {
        for (Type type : Type.values()) {
            if (type.register != null) {
                type.register.registerPlot(plot);
            }
        }
    }

    public double getCostForNextLevel(Plot plot, Type type) {
        if (type.levelLimit == getLevel(type)) {
            // Already has max level
            return 0;
        }

        if (plot.getOwner().getPlayer().hasPermission(PlayerPermissions.CHEAPER_UPGRADES_COST)) {
            return type.pricePerLimit.apply(getLevel(type) + 1) * CHEAPER_UPGRADES;
        }
        return type.pricePerLimit.apply(getLevel(type) + 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PlotUpgrades[");

        for (Type type : Type.values()) {
            sb.append(type.name()).append(":").append(getLevel(type)).append(",");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb.toString();
    }

    public void removeData(Plot plot) {
        for (Type type : Type.values()) {
            if (type.register != null) {
                type.register.unregisterPlot(plot);
            }
        }
    }

    public enum Type {
        PLOT_SIZE_UPGRADE(6, level -> level * Constants.CONFIGURATION.getDouble("plot.upgrades.PLOT_SIZE_UPGRADE"), null, PlotSizeUpgrade.ON_LEVEL_UP_ACTION),
        PLANTS_GROWTH_UPGRADE(5, level -> level * Constants.CONFIGURATION.getDouble("plot.upgrades.PLANTS_GROWTH_UPGRADE"), PlantsGrowthUpgrade.getInstance(), null),
        ANIMALS_GROWTH_UPGRADE(5, level -> level * Constants.CONFIGURATION.getDouble("plot.upgrades.ANIMALS_GROWTH_UPGRADE"), AnimalsGrowthUpgrade.getInstance(), null),
        ;

        private final int levelLimit;
        private final Function<Integer, Double> pricePerLimit;
        private final PlotOptionsRegister register;
        private final BiConsumer<Integer, Plot> onLevelUp;

        Type(int levelLimit, Function<Integer, Double> pricePerLimit, PlotOptionsRegister register, BiConsumer<Integer, Plot> onLevelUp) {
            this.levelLimit = levelLimit;
            this.pricePerLimit = pricePerLimit;
            this.register = register;
            this.onLevelUp = onLevelUp;
        }

        public PlotOptionsRegister getRegister() {
            return register;
        }

        public Function<Integer, Double> getPricePerLimit() {
            return pricePerLimit;
        }

        public int getLevelLimit() {
            return levelLimit;
        }

        public BiConsumer<Integer, Plot> getOnLevelUp() {
            return onLevelUp;
        }

        private static class Constants {
            public static final Configuration CONFIGURATION = OpPlots.getInstance().getFilesManager().getConfigFile().getConfiguration();
        }
    }

}
