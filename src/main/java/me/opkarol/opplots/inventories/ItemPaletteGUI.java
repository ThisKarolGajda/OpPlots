package me.opkarol.opplots.inventories;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.*;
import me.opkarol.opc.api.gui.InventoryUtils;
import me.opkarol.opc.api.item.ItemBuilder;
import me.opkarol.opc.api.tools.HeadManager;
import me.opkarol.opc.api.utils.FormatUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class ItemPaletteGUI<M> extends ChestGui {
    private final int rows;
    private final int itemsPerPage;
    private final Predicate<M> itemFilter;
    private final Function<M, GuiItem> itemTransformer;
    private final List<M> values;
    private PaginatedPane itemsPane;
    private Consumer<OutlinePane> controlPaneConsumer;
    private List<GuiItem> displayItems;
    private Runnable onPreviousAction;

    private ItemPaletteGUI(Builder<M> builder, List<M> values, int rows, Runnable onPreviousAction) {
        super(rows, FormatUtils.formatMessage(builder.title));
        this.itemTransformer = builder.itemTransformer;
        this.onPreviousAction = onPreviousAction;
        this.itemFilter = builder.itemFilter;
        this.values = values;
        this.rows = rows;
        this.itemsPerPage = rows * 9 - 9;
        this.setOnTopClick((event) -> event.setCancelled(true));
    }

    private void build() {
        this.addPane(this.itemsPane = this.createItemsPane());
        this.addPane(InventoryUtils.createRectangle(Pane.Priority.LOWEST, 1, rows - 1, 8, 1, new GuiItem(InventoryUtils.createWall(Material.BLACK_STAINED_GLASS_PANE))));
        this.addPane(this.createControlPane());

        Pane backgroundPane =  switch (rows) {
            case 6 -> InventoryHelper.backgroundPaneSixRows;
            case 5 -> InventoryHelper.backgroundPaneFiveRows;
            case 4 -> InventoryHelper.backgroundPaneFourRows;
            case 3 -> InventoryHelper.backgroundPaneThreeRows;
            default -> null;
        };
        if (backgroundPane != null) {
            this.addPane(backgroundPane);
        }

        this.update();
    }

    public Pane createControlPane() {
        OutlinePane pane;
        if (this.controlPaneConsumer != null) {
            pane = new OutlinePane(0, rows - 1, 9, 1, Pane.Priority.HIGH);
            pane.setOrientation(Orientable.Orientation.HORIZONTAL);
            pane.addItem(ItemPaletteGUI.PageController.PREVIOUS.toItemStack(this, "&7Poprzednia strona", this.itemsPane));
            this.controlPaneConsumer.accept(pane);
            pane.addItem(ItemPaletteGUI.PageController.NEXT.toItemStack(this, "&7Następna strona", this.itemsPane));
        } else {
            if (onPreviousAction == null) {
                pane = new OutlinePane(7, rows - 1, 3, 1, Pane.Priority.HIGH);
                pane.addItem(ItemPaletteGUI.PageController.PREVIOUS.toItemStack(this, "&7Poprzednia strona", this.itemsPane));
                pane.addItem(ItemPaletteGUI.PageController.NEXT.toItemStack(this, "&7Następna strona", this.itemsPane));
            } else {
                pane = new OutlinePane(6, rows - 1, 3, 1, Pane.Priority.HIGH);
                pane.addItem(ItemPaletteGUI.PageController.PREVIOUS.toItemStack(this, "&7Poprzednia strona", this.itemsPane));
                pane.addItem(new GuiItem(new ItemBuilder(HeadManager.getHeadFromMinecraftValueUrl("8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f"))
                        .setName("&7Powrót"),
                        event -> {
                    event.setCancelled(true);
                    onPreviousAction.run();
                }));
                pane.addItem(ItemPaletteGUI.PageController.NEXT.toItemStack(this, "&7Następna strona", this.itemsPane));
            }
        }

        return pane;
    }

    public PaginatedPane createItemsPane() {
        Deque<GuiItem> itemsToDisplay = new ArrayDeque();
        if (this.displayItems != null) {
            itemsToDisplay.addAll(this.displayItems);
        }

        if (this.values != null) {
            itemsToDisplay.addAll(this.values.stream()
                    .filter(this.itemFilter != null ? this.itemFilter : (predicate) -> true)
                    .map(this.itemTransformer).collect(Collectors.toCollection(LinkedList::new)));
        }

        PaginatedPane pane = new PaginatedPane(0, 0, 9, rows, Pane.Priority.LOWEST);
        int i = 0;

        for (int pagesAmount = itemsToDisplay.size() / itemsPerPage + 1; i < pagesAmount; ++i) {
            pane.addPane(i, this.createPage(itemsToDisplay));
        }

        pane.setPage(0);
        return pane;
    }

    public Pane createPage(Deque<GuiItem> items) {
        OutlinePane page = new OutlinePane(0, 0, 9, rows, Pane.Priority.LOWEST);
        page.setOrientation(Orientable.Orientation.HORIZONTAL);

        for (int i = 1; i <= itemsPerPage; ++i) {
            if (!items.isEmpty()) {
                page.addItem(items.removeLast());
            }
        }

        return page;
    }

    public Consumer<OutlinePane> getControlPaneConsumer() {
        return this.controlPaneConsumer;
    }

    public void setControlPaneConsumer(Consumer<OutlinePane> controlPaneConsumer) {
        this.controlPaneConsumer = controlPaneConsumer;
    }

    public Function<M, GuiItem> getItemTransformer() {
        return this.itemTransformer;
    }

    public List<M> getValues() {
        return this.values;
    }

    public PaginatedPane getItemsPane() {
        return this.itemsPane;
    }

    public Predicate<M> getItemFilter() {
        return this.itemFilter;
    }

    public List<GuiItem> getDisplayItems() {
        return this.displayItems;
    }

    public void setDisplayItems(List<GuiItem> displayItems) {
        this.displayItems = displayItems;
    }

    public enum PageController {
        PREVIOUS(HeadManager.getHeadFromMinecraftValueUrl("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9"), (page, itemsPane) -> page > 0, (page) -> {
            --page;
            return page;
        }),
        NEXT(HeadManager.getHeadFromMinecraftValueUrl("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf"), (page, itemsPane) -> page < itemsPane.getPages() - 1, (page) -> {
            ++page;
            return page;
        });

        private final ItemStack skull;
        private final BiPredicate<Integer, PaginatedPane> shouldContinue;
        private final IntUnaryOperator nextPageSupplier;

        PageController(ItemStack skull, BiPredicate<Integer, PaginatedPane> shouldContinue, IntUnaryOperator nextPageSupplier) {
            this.skull = skull;
            this.shouldContinue = shouldContinue;
            this.nextPageSupplier = nextPageSupplier;
        }

        public GuiItem toItemStack(ChestGui gui, String itemName, PaginatedPane itemsPane) {
            return new GuiItem((new ItemBuilder(this.skull.clone())).setName(itemName), (event) -> {
                int currentPage = itemsPane.getPage();
                if (this.shouldContinue.test(currentPage, itemsPane)) {
                    itemsPane.setPage(this.nextPageSupplier.applyAsInt(currentPage));
                    gui.update();
                }
            });
        }
    }

    public static class Builder<M> {
        private final String title;
        private Function<M, GuiItem> itemTransformer;
        private Predicate<M> itemFilter;
        private Consumer<OutlinePane> controlPaneConsumer;
        private List<GuiItem> displayItems;

        public Builder(String title) {
            this.title = title;
        }

        public Builder<M> as(Function<M, GuiItem> itemTransformer) {
            this.itemTransformer = itemTransformer;
            return this;
        }

        public Builder<M> show(Predicate<M> itemFilter) {
            this.itemFilter = itemFilter;
            return this;
        }

        public ItemPaletteGUI<M> build(List<M> values, int rows, @Nullable Runnable onPreviousAction) {
            ItemPaletteGUI<M> paletteGUI = new ItemPaletteGUI(this, values, rows, onPreviousAction);
            paletteGUI.setControlPaneConsumer(this.controlPaneConsumer);
            paletteGUI.setDisplayItems(this.displayItems);
            paletteGUI.build();
            return paletteGUI;
        }

        public ItemPaletteGUI<M> build(List<M> values, @Nullable Runnable onPreviousAction) {
            return build(values, 6, onPreviousAction);
        }

        public ItemPaletteGUI<M> build(List<M> values, int rows) {
            return build(values, rows, null);
        }

        public ItemPaletteGUI<M> build(List<M> values) {
            return build(values, 6, null);
        }


        public ItemPaletteGUI<M> build(Supplier<List<M>> values) {
            return this.build(values.get());
        }

        public ItemPaletteGUI<M> build(M value) {
            return this.build(List.of(value));
        }

        @SafeVarargs
        public final ItemPaletteGUI<M> build(M... values) {
            return this.build(Arrays.asList(values));
        }

        public Builder<M> controlPane(Consumer<OutlinePane> controlPaneConsumer) {
            this.controlPaneConsumer = controlPaneConsumer;
            return this;
        }

        public Builder<M> displayItems(List<GuiItem> displayItems) {
            this.displayItems = displayItems;
            return this;
        }
    }
}