package me.opkarol.opplots.inventories.plot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.inventories.InventoryHelper;
import me.opkarol.opplots.plots.PlotFunctions;
import org.bukkit.entity.Player;

public class MainPlotInventory {
    private final PlotFunctions plot;
    private final ChestGui gui;

    public MainPlotInventory(PlotFunctions plot, Player player) {
        this.plot = plot;
        gui = new ChestGui(4, FormatUtils.formatMessage("Zarządzanie działką"));

        StaticPane itemsPane = new StaticPane(1, 1, 7, 2, Pane.Priority.HIGH);

        GuiItem nameChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("2ad8a3a3b36add5d9541a8ec970996fbdcdea9414cd754c50e48e5d34f1bf60a"))
                .setName("#<447cfc>&lZmiana nazwy działki"),
                event -> plot.openPlotChangeNameAnvilInventory(player));

        GuiItem membersChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("a4d6dd99928e32b34596c60d6164535fd06d56d85fb3990ef3dcbbc939cf8034"))
                .setName("#<447cfc>&lZarządzanie członkami"),
                event -> plot.openManageMembersPagedInventory(player));

        GuiItem ignoreChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("ddc80c01f9db8d5a6b7c5a333824b0fa768e60ff4b5341dbc1e34329bb9cdc8c"))
                .setName("#<447cfc>&lZarządzanie ignorowanymi"),
                event -> plot.openManageIgnorePagedInventory(player));

        GuiItem upgradesChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("399ad7a0431692994b6c412c7eafb9e0fc49975240b73a27d24ed797035fb894"))
                .setName("#<447cfc>&lUlepszanie działki")
                , event -> plot.openUpgradesInventory(player));

        GuiItem settingsChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("204a6fc8f0cdcb1332ad98354ecba1db595253642b6b6182258bb183625d1892"))
                .setName("#<447cfc>&lUstawienia działki"),
                event -> plot.openSettingsInventory(player));

        GuiItem addExpiration = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("3ca1a48d2d231fa71ba5f7c40fdc10d3f2e98c5a63c017321e6781308b8a5793"))
                .setName("#<447cfc>&lZarządzanie wygaśnięciem"),
                event -> plot.openAddExpirationInventory(player));

        GuiItem showBorders = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("7c373b60c4804e8f851ba8829bc0250f2db03d5d9e9a010cc03a2d255ad7fc15"))
                .setName("#<447cfc>&lWyświetlanie granicy"),
                event -> plot.displayBorder(player));

        itemsPane.addItem(nameChange, 0, 0);
        itemsPane.addItem(membersChange, 2, 0);
        itemsPane.addItem(ignoreChange, 4, 0);
        itemsPane.addItem(upgradesChange, 6, 0);
        itemsPane.addItem(settingsChange, 1, 1);
        itemsPane.addItem(addExpiration, 3, 1);
        itemsPane.addItem(showBorders, 5, 1);

        gui.addPane(InventoryHelper.backgroundPaneFourRows);
        gui.addPane(itemsPane);
        gui.update();
        gui.show(player);
    }

    public PlotFunctions getPlot() {
        return plot;
    }

    public ChestGui getGui() {
        return gui;
    }
}
