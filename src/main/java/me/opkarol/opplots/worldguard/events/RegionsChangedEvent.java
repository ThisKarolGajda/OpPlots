package me.opkarol.opplots.worldguard.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RegionsChangedEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final UUID uuid;
    private final Set<ProtectedRegion> previousRegions = new HashSet<>();
    private final Set<ProtectedRegion> currentRegions = new HashSet<>();
    private final Set<String> previousRegionsNames = new HashSet<>();
    private final Set<String> currentRegionsNames = new HashSet<>();
    private boolean cancelled = false;

    public RegionsChangedEvent(UUID playerUUID, Set<ProtectedRegion> previous, Set<ProtectedRegion> current) {
        this.uuid = playerUUID;
        previousRegions.addAll(previous);
        currentRegions.addAll(current);

        for (ProtectedRegion region : current) {
            currentRegionsNames.add(region.getId());
        }

        for (ProtectedRegion region : previous) {
            previousRegionsNames.add(region.getId());
        }
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Set<String> getCurrentRegionsNames() {
        return currentRegionsNames;
    }

    public Set<String> getPreviousRegionsNames() {
        return previousRegionsNames;
    }

    public Set<ProtectedRegion> getCurrentRegions() {
        return currentRegions;
    }

    public Set<ProtectedRegion> getPreviousRegions() {
        return previousRegions;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}