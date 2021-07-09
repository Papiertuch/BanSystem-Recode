package de.papiertuch.bukkit.events;

import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@AllArgsConstructor
public class BukkitPlayerLoginNotifyEvent extends Event {

    private UUID uuid;
    private boolean state;
    private HandlerList handlers;

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
