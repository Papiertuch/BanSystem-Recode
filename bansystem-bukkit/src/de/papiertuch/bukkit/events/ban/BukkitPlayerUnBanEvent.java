package de.papiertuch.bukkit.events.ban;

import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@AllArgsConstructor
public class BukkitPlayerUnBanEvent extends Event {

    private IBanPlayer player;
    private UUID target;
    private HandlerList handlers;

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
