package de.papiertuch.bukkit.events.report;

import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@AllArgsConstructor
public class BukkitPlayerReportAcceptEvent extends Event {

    private UUID operator, target;
    private String reason;
    private HandlerList handlers;

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
