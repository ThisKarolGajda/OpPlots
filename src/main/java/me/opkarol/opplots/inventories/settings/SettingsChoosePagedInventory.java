package me.opkarol.opplots.inventories.settings;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import me.opkarol.opc.api.extensions.Vault;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.inventories.ItemPaletteGUI;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.settings.PlotSettings;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;

public class SettingsChoosePagedInventory {

    public SettingsChoosePagedInventory(Plot plot, PlotSettings.Type type, Player player) {
        PlotSettings settings = plot.getSettings();
        ItemPaletteGUI<Tuple<String, Object>> gui = new ItemPaletteGUI.Builder<Tuple<String, Object>>("Zarządzanie ustawieniami")
                .as(tuple -> {
                    String familyName = tuple.first();
                    Object object = tuple.second();
                    boolean isOwned = settings.isOwned(type, object);
                    boolean isCurrent = settings.isCurrent(type, object);
                    ItemBuilder itemBuilder = new ItemBuilder(isOwned ? Material.GREEN_WOOL : Material.RED_WOOL)
                            .setName(familyName == null ? type.getToString().apply(object) : familyName)
                            .setLore("&7Posiadasz: #<447cfc>" + isOwned, "&7Aktywny: #<447cfc>" + isCurrent, "&7Cena: #<447cfc>" + type.getCost());
                    if (isCurrent) {
                        itemBuilder.setEnchants( Tuple.of(Enchantment.LUCK, 1));
                        itemBuilder.setFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
                    }

                    return new GuiItem(itemBuilder,
                            event -> {
                        player.closeInventory();
                        if (!isOwned) {
                            Vault vault = OpPlots.getInstance().getPluginManager().getVault();
                            double cost = settings.calculateCostForType(plot, type);
                            if (!vault.has(player, cost)) {
                                player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cNie masz wystarczająco pieniędzy!"));
                                return;
                            }

                            if (vault.withdraw(player, cost) == Vault.VAULT_RETURN_INFO.WITHDRAW_SUCCESSFUL) {
                                player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Pomyślnie zakupiono!"));
                                settings.addOwned(plot, type, object);
                                settings.setCurrent(plot, type, object);
                            }
                        } else {
                            settings.setCurrent(plot, type, object);
                            player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Zmienono aktywne ustawienie na: " + type.getToString().apply(object)) + ".");
                        }
                    });
                })
                .build(Arrays.stream(type.getObjects()).toList());

        gui.update();
        gui.show(player);
    }
}
