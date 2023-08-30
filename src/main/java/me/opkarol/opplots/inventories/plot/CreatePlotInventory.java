package me.opkarol.opplots.inventories.plot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.inventories.InventoryHelper;
import me.opkarol.opplots.inventories.wiki.PlotWikiInventory;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.PlotCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import static me.opkarol.opplots.plots.Plot.PLOTS_WORLD;

public class CreatePlotInventory {
    private final ChestGui gui;

    public CreatePlotInventory(Player player) {
        gui = new ChestGui(3, FormatUtils.formatMessage("Stwórz działkę"));

        StaticPane itemsPane = new StaticPane(2, 1, 5, 1, Pane.Priority.HIGH);

        GuiItem createPlot = new GuiItem(new ItemBuilder(Material.DIRT)
                .setName("#<447cfc>&lStwórz działkę!")
                .setLore("&7Naciskąjąc ten przycisk, spróbujesz", "&7stworzyć działkę o rozmiarach 41 bloków", "&7w każdą stronę w miejscu, w którym stoisz!", "&7Jeśli miejsce nie będzie już zajęte", "&7to będziesz miał działkę na własność!", "&7(takie łatwe powinny być podatki w Polsce!)"),
                event -> {
                    player.closeInventory();
                    if (player.getWorld() != PLOTS_WORLD) {
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cNie można tworzyć działek na tym świecie!"));
                        return;
                    }

                    if (PlotCreator.isUsingCreator(player.getUniqueId())) {
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cJuż czekasz na działkę!"));
                        return;
                    }

                    player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Przygotowywanie działki, proszę czekać!"));
                    PlotCreator.creteAndRegisterEmptyPlot(player).thenAcceptAsync(locationPlotTuple -> {
                        Location invalidLocation = locationPlotTuple.first();
                        if (invalidLocation != null) {
                            player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cRegion jest zajęty w lokalizacji X:" + invalidLocation.getBlockX() + " Y:" + invalidLocation.getBlockY() + " Z:" + invalidLocation.getBlockZ() + ". Nie można stworzyć w tym miejscu działki!"));
                            return;
                        }

                        Plot plot = locationPlotTuple.second();
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Działka stworzona o nazwie: " + plot.getName()));
                    });
                });

        GuiItem information = new GuiItem(new ItemBuilder(Material.BOOK)
                .setName("#<447cfc>&lO co biega?")
                .setLore("&7Naciskając #<447cfc>PPM &7otworzy ci się działkowa wikipedia!"),
                event -> new PlotWikiInventory(player));

        itemsPane.addItem(createPlot, 0, 0);
        itemsPane.addItem(information, 4, 0);

        gui.addPane(itemsPane);
        gui.addPane(InventoryHelper.backgroundPaneThreeRows);
        gui.update();
        gui.show(player);
    }

    public ChestGui getGui() {
        return gui;
    }

    public void openGui(HumanEntity humanEntity) {
        humanEntity.openInventory(getGui().getInventory());
    }
}
