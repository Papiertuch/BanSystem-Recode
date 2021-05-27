package de.papiertuch.proxy.listener;

import de.papiertuch.utils.BanSystem;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class LoginListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        UUID uuid = event.getConnection().getUniqueId();
        BanSystem.getInstance().getBanHandler().getDataBase().createAsync(uuid);
        BanSystem.getInstance().getMuteDataBase().createAsync(uuid);
        BanSystem.getInstance().getPlayerDataBase().createPlayerAsync(uuid);
    }
}
