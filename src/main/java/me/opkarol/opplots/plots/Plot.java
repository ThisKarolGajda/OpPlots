package me.opkarol.opplots.plots;

import me.opkarol.opc.api.tools.location.OpSerializableLocation;
import me.opkarol.opplots.OpPlots;
import me.opkarol.opplots.plots.identifier.RegionIdentifierFactory;
import me.opkarol.opplots.plots.name.PlotNameFactory;
import me.opkarol.opplots.plots.settings.PlotSettings;
import me.opkarol.opplots.plots.upgrades.PlotUpgrades;
import me.opkarol.opplots.utils.DateTimeConverter;
import me.opkarol.opplots.utils.ExpirationConverter;
import me.opkarol.opplots.worldguard.WorldGuardBorder;
import me.opkarol.opplots.worldguard.WorldGuardUsers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.opkarol.opplots.plots.listener.PlotOutsideBorderListener.getParticle;
import static me.opkarol.opplots.webhook.DiscordWebhooks.*;

public class Plot {
    public static final String PLOTS_WORLD_STRING = OpPlots.getInstance().getFilesManager().getConfigFile().getConfiguration().get("plot.world");
    public static final World PLOTS_WORLD = Bukkit.getWorld(PLOTS_WORLD_STRING);
    private final UUID owner;
    private final String creationDate;
    private final List<UUID> members;
    private final List<UUID> ignored;
    private final PlotUpgrades upgrades;
    private final PlotSettings settings;
    private String name;
    private long expiration;
    private OpSerializableLocation homeLocation;

    public Plot(UUID owner, String creationDate, String name, List<UUID> members, List<UUID> ignored, PlotUpgrades upgrades, PlotSettings settings, long expiration, OpSerializableLocation homeLocation) {
        this.owner = owner;
        this.creationDate = creationDate;
        this.name = name;
        this.members = members;
        this.ignored = ignored;
        this.upgrades = upgrades;
        this.settings = settings;
        this.expiration = expiration;
        this.homeLocation = homeLocation;
    }

    public void displayBorder(WorldGuardBorder.IPlayerParticle particle, Player viewer, int seconds) {
        WorldGuardBorder.spawnBorderParticles(viewer, getRegionIdentifier(), particle, seconds);
    }

    public void displayBorder(WorldGuardBorder.IPlayerParticle particle, Player viewer) {
        displayBorder(particle, viewer, 1);
    }

    public void displayBorder(Player viewer, int seconds) {
        displayBorder(getParticle(this, viewer), viewer, seconds);
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    public boolean isOwner(OfflinePlayer player) {
        return isOwner(player.getUniqueId());
    }

    public boolean isAdded(UUID uuid) {
        return isOwner(uuid) || isMember(uuid);
    }

    public boolean isAdded(OfflinePlayer player) {
        return isAdded(player.getUniqueId());
    }

    /*
     * Regions
     */

    public String getRegionIdentifier() {
        return RegionIdentifierFactory.createRegionIdentifier(this);
    }

    public String getSupportRegionIdentifier() {
        return RegionIdentifierFactory.createSupportRegionIdentifier(this);
    }

    public String getSafeAreaRegionIdentifier() {
        return RegionIdentifierFactory.createSafeAreaRegionIdentifier(this);
    }

    /*
     * Name
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String previousName = this.name + "";
        this.name = name;
        sendPlotChangedNameWebhook(this, previousName, false);
        updateToDatabase();
    }

    public void resetName() {
        setName(PlotNameFactory.createPlotName(this));
    }

    public boolean canSetName(UUID uuid) {
        return isOwner(uuid);
    }

    public boolean canSetName(OfflinePlayer player) {
        return isOwner(player);
    }

    public boolean canNameBeSet(String name) {
        return !name.toLowerCase().contains("thisopkarol");
    }

    /*
     * Members
     */

    public void addMember(UUID uuid) {
        WorldGuardUsers.addMember(getRegionIdentifier(), uuid);
        List<UUID> previousMembers = members.stream().toList();
        members.add(uuid);
        sendPlotAddedMemberWebhook(this, previousMembers, uuid);
        updateToDatabase();
    }

    public void addMember(OfflinePlayer player) {
        addMember(player.getUniqueId());
    }

    public boolean canAddMember(UUID uuid) {
        return isOwner(uuid);
    }

    public boolean canAddMember(OfflinePlayer player) {
        return canAddMember(player.getUniqueId());
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public boolean isMember(OfflinePlayer player) {
        return isMember(player.getUniqueId());
    }

    public void removeMember(UUID uuid) {
        WorldGuardUsers.removeMember(getRegionIdentifier(), uuid);
        List<UUID> previousMembers = members.stream().toList();
        members.remove(uuid);
        sendPlotRemovedMemberWebhook(this, previousMembers, uuid);
        updateToDatabase();
    }

    public String getMembersNames() {
        return getMembers().stream()
                .map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName())
                .collect(Collectors.joining(", "));
    }

    public void removeMember(OfflinePlayer player) {
        removeMember(player.getUniqueId());
    }

    /*
     * Ignored
     */

    public void addIgnored(UUID uuid) {
        List<UUID> previousIgnored = ignored.stream().toList();
        ignored.add(uuid);
        sendPlotChangedIgnoredWebhook(this, previousIgnored);
        updateToDatabase();
    }

    public void addIgnored(OfflinePlayer player) {
        addIgnored(player.getUniqueId());
    }

    public boolean canAddIgnored(UUID uuid) {
        return isOwner(uuid);
    }

    public boolean canAddIgnored(OfflinePlayer player) {
        return canAddIgnored(player.getUniqueId());
    }

    public boolean isIgnored(UUID uuid) {
        return ignored.contains(uuid);
    }

    public boolean isIgnored(OfflinePlayer player) {
        return isIgnored(player.getUniqueId());
    }

    public void removeIgnored(UUID uuid) {
        List<UUID> previousIgnored = ignored.stream().toList();
        ignored.remove(uuid);
        sendPlotChangedIgnoredWebhook(this, previousIgnored);
        updateToDatabase();
    }

    public String getIgnoredNames() {
        return getIgnored().stream()
                .map(uuid1 -> Bukkit.getOfflinePlayer(uuid1).getName())
                .collect(Collectors.joining(", "));
    }

    public void removeIgnored(OfflinePlayer player) {
        removeIgnored(player.getUniqueId());
    }

    /*
     * Expiration
     */

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
        updateToDatabase();
    }

    public void addExpiration(long expiration) {
        setExpiration(getExpiration() + expiration);
    }

    public String getExpirationLeftString() {
        return ExpirationConverter.getTimeLeftString(getExpiration());
    }

    /*
     * Home
     */

    public OpSerializableLocation getHomeLocation() {
        return homeLocation;
    }

    public Location getBukkitHomeLocation() {
        return getHomeLocation().getLocation();
    }

    public void setHomeLocation(OpSerializableLocation homeLocation) {
        String previousHomeLocation = homeLocation.toFamilyString();
        this.homeLocation = homeLocation;
        sendPlotChangedHomeWebhook(this, previousHomeLocation);
        updateToDatabase();
    }

    public String getFamilyHomeLocation() {
        OpSerializableLocation location = getHomeLocation();
        return "X:" + Math.round(location.getX()) + " Y:" + Math.round(location.getY()) + " Z:" + Math.round(location.getZ());
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

    public PlotSettings getSettings() {
        return settings;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getFormattedCreationDate() {
        return DateTimeConverter.convertToReadableFormat(creationDate);
    }

    public UUID getOwnerUUID() {
        return owner;
    }

    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(getOwnerUUID());
    }

    public String getOwnerName() {
        return getOwner().getName();
    }

    public void updateToDatabase() {
        OpPlots.getInstance().getDatabaseManager().getDatabaseLoader().getDatabaseHandler().insertData(this);
    }

    @Override
    public String toString() {
        return "Plot{" +
                "owner=" + owner +
                ", creationDate='" + creationDate + '\'' +
                ", members=" + members +
                ", ignored=" + ignored +
                ", upgrades=" + upgrades +
                ", settings=" + settings +
                ", name='" + name + '\'' +
                '}';
    }
}
