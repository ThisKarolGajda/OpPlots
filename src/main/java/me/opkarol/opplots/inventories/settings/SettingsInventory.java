package me.opkarol.opplots.inventories.settings;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opplots.inventories.InventoryHelper;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.settings.PlotSettings;
import org.bukkit.entity.Player;

public class SettingsInventory {
    private final ChestGui gui;

    public SettingsInventory(Plot plot, Player player) {
        gui = new ChestGui(3, "Menu ustawień");

        StaticPane itemsPane = new StaticPane(1, 1, 7, 1, Pane.Priority.HIGH);

        GuiItem weatherChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("c465c121958c0522e3dccb3d14d68612d6317cd380b0e646b61b7420b904af02")).setName("#<447cfc>&lUstawienia pogody"), event -> new SettingsChoosePagedInventory(plot, PlotSettings.Type.WEATHER_CHANGE, player));

        GuiItem biomeChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("7eb4c41f481e816cf4b507b0a17595f2ba1f24664dc432be347d4e7a4eb3")).setName("#<447cfc>&lUstawienia biomu"), event -> new SettingsChoosePagedInventory(plot, PlotSettings.Type.BIOME_CHANGE, player));

        GuiItem animalsSpawnChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("1caaa7fc0396850088bbdd0f8007e2c1a29abc9088c6d24b7943db65ebc814f8")).setName("#<447cfc>&lUstawienia spanwnu zwierząt"), event -> new SettingsChoosePagedInventory(plot, PlotSettings.Type.ANIMALS_SPAWN_CHANGE, player));

        GuiItem pvpChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("1765341353c029e9b655f4f57931ae6adc2c7a73c657945d945a307641d3778")).setName("#<447cfc>&lUstawienia PVP"), event -> new SettingsChoosePagedInventory(plot, PlotSettings.Type.PVP_CHANGE, player));

        itemsPane.addItem(weatherChange, 0, 0);
        itemsPane.addItem(biomeChange, 2, 0);
        itemsPane.addItem(animalsSpawnChange, 4, 0);
        itemsPane.addItem(pvpChange, 6, 0);

        gui.addPane(itemsPane);
        gui.addPane(InventoryHelper.backgroundPaneThreeRows);
        gui.update();
        gui.show(player);
    }

    public ChestGui getGui() {
        return gui;
    }
}
