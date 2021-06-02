package de.papiertuch.proxy.listener;

import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        IBanPlayer banPlayer = BanSystem.getInstance().getBanPlayer(event.getPlayer().getUniqueId());
        if (BanSystem.getInstance().getNotify().contains(banPlayer)) {
            BanSystem.getInstance().getNotify().remove(banPlayer);
        }
        BanSystem.getInstance().getBanPlayerHashMap().remove(event.getPlayer().getUniqueId());
    }
}
