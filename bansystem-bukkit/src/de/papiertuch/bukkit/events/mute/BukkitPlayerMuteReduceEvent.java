package de.papiertuch.bukkit.events.mute;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@AllArgsConstructor
public class BukkitPlayerMuteReduceEvent extends Event {

    private Player player;
    private UUID target;
    private HandlerList handlers;

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
