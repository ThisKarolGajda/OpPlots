package me.opkarol.opplots.inventories.plot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.inventories.ItemPaletteGUI;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.PlotFunctions;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public class ChoosePlotInventory {
    public ChoosePlotInventory(Player player, Consumer<PlotFunctions> action, boolean seeAllPlots) {
        List<Plot> list;

        if (seeAllPlots) {
            list = OpPlots.getInstance().getPluginManager().getPlotsDatabase().getPlotList();
        } else {
            list = OpPlots.getInstance().getPluginManager().getPlotsDatabase().getAllPlotsFrom(player);
        }

        if (list.size() == 1) {
            // Skip opening inventory if there is only one plot
            action.accept(new PlotFunctions(list.get(0)));
            return;
        }

        ItemPaletteGUI<Plot> gui = new ItemPaletteGUI.Builder<Plot>("Wybierz działkę")
                .as(plot -> new GuiItem(new ItemBuilder(Material.STONE)
                        .setName("#<447cfc>&l" + plot.getName())
                        .setLore("&7Właściciel: #<447cfc>" + plot.getOwnerName(), "&7Data stworzenia: #<447cfc>" + plot.getFormattedCreationDate(), "&7Liczba członków: #<447cfc>" + plot.getMembers().size()),
                        event -> action.accept(new PlotFunctions(plot))))
                .build(list);

        gui.update();
        gui.show(player);
    }

    public ChoosePlotInventory(Player player, Consumer<PlotFunctions> action) {
        this(player, action, false);
    }
}
