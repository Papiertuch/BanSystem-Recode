package de.papiertuch.proxy.events.mute;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

@AllArgsConstructor
public class ProxiedPlayerMuteReduceEvent extends Event {

    private ProxiedPlayer player;
    private UUID name;
    private String reason;
    private long oldDuration, duration;
}
