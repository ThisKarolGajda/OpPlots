package me.opkarol.opplots.inventories.upgrades;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.extensions.Vault;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opc.api.wrappers.OpSound;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.inventories.InventoryHelper;
import me.opkarol.opplots.inventories.plot.MainPlotInventory;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.PlotFunctions;
import me.opkarol.opplots.plots.upgrades.PlotUpgrades;
import me.opkarol.opplots.utils.NumberFormatting;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import static me.opkarol.opplots.inventories.settings.SettingsInventory.errorSound;
import static me.opkarol.opplots.plots.upgrades.PlotUpgrades.getMaxPlayersInPlot;

public class UpgradesInventory {
    private final ChestGui gui;
    private final OpSound levelUpSound = new OpSound(Sound.ENTITY_PLAYER_LEVELUP);

    public UpgradesInventory(Plot plot, Player player) {
        gui = new ChestGui(3, "Menu ulepszeń");

        createItemsPane(plot, player);

        StaticPane previousInventoryPane = new StaticPane(0, 0, 9, 3, Pane.Priority.HIGHEST);
        previousInventoryPane.addItem(new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f"))
                .setName("&7Powrót"),
                event -> {
                    event.setCancelled(true);
                    new MainPlotInventory(new PlotFunctions(plot), player);
                }), 8, 2);
        gui.addPane(previousInventoryPane);

        gui.addPane(InventoryHelper.backgroundPaneThreeRows);
        gui.update();
        gui.show(player);
    }

    private void createItemsPane(Plot plot, Player player) {
        StaticPane itemsPane = new StaticPane(1, 1, 7, 1, Pane.Priority.HIGH);
        PlotUpgrades plotUpgrades = plot.getUpgrades();

        GuiItem plotSizeUpgrade = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("c61b279e00f19d9f69a712f6560767cae0cfa02c83160d46c7f50c7526f6776e"))
                .setName("#<447cfc>&lUlepszenie rozmiaru działki")
                .setLore("&7Obecny poziom: #<447cfc>" + plotUpgrades.getLevel(PlotUpgrades.Type.PLOT_SIZE_UPGRADE), "&7Cena za kolejny poziom: #<447cfc>" + getCostForNextLevel(plot, plotUpgrades, PlotUpgrades.Type.PLOT_SIZE_UPGRADE), "&7Obecne wymiary działki: #<447cfc>" + getPlotSizes(plotUpgrades.getLevel(PlotUpgrades.Type.PLOT_SIZE_UPGRADE)), "", "&7Domyślnie rozmiar działki to 41x41, możesz", "&7ją powiększyć poprzez zakupienie ulepszenia.", "&7Każde ulepszenie dodaje 5 bloków w jedną stronę,", "&7a 6 ulepszenie (dostępne tylko dla rang premium) daje", "&7aż 10 bloków w jedną stronę!"),
                event -> {
                    handleEventClick(event, plot, player, plotUpgrades, PlotUpgrades.Type.PLOT_SIZE_UPGRADE);
                });

        GuiItem animalsGrowthUpgrade = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("3d597f77cde32c9ac9b06f82fcf7c9cb500facc14bff166222b24be39962f0ef"))
                .setName("#<447cfc>&lUlepszenie rozwoju zwierząt")
                .setLore("&7Obecny poziom: #<447cfc>" + plotUpgrades.getLevel(PlotUpgrades.Type.ANIMALS_GROWTH_UPGRADE), "&7Cena za kolejny poziom: #<447cfc>" + getCostForNextLevel(plot, plotUpgrades, PlotUpgrades.Type.ANIMALS_GROWTH_UPGRADE), "&7Obecny czas dojrzewania zwierząt to: #<447cfc>" + getAnimalMatureTime(plotUpgrades.getLevel(PlotUpgrades.Type.ANIMALS_GROWTH_UPGRADE)), "", "&7Każde ulepszenie powoduje zmniejszenie czasu", "&7dorastania zwierząt.", "&7Z każdym ulepszeniem twoje nowe zwierzaki", "&7będą dorastać o 2 minuty szybciej!"),
                event -> {
                    handleEventClick(event, plot, player, plotUpgrades, PlotUpgrades.Type.ANIMALS_GROWTH_UPGRADE);
                });

        GuiItem playersLimitUpgrade = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("7fe9725c950472e469b9fccae32f61bcefebdb5ea9ce9c92d58171ffb7a336fe"))
                .setName("#<447cfc>&lUlepszenie limitu graczy")
                .setLore("&7Obecny poziom: #<447cfc>" + plotUpgrades.getLevel(PlotUpgrades.Type.PLAYER_LIMIT_UPGRADE), "&7Cena za kolejny poziom: #<447cfc>" + getCostForNextLevel(plot, plotUpgrades, PlotUpgrades.Type.PLAYER_LIMIT_UPGRADE), "&7Teraz działka może mieć: #<447cfc>" + getMaxPlayersInPlot(plot) + " członków", "", "&7Dzięki każdemu ulepszeniu będziesz mógł dodać", "&71 znajomego więcej do swojej działki!", "&7Łącznie jest 15 ulepszeń, z czego 5", "&7jest dostępnych tylko dla rang premium!"),
                event -> {
                    handleEventClick(event, plot, player, plotUpgrades, PlotUpgrades.Type.PLAYER_LIMIT_UPGRADE);
                });

        GuiItem plantsGrowthUpgrade = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("49392a2bfa1c4a795bad101797cd54077910c55c1fa8ae55b679e95d2c6e860f"))
                .setName("#<447cfc>&lUlepszenie rozwoju roślin")
                .setLore("&7Obecny poziom: #<447cfc>" + plotUpgrades.getLevel(PlotUpgrades.Type.PLANTS_GROWTH_UPGRADE), "&7Cena za kolejny poziom: #<447cfc>" + getCostForNextLevel(plot, plotUpgrades, PlotUpgrades.Type.PLANTS_GROWTH_UPGRADE), "", "&7Nie chcesz tyle czekać na swoje roślinki?", "&7Zmniejsz oczekiwanie poprzez ulepszanie!", "&7Ulepszenia po kolei to:", " &71 level - 10% na +1 etap rośnięcia", " &72 level - 25% na +1 etap rośnięcia", " &73 level - 40% na +1 etap rośnięcia", " &74 level - 80% na +1 etap rośnięcia", " &75 level - +1 etap rośnięcia"),
                event -> {
                    handleEventClick(event, plot, player, plotUpgrades, PlotUpgrades.Type.PLANTS_GROWTH_UPGRADE);
                });

        itemsPane.addItem(plotSizeUpgrade, 0, 0);
        itemsPane.addItem(animalsGrowthUpgrade, 2, 0);
        itemsPane.addItem(playersLimitUpgrade, 4, 0);
        itemsPane.addItem(plantsGrowthUpgrade, 6, 0);
        gui.addPane(itemsPane);
        gui.update();
    }

    private void handleEventClick(InventoryClickEvent event, Plot plot, Player player, PlotUpgrades plotUpgrades, PlotUpgrades.Type plotSizeUpgrade) {
        event.setCancelled(true);
        buyUpgrade(plot, player, plotUpgrades, plotSizeUpgrade);
        gui.getPanes().removeIf(pane -> pane.getItems().size() == 4 && pane.getPriority() == Pane.Priority.HIGH);
        createItemsPane(plot, player);
    }

    private String getCostForNextLevel(Plot plot, PlotUpgrades plotUpgrades, PlotUpgrades.Type type) {
        double value = plotUpgrades.getCostForNextLevel(plot, type);
        if (value == 0) {
            return "Osiągnąłeś już maksymalny poziom!";
        }
        return String.valueOf(NumberFormatting.formatNumber(value));
    }

    private String getAnimalMatureTime(int level) {
        return switch (level) {
            case 0 -> "20 minut";
            case 1 -> "18 minut";
            case 2 -> "16 minut";
            case 3 -> "14 minut";
            case 4 -> "12 minut";
            case 5 -> "10 minut";
            default -> throw new IllegalStateException("Unexpected value: " + level);
        };
    }

    private String getPlotSizes(int level) {
        return switch (level) {
            case 0 -> "41x41";
            case 1 -> "51x51";
            case 2 -> "61x61";
            case 3 -> "71x71";
            case 4 -> "81x81";
            case 5 -> "91x91";
            case 6 -> "111x111";
            default -> throw new IllegalStateException("Unexpected value: " + level);
        };
    }

    private void buyUpgrade(Plot plot, Player player, PlotUpgrades plotUpgrades, PlotUpgrades.Type type) {
        Vault vault = OpPlots.getInstance().getPluginManager().getVault();
        if (!plotUpgrades.canLevelUp(plot, type)) {
            errorSound.play(player);
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cNie można już tego ulepszyć!"));
            return;
        }

        double cost = plotUpgrades.getCostForNextLevel(plot, type);
        if (!vault.has(player, cost)) {
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cNie masz wystarczająco pieniędzy. Potrzeba: " + NumberFormatting.formatNumber(cost) + " pieniędzy!"));
            errorSound.play(player);
            return;
        }

        Vault.VAULT_RETURN_INFO returnInfo = vault.withdraw(player, cost);
        if (returnInfo == Vault.VAULT_RETURN_INFO.WITHDRAW_SUCCESSFUL) {
            plotUpgrades.increaseLevel(plot, type);
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Pomyślnie ulepszono!"));
            levelUpSound.play(player);
        }
    }

    public ChestGui getGui() {
        return gui;
    }
}
