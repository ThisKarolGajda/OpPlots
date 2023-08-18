package me.opkarol.opplots.inventories;

import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.opkarol.opc.api.item.ItemBuilder;
import org.bukkit.Material;

public class InventoryHelper {
    public static StaticPane backgroundPaneThreeRows;
    public static StaticPane backgroundPaneFourRows;
    public static StaticPane backgroundPaneFiveRows;
    public static StaticPane backgroundPaneSixRows;

    static {
        backgroundPaneThreeRows = new StaticPane(0, 0, 9, 3, Pane.Priority.LOWEST);
        backgroundPaneThreeRows.fillWith(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&k"), event -> event.setCancelled(true));

        backgroundPaneFourRows = new StaticPane(0, 0, 9, 4, Pane.Priority.LOWEST);
        backgroundPaneFourRows.fillWith(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&k"), event -> event.setCancelled(true));

        backgroundPaneFiveRows = new StaticPane(0, 0, 9, 5, Pane.Priority.LOWEST);
        backgroundPaneFiveRows.fillWith(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&k"), event -> event.setCancelled(true));

        backgroundPaneSixRows = new StaticPane(0, 0, 9, 6, Pane.Priority.LOWEST);
        backgroundPaneSixRows.fillWith(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("&k"), event -> event.setCancelled(true));
    }
}
