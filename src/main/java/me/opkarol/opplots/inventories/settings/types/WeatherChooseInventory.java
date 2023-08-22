package me.opkarol.opplots.inventories.settings.types;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opplots.inventories.InventoryHelper;
import me.opkarol.opplots.inventories.settings.SettingsInventory;
import me.opkarol.opplots.plots.Plot;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static me.opkarol.opplots.inventories.settings.SettingsInventory.getChosenItem;
import static me.opkarol.opplots.plots.settings.PlotSettings.Type.WEATHER_CHANGE;

public class WeatherChooseInventory {
    private final ChestGui gui;

    public WeatherChooseInventory(Player player, Plot plot) {
         gui = new ChestGui(3, "Wybierz pogodę");

        StaticPane previousInventoryPane = new StaticPane(0, 0, 9, 3, Pane.Priority.HIGHEST);
        previousInventoryPane.addItem(new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f"))
                .setName("&7Powrót"),
                event -> {
                    event.setCancelled(true);
                    new SettingsInventory(plot, player);
                }), 8, 2);
        gui.addPane(previousInventoryPane);

        gui.addPane(InventoryHelper.backgroundPaneThreeRows);
        updateItemsPane(player, plot);
        gui.show(player);
        gui.update();
    }

    private void updateItemsPane(Player player, Plot plot) {
        gui.getPanes().removeIf(pane1 -> pane1.getItems().size() == 3);
        StaticPane pane = new StaticPane(0, 0, 9, 3);

        GuiItem item1 = getChosenItem(player, WEATHER_CHANGE, "Czysta pogoda", plot, Material.YELLOW_WOOL, () -> updateItemsPane(player, plot));
        GuiItem item2 = getChosenItem(player, WEATHER_CHANGE, "Opady deszczu", plot, Material.BLUE_WOOL, () -> updateItemsPane(player, plot));
        GuiItem item3 = getChosenItem(player, WEATHER_CHANGE, "Domyślna pogoda", plot, Material.GREEN_WOOL, () -> updateItemsPane(player, plot));

        pane.addItem(item1, 2, 1);
        pane.addItem(item2, 6, 1);
        pane.addItem(item3, 4, 1);

        gui.addPane(pane);
        gui.update();
    }
}
