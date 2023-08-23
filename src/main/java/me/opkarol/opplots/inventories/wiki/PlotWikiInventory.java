package me.opkarol.opplots.inventories.wiki;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opplots.inventories.InventoryHelper;
import org.bukkit.entity.Player;

public class PlotWikiInventory {
    private final ChestGui gui;

    public PlotWikiInventory(Player player) {
        gui = new ChestGui(5, "Wikipedia");

        StaticPane itemsPane = new StaticPane(1, 1, 7, 3, Pane.Priority.HIGH);

        GuiItem availableCommands = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("dfaa5d3cfeab33ed05b566aba624c74a7c7655454f7442de07ad3a742e364903"))
                .setName("#<447cfc>&lDostępne komendy")
                .setLore("&7Komendy dotyczące działek, które możesz użyć poza panelem:", "&7/dzialka ustawdom - ustawia dom działki tam, gdzie stoisz", "&7/dzialka usun - usuwa działkę!"),
                event -> event.setCancelled(true));

        GuiItem plotSizes = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("a1c0e69f2cdb5426aaed711ce03ea6d709c92e9beb84ad1d22b584c461d5335d"))
                .setName("#<447cfc>&lRozmiary działek")
                .setLore("&7Masz duże ambicje, a początkowa działka jest za mała?",
                         "&7Nie szkodzi, zawsze możesz ją powiększyć",
                         "&7Dostępne jest 5 poziomów (+1 za rang premium),",
                         "&7które za każdym razem zwiększają wielkość",
                         "&7Twojej posiadłości o 5 kratek w każdą stronę.",
                         "&7Poziomy są wielkości kolejno:",
                         "&7poziom 0 (domyślny) - 41x41",
                         "&7poziom 1 - 51x51",
                         "&7poziom 2 - 61x61",
                         "&7poziom 3 - 71x71",
                         "&7poziom 4 - 81x81",
                         "&7poziom 5 - 91x91",
                         "&7poziom 6 (dla rang premium) - 111x111"),
                event -> event.setCancelled(true));

        GuiItem plotLimits = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("a3852bf616f31ed67c37de4b0baa2c5f8d8fca82e72dbcafcba66956a81c4"))
                .setName("#<447cfc>&lLimity działek")
                .setLore("&7Na naszym serwerze możesz mieć 1 działkę.", "&7Do działek innych możesz być dodany w nieograniczonej ilości.", "&7Tak samo wygląda sprawa z liczbą osób", "&7które możesz dodać do działki i zablokować."),
                event -> event.setCancelled(true));

        GuiItem addMember = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("a4d6dd99928e32b34596c60d6164535fd06d56d85fb3990ef3dcbbc939cf8034"))
                .setName("#<447cfc>&lJak dodać członka?")
                .setLore("&7Wejdź w panel pod komendą /dzialka.", "&7Następnie wybierz opcje 'Zarządzanie członkami'.", "&7Kliknij dodaj członka i wpisz jego nick (nie musi być w całości)", "&7a następnie kliknij znacznik o nazwie 'Naciśnij aby wybrać'.", "&7Następnie z listy graczy wybierz tego, którego chcesz dodać.", "&7Na początku będą znajdować się osoby", "&7które mają najbardziej podobny nick do tego który wpisałeś. ", "&7Dodawany gracz nie musi być wtedy online."),
                event -> event.setCancelled(true));

        GuiItem upgrades = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("399ad7a0431692994b6c412c7eafb9e0fc49975240b73a27d24ed797035fb894"))
                .setName("#<447cfc>&lUlepszenia działek")
                .setLore("&7Ulepszenia to kolejna rzecz, która", "&7może poprawić wydajność i efektywność Twojej działki.", "&7Możesz zakupić parę ulepszeń, które pomogą Ci w zbiorach", "&7hodowli bądź powiększeniu działki."),
                event -> event.setCancelled(true));

        GuiItem settings = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("204a6fc8f0cdcb1332ad98354ecba1db595253642b6b6182258bb183625d1892"))
                .setName("#<447cfc>&lUstawienia działek")
                .setLore("&7W naszych działkach dostępne są 4 ustawienia, które za darmo ", "&7lub drobną opłatą możesz ustawić. ", "&7Masz możliwość ustawić pogodę, porę dnia, czy chcesz", "&7aby respiły się potwory oraz czy ma być włączone PVP na działce.", "&7Wszystko dokonasz z pozycji panelu pod komendą /działka."),
                event -> event.setCancelled(true));

        itemsPane.addItem(availableCommands, 0, 1);
        itemsPane.addItem(plotSizes, 2, 1);
        itemsPane.addItem(plotLimits, 4, 1);
        itemsPane.addItem(addMember, 6, 1);
        itemsPane.addItem(upgrades, 3, 2);
        itemsPane.addItem(settings, 3, 0);

        gui.addPane(itemsPane);
        gui.addPane(InventoryHelper.backgroundPaneFiveRows);
        gui.update();
        gui.show(player);
    }

    public ChestGui getGui() {
        return gui;
    }
}
