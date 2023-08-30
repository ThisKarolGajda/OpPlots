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
import static me.opkarol.opplots.plots.settings.PlotSettings.Type.DAY_TIME_CHANGE;

public class DayChooseInventory {
    private final ChestGui gui;

    public DayChooseInventory(Player player, Plot plot) {
        gui = new ChestGui(5, "Wybierz porę dnia");

        StaticPane previousInventoryPane = new StaticPane(0, 0, 9, 5, Pane.Priority.HIGHEST);
        previousInventoryPane.addItem(new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f"))
                .setName("&7Powrót"),
                event -> {
                    event.setCancelled(true);
                    new SettingsInventory(plot, player);
                }), 8, 4);
        gui.addPane(previousInventoryPane);

        gui.addPane(InventoryHelper.backgroundPaneFiveRows);
        updateItemsPane(player, plot);
        gui.show(player);
        gui.update();
    }

    private void updateItemsPane(Player player, Plot plot) {
        gui.getPanes().removeIf(pane1 -> pane1.getItems().size() == 9);
        StaticPane pane = new StaticPane(0, 0, 9, 3);

        GuiItem item1 = getChosenItem(player, DAY_TIME_CHANGE, "Świt", plot, Material.BLACK_WOOL, () -> updateItemsPane(player, plot));
        GuiItem item2 = getChosenItem(player, DAY_TIME_CHANGE, "Rano", plot, Material.BROWN_WOOL, () -> updateItemsPane(player, plot));
        GuiItem item3 = getChosenItem(player, DAY_TIME_CHANGE, "Południe", plot, Material.RED_WOOL, () -> updateItemsPane(player, plot));
        GuiItem item4 = getChosenItem(player, DAY_TIME_CHANGE, "Zachód słońca", plot, Material.ORANGE_WOOL, () -> updateItemsPane(player, plot));
        GuiItem item5 = getChosenItem(player, DAY_TIME_CHANGE, "Zmierzch", plot, Material.YELLOW_WOOL, () -> updateItemsPane(player, plot));
        GuiItem item6 = getChosenItem(player, DAY_TIME_CHANGE, "Noc", plot, Material.LIME_WOOL, () -> updateItemsPane(player, plot));
        GuiItem item7 = getChosenItem(player, DAY_TIME_CHANGE, "Północ", plot, Material.CYAN_WOOL, () -> updateItemsPane(player, plot));
        GuiItem item8 = getChosenItem(player, DAY_TIME_CHANGE, "Domyślna pora dnia", plot, Material.LIGHT_BLUE_WOOL, () -> updateItemsPane(player, plot));

        pane.addItem(item1, 0, 2);
        pane.addItem(item2, 1, 1);
        pane.addItem(item3, 2, 2);
        pane.addItem(item4, 3, 1);
        pane.addItem(item5, 5, 1);
        pane.addItem(item6, 6, 2);
        pane.addItem(item7, 7, 1);
        pane.addItem(item8, 8, 2);

        gui.addPane(pane);
        gui.update();
    }
}
