package me.opkarol.opplots.inventories.members;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.inventories.ItemPaletteGUI;
import me.opkarol.opplots.inventories.player.PlayerRequestAnvilInventory;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.utils.PlayerLastPlayed;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class ManageMembersPagedInventory {
    private final ItemPaletteGUI<UUID> gui;

    public ManageMembersPagedInventory(Plot plot, Player player) {
        gui = new ItemPaletteGUI.Builder<UUID>("Zarządzaj członkami")
                .as(uuid -> {
                    OfflinePlayer clicked = Bukkit.getOfflinePlayer(uuid);
                    ItemStack item;
                    if (clicked.isOnline()) {
                        item = HeadManager.getHeadValue(clicked.getPlayer());
                    } else {
                        item = new ItemStack(Material.RED_WOOL);
                    }

                    return new GuiItem(new ItemBuilder(item).setName("#<447cfc>&l" + clicked.getName()).setLore("&7Naciśnij #<447cfc>LPM &7aby usunąć tego użytkownika", "&7Ostatnio online: " + PlayerLastPlayed.getPlayerLastPlayed(clicked), "&7UUID: " + clicked.getUniqueId()), event -> {
                        event.setCancelled(true);
                        if (event.isShiftClick()) {
                            return;
                        }

                        Player owner = (Player) event.getWhoClicked();
                        if (event.isLeftClick()) {
                            plot.removeMember(clicked);
                            if (clicked.isOnline()) {
                                clicked.getPlayer().sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Usunięto cię z działki: " + plot.getName() + ", przez: " + plot.getOwnerName() + "."));
                            }
                            owner.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Usunięto gracza: " + clicked.getName() + ", z działki: " + plot.getName() + "."));
                            owner.closeInventory();
                        }
                    });
                })
                .displayItems(List.of(new GuiItem(new ItemBuilder(Material.GREEN_TERRACOTTA).setName("#<447cfc>&lDodaj członka"), event -> new PlayerRequestAnvilInventory(player, newPlayer -> {
                    if (plot.isAdded(newPlayer)) {
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cTen gracz jest już dodany!"));
                        return;
                    }

                    plot.addMember(newPlayer);
                    if (newPlayer.isOnline()) {
                        newPlayer.getPlayer().sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Dodano cię do działki: " + plot.getName() + ", przez: " + player.getName() + "."));
                    }
                    player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Dodano gracza: " + newPlayer.getName() + ", do działki: " + plot.getName() + "."));
                }))))
                .build(plot.getMembers());

        gui.update();
        gui.show(player);
    }

    public ItemPaletteGUI<UUID> getGui() {
        return gui;
    }
}
