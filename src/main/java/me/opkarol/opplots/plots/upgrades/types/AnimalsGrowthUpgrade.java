package me.opkarol.opplots.plots.upgrades.types;

import me.opkarol.opc.api.tools.runnable.OpRunnable;
import me.opkarol.opc.api.utils.VariableUtil;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.listener.PlotOptionsRegister;
import me.opkarol.opplots.plots.upgrades.PlotUpgrades;
import org.bukkit.entity.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.ArrayList;
import java.util.List;

public class AnimalsGrowthUpgrade extends PlotOptionsRegister {
    private static AnimalsGrowthUpgrade instance;

    {
        instance = this;
    }

    public AnimalsGrowthUpgrade(List<Plot> registeredPlots) {
        super(registeredPlots);
        runListener();
    }

    public static AnimalsGrowthUpgrade getInstance() {
        return VariableUtil.getOrDefault(instance, new AnimalsGrowthUpgrade(new ArrayList<>()));
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Ageable ageable)) {
            return;
        }

        if (ageable.isAdult()) {
            return;
        }

        new OpRunnable(() -> getPlotInsideLocationAsync(event.getLocation()).thenAcceptAsync(plot -> {
            int newAge = ageable.getAge() + plot.getUpgrades().getLevel(PlotUpgrades.Type.ANIMALS_GROWTH_UPGRADE) * 2400;
            ageable.setAge(newAge);
        })).runTaskAsynchronously();

    }
}
