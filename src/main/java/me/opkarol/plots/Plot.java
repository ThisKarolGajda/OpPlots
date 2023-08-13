package me.opkarol.plots;

import me.opkarol.plots.identifier.RegionIdentifierFactory;
import me.opkarol.plots.name.PlotNameFactory;
import me.opkarol.plots.upgrades.PlotUpgrades;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Plot {
    private final UUID owner;
    private final String creationDate;
    private String name;
    private List<UUID> members;
    private List<UUID> ignored;
    private PlotUpgrades upgrades;

    public Plot(UUID owner, String creationDate) {
        this.owner = owner;
        this.creationDate = creationDate;
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    public boolean isOwner(Player player) {
        return isOwner(player.getUniqueId());
    }

    public String getRegionIdentifier() {
        return RegionIdentifierFactory.createRegionIdentifier(this);
    }

    /*
     * Name
     */

    public void resetName() {
        setName(PlotNameFactory.createPlotName(this));
    }

    public boolean canSetName(UUID uuid) {
        return isOwner(uuid);
    }

    public boolean canSetName(Player player) {
        return isOwner(player);
    }

    public boolean canNameBeSet(String name) {
        //TODO check if name is good - doesn't contain any slurs etc.
        return true;
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    /*
     * Members
     */

    public void addMember(Player player) {
        addMember(player.getUniqueId());
    }

    public boolean canAddMember(UUID uuid) {
        return isOwner(uuid);
    }

    public boolean canAddMember(Player player) {
        return canAddMember(player.getUniqueId());
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public boolean isMember(Player player) {
        return isMember(player.getUniqueId());
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public void removeMember(Player player) {
        removeMember(player.getUniqueId());
    }

    public void addIgnored(UUID uuid) {
        ignored.add(uuid);
    }

    /*
     * Ignored
     */

    public void addIgnored(Player player) {
        addIgnored(player.getUniqueId());
    }

    public boolean canAddIgnored(UUID uuid) {
        return isOwner(uuid);
    }

    public boolean canAddIgnored(Player player) {
        return canAddIgnored(player.getUniqueId());
    }

    public boolean isIgnored(UUID uuid) {
        return ignored.contains(uuid);
    }

    public boolean isIgnored(Player player) {
        return isIgnored(player.getUniqueId());
    }

    public void removeIgnored(UUID uuid) {
        ignored.remove(uuid);
    }

    public void removeIgnored(Player player) {
        removeIgnored(player.getUniqueId());
    }

    public String getName() {
        return name;
    }


    /*
     * Getters
     */

    public void setName(String name) {
        this.name = name;
    }

    public List<UUID> getIgnored() {
        return ignored;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public PlotUpgrades getUpgrades() {
        return upgrades;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return Bukkit.getOfflinePlayer(getOwner()).getName();
    }
}
