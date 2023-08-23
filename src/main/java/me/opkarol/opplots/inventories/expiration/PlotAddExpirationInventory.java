package me.opkarol.opplots.inventories.expiration;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.extensions.Vault;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.inventories.InventoryHelper;
import me.opkarol.opplots.inventories.plot.MainPlotInventory;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.PlotFunctions;
import me.opkarol.opplots.utils.ExpirationConverter;
import me.opkarol.opplots.utils.TimeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlotAddExpirationInventory {
    private final double EXPIRATION_COST_PER_HOUR = OpPlots.getInstance().getFilesManager().getConfigFile().getConfiguration().getDouble("plot.expiration.costPerHour");
    private final ChestGui gui;

    public PlotAddExpirationInventory(Plot plot, Player player) {
        gui = new ChestGui(3, "Przedłużanie ważności działki!");

        StaticPane itemsPane = new StaticPane(1, 1, 7, 1, Pane.Priority.HIGH);

        GuiItem oneHourAdd = new GuiItem(new ItemBuilder(Material.STONE)
                .setName("#<447cfc>&lPrzedłuż o 1 godzinę")
                .setLore("&7Koszt przedłużenia: " + EXPIRATION_COST_PER_HOUR),
                event -> {
                    event.setCancelled(true);
                    tryToBuy(player, 1, plot);
                });
        GuiItem twelveHoursAdd = new GuiItem(new ItemBuilder(Material.STONE)
                .setName("#<447cfc>&lPrzedłuż o 12 godzin")
                .setLore("&7Koszt przedłużenia: " + EXPIRATION_COST_PER_HOUR * 12),
                event -> {
                    event.setCancelled(true);
                    tryToBuy(player, 12, plot);
                });
        GuiItem oneDayAdd = new GuiItem(new ItemBuilder(Material.STONE)
                .setName("#<447cfc>&lPrzedłuż o 1 dzień")
                .setLore("&7Koszt przedłużenia: " + EXPIRATION_COST_PER_HOUR * 24),
                event -> {
                    event.setCancelled(true);
                    tryToBuy(player, 24, plot);
                });
        GuiItem threeDaysAdd = new GuiItem(new ItemBuilder(Material.STONE)
                .setName("#<447cfc>&lPrzedłuż o 3 dni")
                .setLore("&7Koszt przedłużenia: " + EXPIRATION_COST_PER_HOUR * 72),
                event -> {
                    event.setCancelled(true);
                    tryToBuy(player, 72, plot);
                });
        GuiItem oneWeekAdd = new GuiItem(new ItemBuilder(Material.STONE)
                .setName("#<447cfc>&lPrzedłuż o 7 dni")
                .setLore("&7Koszt przedłużenia: " + EXPIRATION_COST_PER_HOUR * 168),
                event -> {
                    event.setCancelled(true);
                    tryToBuy(player, 168, plot);
                });

        itemsPane.addItem(oneHourAdd, 0, 0);
        itemsPane.addItem(twelveHoursAdd, 1, 0);
        itemsPane.addItem(oneDayAdd, 2, 0);
        itemsPane.addItem(threeDaysAdd, 3, 0);
        itemsPane.addItem(oneWeekAdd, 4, 0);

        gui.addPane(getInformationPane(plot));
        StaticPane previousInventoryPane = new StaticPane(0, 0, 9, 3, Pane.Priority.HIGH);
        previousInventoryPane.addItem(new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f"))
                .setName("&7Powrót"),
                event -> {
                    event.setCancelled(true);
                    new MainPlotInventory(new PlotFunctions(plot), player);
                }), 8, 2);
        gui.addPane(previousInventoryPane);
        gui.addPane(InventoryHelper.backgroundPaneThreeRows);
        gui.addPane(itemsPane);
        gui.show(player);
        gui.update();
    }

    public void tryToBuy(Player player, int hours, Plot plot) {
        Vault vault = OpPlots.getInstance().getPluginManager().getVault();

        double cost = EXPIRATION_COST_PER_HOUR * hours;
        Vault.VAULT_RETURN_INFO returnInfo = vault.withdraw(player, cost);
        if (returnInfo == Vault.VAULT_RETURN_INFO.WITHDRAW_TOO_BROKE) {
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cNie masz wystarczająco pieniędzy. Potrzeba: " + cost + "!"));
            return;
        }

        if (returnInfo == Vault.VAULT_RETURN_INFO.WITHDRAW_SUCCESSFUL) {
            plot.addExpiration(TimeUtils.TimeUnit.HOUR.toMilliseconds() * hours);
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Pomyślnie przedłużono wartość o " + hours + " godzin!"));

            StaticPane informationPane = getInformationPane(plot);

            gui.getPanes().removeIf(pane -> pane.getPriority().equals(Pane.Priority.HIGHEST) && pane.getItems().size() == 1);
            gui.addPane(informationPane);
            gui.update();
        }
    }

    private StaticPane getInformationPane(Plot plot) {
        StaticPane informationPane = new StaticPane(1, 1, 7, 1, Pane.Priority.HIGHEST);
        GuiItem information = new GuiItem(new ItemBuilder(Material.ENCHANTED_BOOK)
                .setName("#<447cfc>&lInformacje")
                .setLore("&7Wygasa: " + ExpirationConverter.getTimeLeftString(plot.getExpiration())),
                event -> event.setCancelled(true));
        informationPane.addItem(information, 6, 0);
        return informationPane;
    }
}
