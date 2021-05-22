package de.papiertuch.proxy.events.ban;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

@AllArgsConstructor
public class ProxiedPlayerBanReduceEvent extends Event {

    private UUID operator;
    private String name, reason;
    private long oldDuration, duration;
}
