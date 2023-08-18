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

public class RegionsEnteredEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final UUID uuid;
    private final Set<ProtectedRegion> regions;
    private final Set<String> regionsNames;
    private boolean cancelled = false;

    public RegionsEnteredEvent(UUID playerUUID, Set<ProtectedRegion> regions) {
        this.uuid = playerUUID;
        this.regionsNames = new HashSet<>();
        this.regions = new HashSet<>();

        if (regions != null) {
            this.regions.addAll(regions);
            for (ProtectedRegion region : regions) {
                this.regionsNames.add(region.getId());
            }
        }
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Set<ProtectedRegion> getRegions() {
        return regions;
    }

    public Set<String> getRegionsNames() {
        return regionsNames;
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