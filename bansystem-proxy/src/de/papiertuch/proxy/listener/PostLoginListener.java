package de.papiertuch.proxy.listener;

import de.papiertuch.proxy.ProxyCore;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.ProxiedBanPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import java.util.concurrent.TimeUnit;

public class PostLoginListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        BanSystem.getInstance().loadBanPlayer(new ProxiedBanPlayer(player));
        ProxyServer.getInstance().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            if (player.hasPermission("bungeecord.command.list")) {
                BanSystem.getInstance().getPlayerDataBase().isNotifyAsync(player.getUniqueId(), state -> {
                    if (state) {
                        if (!BanSystem.getInstance().getNotify().contains(BanSystem.getInstance().getBanPlayer(player.getUniqueId()))) {
                            BanSystem.getInstance().getNotify().add(BanSystem.getInstance().getBanPlayer(player.getUniqueId()));
                        }
                    }
                });
            }
            //TODO LOAD NOTIFY STATES
            if (player.hasPermission("update.notify")) {
                if (!BanSystem.getInstance().getVersion().equalsIgnoreCase(ProxyCore.getInstance().getDescription().getVersion())) {
                    player.sendMessage(BanSystem.getInstance().getConfig().getString("messages.prefix") + " §aA new version is available §8» §f§l" + BanSystem.getInstance().getVersion());
                    player.sendMessage("§ehttps://www.spigotmc.org/resources/bansystem-for-bungeecord-or-bukkit-mysql.57979/");
                }
            }
        }, 1, TimeUnit.SECONDS);
    }
}
