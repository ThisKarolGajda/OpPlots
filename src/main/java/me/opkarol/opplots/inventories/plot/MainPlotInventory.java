package me.opkarol.opplots.inventories.plot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.inventories.InventoryHelper;
import me.opkarol.opplots.inventories.wiki.PlotWikiInventory;
import me.opkarol.opplots.plots.PlotFunctions;
import me.opkarol.opplots.plots.PlotRemover;
import me.opkarol.opplots.plots.permissions.PlayerPermissions;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static me.opkarol.opplots.plots.PlotFunctions.PLOT_FEATURE.*;

public class MainPlotInventory {
    private final PlotFunctions plot;
    private final ChestGui gui;

    public MainPlotInventory(PlotFunctions plot, Player player) {
        this.plot = plot;
        gui = new ChestGui(5, FormatUtils.formatMessage("Zarządzanie działką"));

        StaticPane itemsPane = new StaticPane(0, 0, 9, 5, Pane.Priority.HIGH);

        GuiItem nameChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("2ad8a3a3b36add5d9541a8ec970996fbdcdea9414cd754c50e48e5d34f1bf60a"))
                .setName("#<447cfc>&lZmiana nazwy działki")
                .setLore("&7Dostępne: " + plot.getAvailableFeature(player, CHANGE_NAME), "&7Zmień nazwę swojej działki!",  "&7Wulgarne nazwy mogą być ukarane usunięciem działki!"),
                event -> {
                    event.setCancelled(true);
                    plot.openPlotChangeNameAnvilInventory(player);
                });

        GuiItem membersChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("a4d6dd99928e32b34596c60d6164535fd06d56d85fb3990ef3dcbbc939cf8034"))
                .setName("#<447cfc>&lZarządzanie członkami")
                .setLore("&7Dostępne: " + plot.getAvailableFeature(player, MANAGE_MEMBERS), "&7Dodawaj i wyrzucaj członków Twojej działki!"),
                event -> {
                    event.setCancelled(true);
                    plot.openManageMembersPagedInventory(player);
                });

        GuiItem ignoreChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("ddc80c01f9db8d5a6b7c5a333824b0fa768e60ff4b5341dbc1e34329bb9cdc8c"))
                .setName("#<447cfc>&lZarządzanie ignorowanymi")
                .setLore("&7Dostępne: " + plot.getAvailableFeature(player, MANAGE_IGNORED), "&7Nieproszeni goście przeszkadzają na Twojej działce?", "&7Zablokuj im wejście na działkę!"),
                event -> {
                    event.setCancelled(true);
                    plot.openManageIgnorePagedInventory(player);
                });

        GuiItem upgradesChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("399ad7a0431692994b6c412c7eafb9e0fc49975240b73a27d24ed797035fb894"))
                .setName("#<447cfc>&lUlepszanie działki")
                .setLore("&7Dostępne: " + plot.getAvailableFeature(player, MANAGE_UPGRADES), "&7Zakup ulepszenia do swojej działki!"),
                event -> {
                    event.setCancelled(true);
                    plot.openUpgradesInventory(player);
                });

        GuiItem settingsChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("204a6fc8f0cdcb1332ad98354ecba1db595253642b6b6182258bb183625d1892"))
                .setName("#<447cfc>&lUstawienia działki")
                .setLore("&7Dostępne: " + plot.getAvailableFeature(player, MANAGE_SETTINGS), "&7Sprawdź i zmień ustawienia swojej działki!"),
                event -> {
                    event.setCancelled(true);
                    plot.openSettingsInventory(player);
                });

        GuiItem addExpiration = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("3ca1a48d2d231fa71ba5f7c40fdc10d3f2e98c5a63c017321e6781308b8a5793"))
                .setName("#<447cfc>&lZarządzanie wygaśnięciem działki")
                .setLore("&7Dostępne: " + plot.getAvailableFeature(player, MANAGE_EXPIRATION), "&7Sprawdź długość swojej działki oraz ją przedłuż.", "&7Wygaśnięcie oznacza usunięcie działki i pojawienie", "&7się jej kordów na czacie!"),
                event -> {
                    event.setCancelled(true);
                    plot.openAddExpirationInventory(player);
                });

        GuiItem showBorders = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("7c373b60c4804e8f851ba8829bc0250f2db03d5d9e9a010cc03a2d255ad7fc15"))
                .setName("#<447cfc>&lWyświetl granicę działki")
                .setLore("&7Dostępne: " + plot.getAvailableFeature(player, SHOW_BORDERS), "&7Wyświetl na 10 sekund granicę swojej działki!"),
                event -> {
                    event.setCancelled(true);
                    plot.displayBorder(player);
                });

        GuiItem information = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("d01afe973c5482fdc71e6aa10698833c79c437f21308ea9a1a095746ec274a0f"))
                .setName("#<447cfc>&lInformacje o działce")
                .setLore("&7Nazwa: #<447cfc>" + plot.plot().getName(), "&7Właściciel: #<447cfc>" + plot.plot().getOwnerName(), "&7Data stworzenia: #<447cfc>" + plot.plot().getFormattedCreationDate(), "&7Członkowie: #<447cfc>" + plot.plot().getMembersNames(), "&7Wygasa: #<447cfc>" + plot.plot().getExpirationLeftString()),
                event -> {
                    event.setCancelled(true);
                });

        GuiItem teleportHome = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("c3a8e402dad1b7dad9aae6f4015932183429ce87bbbeced3119026f8296336c2"))
                .setName("#<447cfc>&lTeleportuj się do domu działki")
                .setLore("&7Dostępne: " + plot.getAvailableFeature(player, TELEPORT_HOME)),
                event -> {
                    event.setCancelled(true);
                    PlotFunctions.teleportPlayer(player, plot);
                });

        GuiItem wiki = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("fc445b007b551c4a02648bb38051776bb0ed0bb0d9097820225ebefaf5a1ec3e"))
                .setName("#<447cfc>&lWikipedia")
                .setLore("&7Sprawdź więcej informacji i komend dotyczących działek!"),
                event -> {
                    event.setCancelled(true);
                    new PlotWikiInventory(player);
                });


        itemsPane.addItem(nameChange, 2, 1);
        itemsPane.addItem(membersChange, 3, 1);
        itemsPane.addItem(ignoreChange, 4, 1);
        itemsPane.addItem(upgradesChange, 2, 2);
        itemsPane.addItem(settingsChange, 3, 2);
        itemsPane.addItem(addExpiration, 4, 2);
        itemsPane.addItem(showBorders, 2, 3);
        itemsPane.addItem(teleportHome, 3, 3);
        itemsPane.addItem(wiki, 4, 3);
        itemsPane.addItem(information, 6, 2);

        if (player.hasPermission(PlayerPermissions.ADMIN)) {
            StaticPane pane = new StaticPane(0, 0, 9, 5, Pane.Priority.HIGH);
            pane.addItem(new GuiItem(new ItemBuilder(Material.BARRIER)
                    .setName("Usuń działkę")
                    .setLore("OPCJA WIDOCZNA DLA PERMISIJI: " + PlayerPermissions.ADMIN),
                    event -> {
                        event.setCancelled(true);
                        player.closeInventory();
                        PlotRemover.removePlotFromOwner(plot.plot().getOwnerUUID());
                    }), 0, 4);
            gui.addPane(pane);
        }

        gui.addPane(InventoryHelper.backgroundPaneFiveRows);
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
