package de.papiertuch.proxy.events;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

@AllArgsConstructor
public class ProxiedPlayerKickEvent extends Event {

    private UUID operator, target;
    private String reason;
}
