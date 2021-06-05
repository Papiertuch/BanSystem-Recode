package de.papiertuch.proxy.events.ban;

import de.papiertuch.utils.Reason;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;


@AllArgsConstructor
public class ProxiedPlayerBanEvent extends Event {

    private IBanPlayer player;
    private UUID target;
    private Reason reason;

}
