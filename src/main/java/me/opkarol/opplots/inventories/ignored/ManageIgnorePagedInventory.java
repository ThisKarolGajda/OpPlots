package me.opkarol.opplots.inventories.ignored;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opc.api.wrappers.OpSound;
import me.opkarol.opplots.inventories.ItemPaletteGUI;
import me.opkarol.opplots.inventories.player.PlayerRequestAnvilInventory;
import me.opkarol.opplots.inventories.plot.MainPlotInventory;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.PlotFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class ManageIgnorePagedInventory {
    private final ItemPaletteGUI<UUID> gui;
    private final OpSound addSound = new OpSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);

    public ManageIgnorePagedInventory(Plot plot, Player player) {
        gui = new ItemPaletteGUI.Builder<UUID>("Zarządzaj ignorowanymi")
                .as(uuid -> {
                    OfflinePlayer clicked = Bukkit.getOfflinePlayer(uuid);
                    ItemStack item;
                    if (clicked.isOnline()) {
                        item = HeadManager.getHeadValue(clicked.getPlayer());
                    } else {
                        item = new ItemStack(Material.RED_WOOL);
                    }

                    return new GuiItem(new ItemBuilder(item).setName("#<447cfc>&l" + clicked.getName()).setLore("&7Naciśnij #<447cfc>LPM &7aby usunąć ignorowanie tego użytkownika"), event -> {
                        event.setCancelled(true);
                        if (event.isShiftClick()) {
                            return;
                        }

                        Player owner = (Player) event.getWhoClicked();
                        if (event.isLeftClick()) {
                            plot.removeIgnored(clicked);
                            owner.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Usunięto ignorowanego: " + clicked.getName() + ", z działki: " + plot.getName() + "."));
                            owner.closeInventory();
                        }
                    });
                })
                .displayItems(List.of(new GuiItem(new ItemBuilder(Material.GREEN_TERRACOTTA).setName("#<447cfc>&lDodaj ignorowanego"), event -> new PlayerRequestAnvilInventory(player, newPlayer -> {
                    if (plot.isIgnored(newPlayer)) {
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cTen gracz jest już ignorowany!"));
                        return;
                    }

                    if (plot.isMember(newPlayer)) {
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cTen gracz jest członkiem, nie można go ignorować!"));
                        return;
                    }

                    new ManageIgnorePagedInventory(plot, player);
                    plot.addIgnored(newPlayer);
                    player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Dodano gracza: " + newPlayer.getName() + ", do ignorowanych w działce: " + plot.getName() + "."));
                    addSound.play(player);
                }))))
                .build(plot.getIgnored(), 3, () -> new MainPlotInventory(new PlotFunctions(plot), player));
        gui.update();
        gui.show(player);
    }

    public ItemPaletteGUI<UUID> getGui() {
        return gui;
    }
}
