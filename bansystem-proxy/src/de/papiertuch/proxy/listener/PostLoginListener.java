package de.papiertuch.proxy.listener;

import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.ProxiedBanPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        BanSystem.getInstance().loadBanPlayer(new ProxiedBanPlayer(player));
    }
}
