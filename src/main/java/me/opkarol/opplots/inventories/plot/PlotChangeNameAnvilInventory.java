package me.opkarol.opplots.inventories.plot;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.plots.Plot;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlotChangeNameAnvilInventory {
    public PlotChangeNameAnvilInventory(Plot plot, Player player) {
        final int MINIMAL_NAME_LENGTH = OpPlots.getInstance().getFilesManager().getConfigFile().getConfiguration().getInt("plot.name.minimal");
        final int MAXIMAL_NAME_LENGTH = OpPlots.getInstance().getFilesManager().getConfigFile().getConfiguration().getInt("plot.name.maximal");

        AnvilGui gui = new AnvilGui(FormatUtils.formatMessage("Zmień nazwę działki"));
        gui.setCost((short) 0);

        StaticPane pane = new StaticPane(0, 0, 1, 1);
        pane.addItem(new GuiItem(new ItemBuilder(Material.NAME_TAG).setName(plot.getName()), event -> event.setCancelled(true)), 0, 0);
        gui.getFirstItemComponent().addPane(pane);

        StaticPane pane1 = new StaticPane(0, 0, 1, 1);
        pane1.addItem(new GuiItem(new ItemBuilder(Material.BARRIER).setName("&k"), event -> event.setCancelled(true)), 0, 0);
        gui.getSecondItemComponent().addPane(pane1);

        StaticPane secondPane = new StaticPane(0, 0, 1, 1);
        secondPane.addItem(new GuiItem(new ItemBuilder(Material.NAME_TAG).setName("#<447cfc>&lNaciśnij, aby ustawić!"), event -> {
            event.setCancelled(true);
            player.closeInventory();
            String name = gui.getRenameText();

            int length = name.length();
            if (length > MAXIMAL_NAME_LENGTH) {
                player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cNazwa działki może mieć maksymalnie " + MAXIMAL_NAME_LENGTH + " znaków."));
                return;
            }

            if (length < MINIMAL_NAME_LENGTH) {
                player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cNazwa działki musi mieć przynajmniej " + MINIMAL_NAME_LENGTH + " znaków."));
                return;
            }

            if (!plot.canNameBeSet(name)) {
                player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &cNie można zmienić nazwy działki na: " + name));
                return;
            }

            plot.setName(name);
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>&l☁ &7Pomyślnie zmieniono nazwę na: #<447cfc>" + name + "&7."));
        }), 0, 0);
        gui.getResultComponent().addPane(secondPane);

        gui.update();
        gui.show(player);
    }
}
