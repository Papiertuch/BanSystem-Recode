package de.papiertuch.proxy.events.mute;

import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

@AllArgsConstructor
public class ProxiedPlayerUnMuteEvent extends Event {

    private IBanPlayer player;
    private UUID target;
}
