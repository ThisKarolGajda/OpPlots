package me.opkarol.opplots.worldguard.events;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface IChangeEvent {
    Player getPlayer();

    String getRegionName();

    UUID getUUID();
}
