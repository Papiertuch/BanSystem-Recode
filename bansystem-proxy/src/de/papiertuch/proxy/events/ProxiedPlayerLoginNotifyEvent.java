package de.papiertuch.proxy.events;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

@AllArgsConstructor
public class ProxiedPlayerLoginNotifyEvent extends Event {

    private UUID uuid;
    private boolean state;
}
