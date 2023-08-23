package me.opkarol.opplots.inventories.plot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.PlotRemover;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlotRemoveAnvilInventory {
    public PlotRemoveAnvilInventory(Player player, Plot plot) {
        AnvilGui gui = new AnvilGui("Wpisz: " + plot.getName());

        StaticPane pane = new StaticPane(0, 0, 1, 1);
        pane.addItem(new GuiItem(new ItemBuilder(Material.NAME_TAG).setName(" "), event -> event.setCancelled(true)), 0, 0);
        gui.getFirstItemComponent().addPane(pane);

        StaticPane pane1 = new StaticPane(0, 0, 1, 1);
        pane1.addItem(new GuiItem(new ItemBuilder(Material.BARRIER).setName("&k"), event -> event.setCancelled(true)), 0, 0);
        gui.getSecondItemComponent().addPane(pane1);

        StaticPane secondPane = new StaticPane(0, 0, 1, 1);
        secondPane.addItem(new GuiItem(new ItemBuilder(Material.NAME_TAG).setName("#<447cfc>&lUsuń swoją działkę!"), event -> {
            event.setCancelled(true);
            player.closeInventory();
            String name = gui.getRenameText();
            if (name.startsWith(" ")) {
                name = name.substring(1); // Remove space if exists at the start set by the item name
            }

            if (name.equals(plot.getName())) {
                PlotRemover.removePlot(plot);
                player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Usunięto działkę: " + plot.getName() + "!"));
            } else {
                player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cPodano złą wartość, działka nie usunięta."));
            }
        }), 0, 0);
        gui.getResultComponent().addPane(secondPane);

        gui.show(player);
        gui.update();
    }
}
