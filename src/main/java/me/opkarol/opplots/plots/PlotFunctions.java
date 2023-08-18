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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
        player.closeInventory();
        player.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Wyświetlanie granicy działki przez 10 sekund!"));
        OpPlotsAPI.displayBorder(plot, player);
    }

    public void openSettingsInventory(Player player) {
        new SettingsInventory(plot, player);
    }

    public void openUpgradesInventory(Player player) {
        new UpgradesInventory(plot, player);
    }

    public void openManageIgnorePagedInventory(Player player) {
        new ManageIgnorePagedInventory(plot, player);
    }

    public void openManageMembersPagedInventory(Player player) {
        new ManageMembersPagedInventory(plot, player);
    }

    public void openPlotChangeNameAnvilInventory(Player player) {
        new PlotChangeNameAnvilInventory(plot, player);
    }

    public void openMainInventory(Player player) {
        new MainPlotInventory(this, player);
    }

    public void openAddExpirationInventory(Player player) {
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
        new ChoosePlotInventory(player, plot -> {
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
        }, seeAll);
    }

    public static void teleportPlayer(Player player) {
        teleportPlayer(player, false);
    }
}