package me.opkarol.opplots.inventories.settings;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.extensions.Vault;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.misc.Tuple;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opc.api.wrappers.OpSound;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.inventories.InventoryHelper;
import me.opkarol.opplots.inventories.plot.MainPlotInventory;
import me.opkarol.opplots.inventories.settings.types.DayChooseInventory;
import me.opkarol.opplots.inventories.settings.types.WeatherChooseInventory;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.PlotFunctions;
import me.opkarol.opplots.plots.settings.PlotSettings;
import me.opkarol.opplots.utils.NumberFormatting;
import me.opkarol.opplots.utils.StringIconUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Objects;

public class SettingsInventory {
    private final ChestGui gui;
    private static final OpSound changeSettingSound = new OpSound(Sound.UI_BUTTON_CLICK);
    public static final OpSound errorSound = new OpSound(Sound.ENTITY_PLAYER_HURT);

    public SettingsInventory(Plot plot, Player player) {
        gui = new ChestGui(3, "Menu ustawień");

        StaticPane previousInventoryPane = new StaticPane(0, 0, 9, 3, Pane.Priority.HIGHEST);
        previousInventoryPane.addItem(new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f"))
                .setName("&7Powrót"),
                event -> {
            event.setCancelled(true);
            new MainPlotInventory(new PlotFunctions(plot), player);
        }), 8, 2);
        gui.addPane(previousInventoryPane);

        gui.addPane(InventoryHelper.backgroundPaneThreeRows);
        buildItemsPane(plot, player);
        gui.update();
        gui.show(player);
    }

    private void buildItemsPane(Plot plot, Player player) {
        StaticPane itemsPane = new StaticPane(1, 1, 8, 1, Pane.Priority.HIGH);

        GuiItem weatherChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("c465c121958c0522e3dccb3d14d68612d6317cd380b0e646b61b7420b904af02"))
                .setName("#<447cfc>&lUstawienia pogody")
                .setLore("&7Obecne ustawienie: #<447cfc>" + getWeatherCurrentSettings(plot)),
                event -> {
                    event.setCancelled(true);
                    new WeatherChooseInventory(player, plot);
                });

        GuiItem dayChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("71a11a03d6bc75b144be85848556a15f358ee6a65e0466f6c8b706e7f9bcf14"))
                .setName("#<447cfc>&lUstawienia pory dnia")
                .setLore("&7Obecne ustawienie: #<447cfc>" + getDayTimeCurrentSettings(plot)),
                event -> {
                    event.setCancelled(true);
                    new DayChooseInventory(player, plot);
                });

        GuiItem animalsSpawnChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("1caaa7fc0396850088bbdd0f8007e2c1a29abc9088c6d24b7943db65ebc814f8"))
                .setName("#<447cfc>&lSpawn potworów na działce")
                .setLore("&7Spawn potworów: " + StringIconUtil.getReturnedEmojiFromBoolean(!plot.getSettings().isCurrent(PlotSettings.Type.ANIMALS_SPAWN_CHANGE, true)), "&7Kliknij aby zmienić to ustawienie za darmo!"),
                event -> {
                    event.setCancelled(true);
                    if (plot.getSettings().isCurrent(PlotSettings.Type.ANIMALS_SPAWN_CHANGE, true)) {
                        plot.getSettings().setCurrent(plot, PlotSettings.Type.ANIMALS_SPAWN_CHANGE, false);
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Zmieniono ustawienie spawnu potworów na włączone!"));
                    } else {
                        plot.getSettings().setCurrent(plot, PlotSettings.Type.ANIMALS_SPAWN_CHANGE, true);
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Zmieniono ustawienie spawnu potworów na wyłączone!"));
                    }
                    changeSettingSound.play(player);
                    gui.getPanes().removeIf(pane1 -> pane1.getItems().size() == 4);
                    buildItemsPane(plot, player);
                    gui.update();
                });

        GuiItem pvpChange = new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("1765341353c029e9b655f4f57931ae6adc2c7a73c657945d945a307641d3778"))
                .setName("#<447cfc>&lPVP na działce")
                .setLore("&7Włączone: " + StringIconUtil.getReturnedEmojiFromBoolean(plot.getSettings().isCurrent(PlotSettings.Type.PVP_CHANGE, true)), "&7Kliknij aby zmienić to ustawienie za darmo!", "&7Kto by nie chciał czuć się bezpieczny!"),
                event -> {
                    event.setCancelled(true);
                    if (plot.getSettings().isCurrent(PlotSettings.Type.PVP_CHANGE, true)) {
                        plot.getSettings().setCurrent(plot, PlotSettings.Type.PVP_CHANGE, false);
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Zmieniono ustawienie PVP na wyłączone!"));
                    } else {
                        plot.getSettings().setCurrent(plot, PlotSettings.Type.PVP_CHANGE, true);
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Zmieniono ustawienie PVP na włączone!"));
                    }
                    changeSettingSound.play(player);
                    gui.getPanes().removeIf(pane1 -> pane1.getItems().size() == 4);
                    buildItemsPane(plot, player);
                    gui.update();
                });

        itemsPane.addItem(weatherChange, 0, 0);
        itemsPane.addItem(dayChange, 2, 0);
        itemsPane.addItem(animalsSpawnChange, 4, 0);
        itemsPane.addItem(pvpChange, 6, 0);
        gui.addPane(itemsPane);
    }

    private Object getWeatherCurrentSettings(Plot plot) {
        PlotSettings settings = plot.getSettings();
        Tuple<String, Object>[] tuples = PlotSettings.Type.WEATHER_CHANGE.getObjects();
        Object object = settings.getCurrent(PlotSettings.Type.WEATHER_CHANGE);

        for (Tuple<String, Object> tuple : tuples) {
            if (Objects.equals(tuple.second(), object)) {
                return tuple.first();
            }
        }

        return null;
    }

    private Object getDayTimeCurrentSettings(Plot plot) {
        PlotSettings settings = plot.getSettings();
        Tuple<String, Object>[] tuples = PlotSettings.Type.DAY_TIME_CHANGE.getObjects();
        Object object = settings.getCurrent(PlotSettings.Type.DAY_TIME_CHANGE);

        for (Tuple<String, Object> tuple : tuples) {
            if (Objects.equals(tuple.second(), object)) {
                return tuple.first();
            }
        }

        return null;
    }

    public ChestGui getGui() {
        return gui;
    }

    public static GuiItem getChosenItem(Player player, PlotSettings.Type type, String identifier, Plot plot, Material material, Runnable runnable) {
        Tuple<String, Object>[] tuples = type.getObjects();
        Tuple<String, Object> tuple = null;

        for (Tuple<String, Object> tuple1 : tuples) {
            if (tuple1.first().equals(identifier)) {
                tuple = tuple1;
                break;
            }
        }

        if (tuple == null) {
            throw new RuntimeException("OPLOTS_BŁĄD: Brak typu " + identifier);
        }

        PlotSettings settings = plot.getSettings();

        Object object = tuple.second();
        boolean isOwned = settings.isOwned(type, object);
        boolean isCurrent = settings.isCurrent(type, object);
        String familyName = tuple.first();
        double cost = settings.calculateCostForType(plot, type);

        ItemBuilder itemBuilder = new ItemBuilder(material)
                .setName("#<447cfc>&l" + (familyName == null ? type.getToString().apply(object) : familyName))
                .setLore("&7Posiadasz: " + StringIconUtil.getReturnedEmojiFromBoolean(isOwned), "&7Aktywny: " + StringIconUtil.getReturnedEmojiFromBoolean(isCurrent), isOwned ? "&7Wykupione!" : "&7Cena: #<447cfc>" + NumberFormatting.formatNumber(cost));
        if (isCurrent) {
            itemBuilder.setEnchants(Tuple.of(Enchantment.LUCK, 1));
            itemBuilder.setFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        }

        return new GuiItem(itemBuilder,
                event -> {
                    event.setCancelled(true);
                    if (!isOwned) {
                        Vault vault = OpPlots.getInstance().getPluginManager().getVault();
                        if (!vault.has(player, cost)) {
                            player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cNie masz wystarczająco pieniędzy!"));
                            errorSound.play(player);
                            return;
                        }

                        if (vault.withdraw(player, cost) == Vault.VAULT_RETURN_INFO.WITHDRAW_SUCCESSFUL) {
                            player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Pomyślnie zakupiono!"));
                            settings.addOwned(plot, type, object);
                            settings.setCurrent(plot, type, object);
                            changeSettingSound.play(player);
                        }
                    } else {
                        settings.setCurrent(plot, type, object);
                        player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Zmieniono aktywne ustawienie na: " + familyName + "."));
                        changeSettingSound.play(player);
                    }

                    runnable.run();
                });
    }
}
