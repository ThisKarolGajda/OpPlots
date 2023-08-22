package me.opkarol.opplots.inventories.expiration;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.extensions.Vault;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.inventories.InventoryHelper;
import me.opkarol.opplots.plots.Plot;
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
        GuiItem information = new GuiItem(new ItemBuilder(Material.ENCHANTED_BOOK)
                .setName("#<447cfc>&lInformacje")
                .setLore("&7Wygasa: " + ExpirationConverter.getTimeLeftString(plot.getExpiration())),
                event -> event.setCancelled(true));
        itemsPane.addItem(oneHourAdd, 0, 0);
        itemsPane.addItem(twelveHoursAdd, 1, 0);
        itemsPane.addItem(oneDayAdd, 2, 0);
        itemsPane.addItem(threeDaysAdd, 3, 0);
        itemsPane.addItem(oneWeekAdd, 4, 0);
        itemsPane.addItem(information, 6, 0);

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
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cNie masz wystarczająco pieniędzy. Potrzeba: " + cost + "!"));
            return;
        }

        if (returnInfo == Vault.VAULT_RETURN_INFO.WITHDRAW_SUCCESSFUL) {
            plot.addExpiration(TimeUtils.TimeUnit.HOUR.toMilliseconds() * hours);
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Pomyślnie przedłużono wartość o " + hours + " godzin!"));

            StaticPane itemsPane = new StaticPane(1, 1, 7, 1, Pane.Priority.HIGHEST);
            GuiItem information = new GuiItem(new ItemBuilder(Material.ENCHANTED_BOOK)
                    .setName("#<447cfc>&lInformacje")
                    .setLore("&7Wygasa: " + plot.getExpirationLeftString()),
                    event -> event.setCancelled(true));
            itemsPane.addItem(information, 6, 0);
            gui.addPane(itemsPane);
            gui.update();
        }
    }
}
