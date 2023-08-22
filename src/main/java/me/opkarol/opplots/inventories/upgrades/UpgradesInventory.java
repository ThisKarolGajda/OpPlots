package me.opkarol.opplots.inventories.upgrades;

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
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.upgrades.PlotUpgrades;
import org.bukkit.entity.Player;

public class UpgradesInventory {
    private final ChestGui gui;

    public UpgradesInventory(Plot plot, Player player) {
        gui = new ChestGui(3, "Menu ulepszeń");

        StaticPane itemsPane = new StaticPane(1, 1, 7, 1, Pane.Priority.HIGH);
        PlotUpgrades plotUpgrades = plot.getUpgrades();

        GuiItem plotSizeUpgrade = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("c61b279e00f19d9f69a712f6560767cae0cfa02c83160d46c7f50c7526f6776e"))
                .setName("#<447cfc>&lUlepszenie rozmiaru działki")
                .setLore("&7Obecny poziom: #<447cfc>" + plotUpgrades.getLevel(PlotUpgrades.Type.PLOT_SIZE_UPGRADE), "&7Cena za kolejny poziom: #<447cfc>" + plotUpgrades.getCostForNextLevel(plot, PlotUpgrades.Type.PLOT_SIZE_UPGRADE), "&7Obecne wymiary działki: #<447cfc>" + getPlotSizes(plotUpgrades.getLevel(PlotUpgrades.Type.PLOT_SIZE_UPGRADE)), "", "&7Domyślnie rozmiar działki to 41x41, możesz", "&7ją powiększyć poprzez zakupienie ulepszenia.", "&7Każde ulepszenie dodaje 10 bloków w jedną stronę,", "&7a 6 ulepszenie (dostępne tylko dla rang premium) daje", "&7aż 20 bloków w jedną stronę!"),
                event -> {
            event.setCancelled(true);
            player.closeInventory();
            buyUpgrade(plot, player, plotUpgrades, PlotUpgrades.Type.PLOT_SIZE_UPGRADE);
        });

        GuiItem animalsGrowthUpgrade = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("3d597f77cde32c9ac9b06f82fcf7c9cb500facc14bff166222b24be39962f0ef"))
                .setName("#<447cfc>&lUlepszenie rozwoju zwierząt")
                .setLore("&7Obecny poziom: #<447cfc>" + plotUpgrades.getLevel(PlotUpgrades.Type.ANIMALS_GROWTH_UPGRADE), "&7Cena za kolejny poziom: #<447cfc>" + plotUpgrades.getCostForNextLevel(plot, PlotUpgrades.Type.ANIMALS_GROWTH_UPGRADE), "&7Obecny czas dojrzewania zwierząt to: #<447cfc>" + getAnimalMatureTime(plotUpgrades.getLevel(PlotUpgrades.Type.ANIMALS_GROWTH_UPGRADE)), "", "&7Każde ulepszenie powoduje zmniejszenie czasu", "&7dorastania zwierząt.", "&7Za każdym ulepszeniem twoje nowe zwierzaki", "&7będą o 3 minuty starsze!"),
                event -> {
            event.setCancelled(true);
            player.closeInventory();
            buyUpgrade(plot, player, plotUpgrades, PlotUpgrades.Type.ANIMALS_GROWTH_UPGRADE);
        });

        GuiItem plantsGrowthUpgrade = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("49392a2bfa1c4a795bad101797cd54077910c55c1fa8ae55b679e95d2c6e860f"))
                .setName("#<447cfc>&lUlepszenie rozwoju roślin")
                .setLore("&7Obecny poziom: #<447cfc>" + plotUpgrades.getLevel(PlotUpgrades.Type.PLANTS_GROWTH_UPGRADE), "&7Cena za kolejny poziom: #<447cfc>" + plotUpgrades.getCostForNextLevel(plot, PlotUpgrades.Type.PLANTS_GROWTH_UPGRADE), "", "&7Nie chcesz tyle czekać na swoje roślinki?", "&7Zmniejsz oczekiwanie poprzez ulepszanie!", "&7Ulepszenia po kolei to:", " &71 level - 10% na +1 etap rośnięcia", " &72 level - 25% na +1 etap rośnięcia", " &73 level - 40% na +1 etap rośnięcia", " &74 level - 80% na +1 etap rośnięcia", " &75 level - +1 etap rośnięcia"),
                event -> {
            event.setCancelled(true);
            player.closeInventory();
            buyUpgrade(plot, player, plotUpgrades, PlotUpgrades.Type.PLANTS_GROWTH_UPGRADE);
        });

        itemsPane.addItem(plotSizeUpgrade, 0, 0);
        itemsPane.addItem(animalsGrowthUpgrade, 3, 0);
        itemsPane.addItem(plantsGrowthUpgrade, 6, 0);

        gui.addPane(itemsPane);
        gui.addPane(InventoryHelper.backgroundPaneThreeRows);
        gui.update();
        gui.show(player);
    }

    private String getAnimalMatureTime(int level) {
        return switch (level) {
            case 0 -> "20 minut";
            case 1 -> "17 minut";
            case 2 -> "14 minut";
            case 3 -> "11 minut";
            case 4 -> "8 minut";
            case 5 -> "5 minut";
            default -> throw new IllegalStateException("Unexpected value: " + level);
        };
    }

    private String getPlotSizes(int level) {
        return switch (level) {
            case 0 -> "41x41";
            case 1 -> "61x61";
            case 2 -> "81x81";
            case 3 -> "101x101";
            case 4 -> "121x121";
            case 5 -> "131x131";
            case 6 -> "151x151";
            default -> throw new IllegalStateException("Unexpected value: " + level);
        };
    }

    private void buyUpgrade(Plot plot, Player player, PlotUpgrades plotUpgrades, PlotUpgrades.Type type) {
        Vault vault = OpPlots.getInstance().getPluginManager().getVault();
        if (!plotUpgrades.canLevelUp(plot, type)) {
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cNie można już tego ulepszyć!"));
            return;
        }

        double cost = plotUpgrades.getCostForNextLevel(plot, type);
        if (!vault.has(player, cost)) {
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cNie masz wystarczająco pieniędzy. Potrzeba: " + cost + " pieniędzy!"));
            return;
        }

        Vault.VAULT_RETURN_INFO returnInfo = vault.withdraw(player, cost);
        if (returnInfo == Vault.VAULT_RETURN_INFO.WITHDRAW_SUCCESSFUL) {
            plotUpgrades.increaseLevel(plot, type);
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Pomyślnie ulepszono!"));
        }
    }

    public ChestGui getGui() {
        return gui;
    }
}
