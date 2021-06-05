package de.papiertuch.proxy.events;

import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

@AllArgsConstructor
public class ProxiedPlayerKickEvent extends Event {

    private IBanPlayer player, target;
    private String reason;
}
