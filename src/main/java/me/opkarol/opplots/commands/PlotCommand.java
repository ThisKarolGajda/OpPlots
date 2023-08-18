package me.opkarol.opplots.commands;

import me.opkarol.opc.api.utils.FormatUtils;
import me.opkarol.opplots.OpPlotsAPI;
import me.opkarol.opplots.inventories.plot.ChoosePlotInventory;
import me.opkarol.opplots.inventories.plot.CreatePlotInventory;
import me.opkarol.opplots.inventories.plot.PlotRemoveAnvilInventory;
import me.opkarol.opplots.inventories.wiki.PlotWikiInventory;
import me.opkarol.opplots.plots.Plot;
import me.opkarol.opplots.plots.PlotFunctions;
import me.opkarol.opplots.plots.PlotRemover;
import me.opkarol.opplots.plots.permissions.PlayerPermissions;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Optional;
import java.util.UUID;

import static me.opkarol.opplots.plots.PlotFunctions.getPlotFromLocationOrOwner;
import static me.opkarol.opplots.plots.PlotFunctions.teleportPlayer;

@Command("dzialka")
public class PlotCommand {

    @DefaultFor("dzialka")
    @Cooldown(1)
    public void mainCommand(Player sender) {
        Optional<Plot> optional = OpPlotsAPI.getPlotFromLocation(sender.getLocation());
        if (optional.isPresent()) {
            PlotFunctions plot = new PlotFunctions(optional.get());
            if (plot.isAdded(sender)) {
                plot.openMainInventory(sender);
                return;
            }
        }

        if (OpPlotsAPI.isAdded(sender)) {
            new ChoosePlotInventory(sender, plot -> plot.openMainInventory(sender));
            return;
        }

        new CreatePlotInventory(sender);
    }

    @Subcommand("usun")
    @Cooldown(5)
    public void deleteCommand(Player sender) {
        Optional<Plot> optional = OpPlotsAPI.getOwnerPlot(sender);
        if (optional.isPresent()) {
            new PlotRemoveAnvilInventory(sender, optional.get());
        } else {
            sender.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cNie jesteś właścicielem żadnej działki!"));
        }
    }

    @Subcommand("wiki")
    @Cooldown(1)
    public void wikiCommand(Player sender) {
        new PlotWikiInventory(sender);
    }

    @Subcommand("granica")
    @Cooldown(10)
    public void borderCommand(Player sender) {
        getPlotFromLocationOrOwner(sender, plot -> plot.displayBorder(sender));
    }

    @Subcommand("ustawienia")
    @Cooldown(3)
    public void settingsCommand(Player sender) {
        getPlotFromLocationOrOwner(sender, plot -> plot.openSettingsInventory(sender));
    }

    @Subcommand("ulepszenia")
    @Cooldown(3)
    public void upgradesCommand(Player sender) {
        getPlotFromLocationOrOwner(sender, plot -> plot.openUpgradesInventory(sender));
    }

    @Subcommand("czlonkowie")
    @Cooldown(5)
    public void membersCommand(Player sender) {
        getPlotFromLocationOrOwner(sender, plot -> plot.openManageMembersPagedInventory(sender));
    }

    @Subcommand("ignorowani")
    @Cooldown(5)
    public void ignoreCommand(Player sender) {
        getPlotFromLocationOrOwner(sender, plot -> plot.openManageIgnorePagedInventory(sender));
    }

    @Subcommand("zmiennazwe")
    @Cooldown(10)
    public void changeNameCommand(Player sender) {
        getPlotFromLocationOrOwner(sender, plot -> plot.openPlotChangeNameAnvilInventory(sender));
    }

    @Subcommand("wygasniecie")
    @Cooldown(3)
    public void addExpirationCommand(Player sender) {
        getPlotFromLocationOrOwner(sender, plot -> plot.openAddExpirationInventory(sender));
    }

    @Subcommand("ustawdom")
    @Cooldown(15)
    public void setHomeCommand(Player sender) {
        Optional<Plot> optional = OpPlotsAPI.getPlotFromLocation(sender.getLocation());
        if (optional.isPresent()) {
            PlotFunctions plot = new PlotFunctions(optional.get());
            if (plot.isOwner(sender)) {
                plot.setHome(sender);
                sender.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &7Pomyślnie ustawiono dom dla lokalizacji " + plot.plot().getFamilyHomeLocation() + "!"));
                return;
            }

            sender.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cNie jesteś właścicielem tej działki!"));
        } else {
            sender.sendMessage(FormatUtils.formatMessage("#<447cfc>☁ &cNie stoisz na żadnej działce!"));
        }
    }

    @Subcommand("dom")
    @Cooldown(3)
    public void teleportHomeCommand(Player sender) {
        teleportPlayer(sender);
    }

    @Subcommand("admin usun-uuid-dzialki")
    @CommandPermission(PlayerPermissions.ADMIN)
    public void adminRemovePlotFromUUIDCommand(Player admin, UUID plotUUID) {
        PlotRemover.removePlotFromOwner(plotUUID);
        admin.sendMessage("Panie szanowny adminie, zobacz kanał logi czy działka została usunięta poprawnie, jeśli nie to usuń ją sam!");
    }

    @Subcommand("admin usun")
    @CommandPermission(PlayerPermissions.ADMIN)
    public void adminRemovePlotCommand(Player admin) {
        new ChoosePlotInventory(admin, plot -> PlotRemover.removePlot(plot.plot()));
        admin.sendMessage("Panie szanowny adminie, zobacz kanał logi czy działka została usunięta poprawnie, jeśli nie to usuń ją sam!");
    }

    @Subcommand("admin tp")
    @CommandPermission(PlayerPermissions.ADMIN)
    public void adminTeleportPlotCommand(Player admin) {
        teleportPlayer(admin);
    }
}
