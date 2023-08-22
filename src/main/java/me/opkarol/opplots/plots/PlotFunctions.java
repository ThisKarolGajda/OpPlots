package me.opkarol.opplots.plots;

import me.opkarol.opc.api.tools.location.OpSerializableLocation;
import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.OpPlotsAPI;
import me.opkarol.opplots.inventories.expiration.PlotAddExpirationInventory;
import me.opkarol.opplots.inventories.ignored.ManageIgnorePagedInventory;
import me.opkarol.opplots.inventories.members.ManageMembersPagedInventory;
import me.opkarol.opplots.inventories.plot.ChoosePlotInventory;
import me.opkarol.opplots.inventories.plot.MainPlotInventory;
import me.opkarol.opplots.inventories.plot.PlotChangeNameAnvilInventory;
import me.opkarol.opplots.inventories.settings.SettingsInventory;
import me.opkarol.opplots.inventories.upgrades.UpgradesInventory;
import me.opkarol.opplots.utils.StringIconUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static me.opkarol.opplots.plots.Plot.PLOTS_WORLD;

public record PlotFunctions(Plot plot) {

    public static void getPlotFromLocationOrOwner(Location location, Player owner, Consumer<PlotFunctions> action) {
        Optional<Plot> optional = OpPlotsAPI.getPlotFromLocation(location);
        if (optional.isPresent()) {
            action.accept(new PlotFunctions(optional.get()));
            return;
        }

        optional = OpPlotsAPI.getOwnerPlot(owner);
        if (optional.isPresent()) {
            action.accept(new PlotFunctions(optional.get()));
            return;
        }

        owner.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cNie znaleziono działki na której można wykonać tą operację!"));
    }

    public static void getPlotFromLocationOrOwner(Player owner, Consumer<PlotFunctions> action) {
        getPlotFromLocationOrOwner(owner.getLocation(), owner, action);
    }

    public void displayBorder(Player player) {
        if (!isFeatureAvailable(player, PLOT_FEATURE.SHOW_BORDERS)) {
            player.sendMessage(PLOT_FEATURE.getTooLowPermission(PLOT_FEATURE.SHOW_BORDERS));
            return;
        }

        player.closeInventory();
        if (!player.getLocation().getWorld().equals(PLOTS_WORLD)) {
            player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cNa tym świecie nie możesz wyświetlać granicy!"));
            return;
        }

        player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Wyświetlanie granicy działki przez 10 sekund!"));
        OpPlotsAPI.displayBorder(plot, player);
    }

    public void openSettingsInventory(Player player) {
        if (!isFeatureAvailable(player, PLOT_FEATURE.MANAGE_SETTINGS)) {
            player.sendMessage(PLOT_FEATURE.getTooLowPermission(PLOT_FEATURE.MANAGE_SETTINGS));
            return;
        }
        new SettingsInventory(plot, player);
    }

    public void openUpgradesInventory(Player player) {
        if (!isFeatureAvailable(player, PLOT_FEATURE.MANAGE_UPGRADES)) {
            player.sendMessage(PLOT_FEATURE.getTooLowPermission(PLOT_FEATURE.MANAGE_UPGRADES));
            return;
        }
        new UpgradesInventory(plot, player);
    }

    public void openManageIgnorePagedInventory(Player player) {
        if (!isFeatureAvailable(player, PLOT_FEATURE.MANAGE_IGNORED)) {
            player.sendMessage(PLOT_FEATURE.getTooLowPermission(PLOT_FEATURE.MANAGE_IGNORED));
            return;
        }
        new ManageIgnorePagedInventory(plot, player);
    }

    public void openManageMembersPagedInventory(Player player) {
        if (!isFeatureAvailable(player, PLOT_FEATURE.MANAGE_MEMBERS)) {
            player.sendMessage(PLOT_FEATURE.getTooLowPermission(PLOT_FEATURE.MANAGE_MEMBERS));
            return;
        }
        new ManageMembersPagedInventory(plot, player);
    }

    public void openPlotChangeNameAnvilInventory(Player player) {
        if (!isFeatureAvailable(player, PLOT_FEATURE.CHANGE_NAME)) {
            player.sendMessage(PLOT_FEATURE.getTooLowPermission(PLOT_FEATURE.CHANGE_NAME));
            return;
        }
        new PlotChangeNameAnvilInventory(plot, player);
    }

    public void openMainInventory(Player player) {
        new MainPlotInventory(this, player);
    }

    public void openAddExpirationInventory(Player player) {
        if (!isFeatureAvailable(player, PLOT_FEATURE.MANAGE_EXPIRATION)) {
            player.sendMessage(PLOT_FEATURE.getTooLowPermission(PLOT_FEATURE.MANAGE_EXPIRATION));
            return;
        }
        new PlotAddExpirationInventory(plot, player);
    }

    public boolean isOwner(Player player) {
        return plot.isOwner(player);
    }

    public void setHome(Player player) {
        plot.setHomeLocation(new OpSerializableLocation(player.getLocation()));
    }

    public boolean isAdded(Player player) {
        return plot.isAdded(player);
    }

    public static void teleportPlayer(Player player, boolean seeAll) {
        new ChoosePlotInventory(player, plot -> teleportPlayer(player, plot), seeAll);
    }

    public static void teleportPlayer(Player player) {
        teleportPlayer(player, false);
    }

    public static void teleportPlayer(Player player, PlotFunctions plot) {
        if (!PLOT_FEATURE.TELEPORT_HOME.isAvailable(player, plot.plot())) {
            player.sendMessage(PLOT_FEATURE.getTooLowPermission(PLOT_FEATURE.TELEPORT_HOME));
            return;
        }

        Location location = plot.plot().getBukkitHomeLocation();
        CompletableFuture<Boolean> teleportFuture = new CompletableFuture<>();

        OpPlots.getInstance()
                .getPluginManager()
                .getEssentials()
                .getUser(player)
                .getAsyncTeleport().teleport(location, null, PlayerTeleportEvent.TeleportCause.PLUGIN, teleportFuture);

        teleportFuture.thenAcceptAsync(success -> {
            if (success) {
                player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Pomyślnie przeteleportowano!"));
            } else {
                player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cNie udało się przeteleportować!"));
            }
        });
    }

    public boolean isFeatureAvailable(Player player, PLOT_FEATURE feature) {
        return feature.isAvailable(player, plot);
    }

    public String getAvailableFeature(Player player, PLOT_FEATURE teleportHome) {
        return StringIconUtil.getReturnedEmojiFromBoolean(isFeatureAvailable(player, teleportHome));
    }

    public enum PLOT_FEATURE {
        TELEPORT_HOME(1),
        SHOW_BORDERS(0),
        MANAGE_EXPIRATION(1),
        MANAGE_SETTINGS(2),
        MANAGE_UPGRADES(2),
        MANAGE_IGNORED(2),
        MANAGE_MEMBERS(2),
        CHANGE_NAME(2),
        ;

        private final int permissionLevel;

        /*
         * 0 - EVERYONE+
         * 1 - MEMBERS+
         * 2 - OWNER+
         */
        PLOT_FEATURE(int permissionLevel) {
            this.permissionLevel = permissionLevel;
        }

        public int getPermissionLevel() {
            return permissionLevel;
        }

        public boolean isAvailable(Player player, Plot plot) {
            int playerPermissionLevel = getPlayerPermissionLevel(player, plot);
            return getPermissionLevel() <= playerPermissionLevel;
        }

        private int getPlayerPermissionLevel(Player player, Plot plot) {
            int playerPermissionLevel;
            if (plot.isOwner(player)) {
                playerPermissionLevel = 2;
            } else if (plot.isMember(player)) {
                playerPermissionLevel = 1;
            } else {
                playerPermissionLevel = 0;
            }
            return playerPermissionLevel;
        }

        public static String getTooLowPermission(PLOT_FEATURE feature) {
            return switch (feature.permissionLevel) {
                case 0 -> FormatUtils.formatMessage("#<447cfc>☁ &cMusisz być co najmniej GRACZEM, żeby móc to wykonać!");
                case 1 -> FormatUtils.formatMessage("#<447cfc>☁ &cMusisz być co najmniej CZŁONKIEM działki, żeby móc to wykonać!");
                case 2 -> FormatUtils.formatMessage("#<447cfc>☁ &cMusisz być co najmniej WŁAŚCICIELEM działki, żeby móc to wykonać!");
                default -> throw new IllegalStateException("Unexpected value: " + feature.permissionLevel);
            };
        }
    }
}