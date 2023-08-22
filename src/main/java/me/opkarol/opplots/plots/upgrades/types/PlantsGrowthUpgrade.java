package me.opkarol.opplots.plots.upgrades.types;

import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opc.api.utils.MathUtils;
import me.opkarol.opc.api.utils.VariableUtil;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.listener.PlotOptionsRegister;
import me.opkarol.opplots.plots.upgrades.PlotUpgrades;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.ArrayList;
import java.util.List;

public class PlantsGrowthUpgrade extends PlotOptionsRegister {
    private static PlantsGrowthUpgrade instance;
    private final int GROWTH_MULTIPLIER = 2;

    {
        instance = this;
    }

    public PlantsGrowthUpgrade(List<Plot> registeredPlots) {
        super(registeredPlots);
        runListener();
    }

    public static PlantsGrowthUpgrade getInstance() {
        return VariableUtil.getOrDefault(instance, new PlantsGrowthUpgrade(new ArrayList<>()));
    }

    @EventHandler
    public void onPlantGrow(BlockGrowEvent event) {
        BlockState blockState = event.getNewState();
        if (blockState.getBlockData() instanceof Ageable ageable) {
            if (ageable.getAge() == ageable.getMaximumAge()) {
                return; // Plant is already fully grown
            }

            Location location = blockState.getLocation();
            new OpRunnable(() -> getPlotInsideLocationAsync(location).thenAcceptAsync(plot -> {
                if (plot != null) {
                    int newAge = ageable.getAge() + 1;
                    int level = plot.getUpgrades().getLevel(PlotUpgrades.Type.PLANTS_GROWTH_UPGRADE);

                    switch (level) {
                        case 1 -> {
                            if (MathUtils.getRandomInt(1, 10) == 10) {
                                newAge++;
                            }
                        }
                        case 2 -> {
                            if (MathUtils.getRandomInt(1, 4) == 1) {
                                newAge++;
                            }
                        }
                        case 3 -> {
                            if (MathUtils.getRandomInt(1, 10) <= 4) {
                                newAge++;
                            }
                        }
                        case 4 -> {
                            if (MathUtils.getRandomInt(1, 10) <= 8) {
                                newAge++;
                            }
                        }
                        case 5 -> newAge++;
                    }

                    if (newAge > ageable.getMaximumAge()) {
                        newAge = ageable.getMaximumAge();
                    }

                    ageable.setAge(newAge);
                    blockState.setBlockData(ageable);
                    new OpRunnable(() -> blockState.update(true)).runTask();
                }
            })).runTaskAsynchronously();
        }
    }
}
