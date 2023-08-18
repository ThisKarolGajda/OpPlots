package me.opkarol.opplots.inventories.player;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opplots.inventories.ItemPaletteGUI;
import me.opkarol.opplots.utils.PlayerLastPlayed;
import me.opkarol.opplots.utils.PlayerNameMatcher;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ChoosePlayerInventory {
    public ChoosePlayerInventory(String name, Player player, Consumer<OfflinePlayer> action) {
        ItemPaletteGUI<OfflinePlayer> gui = new ItemPaletteGUI.Builder<OfflinePlayer>("Wybierz gracza")
                .as(offlinePlayer -> {
                    Player player1 = offlinePlayer.getPlayer();
                    ItemStack item;
                    if (player1 == null) {
                        item = new ItemStack(Material.RED_WOOL);
                    } else {
                        item = HeadManager.getHeadValue(player1);
                    }

                    return new GuiItem(new ItemBuilder(item).setName("#<447cfc>&l" + offlinePlayer.getName()).setLore("&7Ostatnio online: " + PlayerLastPlayed.getPlayerLastPlayed(offlinePlayer), "&7UUID: " + offlinePlayer.getUniqueId()), event -> {
                        event.setCancelled(true);
                        player.closeInventory();
                        action.accept(offlinePlayer);
                    });
                })
                .build(PlayerNameMatcher.getSortedPlayersByName(name, player.getName()));

        gui.show(player);
        gui.update();
    }
}
