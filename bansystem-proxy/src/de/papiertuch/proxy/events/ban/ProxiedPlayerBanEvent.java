package de.papiertuch.proxy.events.ban;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;


@AllArgsConstructor
public class ProxiedPlayerBanEvent extends Event {

    private UUID operator;
    private String name;
    private String reason;
    private long duration;

}
