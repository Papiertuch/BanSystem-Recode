package de.papiertuch.bukkit.events;

import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class BukkitPlayerKickEvent extends Event {

    private IBanPlayer player, target;
    private String reason;
    private HandlerList handlers;

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
