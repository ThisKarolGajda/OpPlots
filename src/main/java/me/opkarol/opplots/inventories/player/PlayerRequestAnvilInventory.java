package me.opkarol.opplots.inventories.player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class PlayerRequestAnvilInventory {

    public PlayerRequestAnvilInventory(Player player, Consumer<OfflinePlayer> playerConsumer) {
        AnvilGui gui = new AnvilGui("Podaj nazwę gracza");
        gui.setCost((short) 0);

        StaticPane pane = new StaticPane(0, 0, 1, 1);
        pane.addItem(new GuiItem(new ItemBuilder(Material.NAME_TAG).setName(" "), event -> event.setCancelled(true)), 0, 0);
        gui.getFirstItemComponent().addPane(pane);

        StaticPane pane1 = new StaticPane(0, 0, 1, 1);
        pane1.addItem(new GuiItem(new ItemBuilder(Material.BARRIER).setName("&k"), event -> event.setCancelled(true)), 0, 0);
        gui.getSecondItemComponent().addPane(pane1);

        StaticPane secondPane = new StaticPane(0, 0, 1, 1);
        secondPane.addItem(new GuiItem(new ItemBuilder(Material.NAME_TAG).setName("#<447cfc>&lNaciśnij, aby wybrać!"), event -> {
            event.setCancelled(true);
            player.closeInventory();
            new ChoosePlayerInventory(gui.getRenameText().replace(" ", ""), player, playerConsumer);
        }), 0, 0);
        gui.getResultComponent().addPane(secondPane);

        gui.update();
        gui.show(player);
    }
}
