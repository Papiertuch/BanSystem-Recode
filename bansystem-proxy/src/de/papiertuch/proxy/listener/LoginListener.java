package de.papiertuch.proxy.listener;

import de.papiertuch.proxy.ProxyCore;
import de.papiertuch.proxy.events.ban.ProxiedPlayerBanEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.Reason;
import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.player.ProxiedCommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class LoginListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        event.registerIntent(ProxyCore.getInstance());

        Executors.newCachedThreadPool().execute(() -> {

            if (!BanSystem.getInstance().getPlayerDataBase().isConnected()) {
                event.setCancelReason(BanSystem.getInstance().getConfig().getString("messages.prefix") + " §cThere is no dataBase connection. Please check your dataBase data");
                event.setCancelled(true);
            }

            UUID uuid = event.getConnection().getUniqueId();
            String address = event.getConnection().getAddress().getHostString();

            BanSystem.getInstance().getBanHandler().getDataBase().create(uuid);
            BanSystem.getInstance().getMuteHandler().getDataBase().create(uuid);
            BanSystem.getInstance().getPlayerDataBase().createPlayer(uuid);

            BanSystem.getInstance().getBanHandler().getDataBase().setAddress(uuid, address);

            if (BanSystem.getInstance().getConfig().getBoolean("module.antiBot.enable")) {
                if (BanSystem.getInstance().getAccounts().containsKey(address)) {
                    if (BanSystem.getInstance().getAccounts().get(address).size() >= BanSystem.getInstance().getConfig().getInt("module.antiBot.maxAccounts")) {
                        event.setCancelReason(BanSystem.getInstance().getMessages().getString("messages.kickAntiBot")
                                .replace("%amount%", String.valueOf(BanSystem.getInstance().getAccounts().get(address).size())));
                        event.setCancelled(true);
                    }
                    List<UUID> list = BanSystem.getInstance().getAccounts().containsKey(address) ? BanSystem.getInstance().getAccounts().get(address) : new ArrayList<>();
                    if (!list.contains(event.getConnection().getUniqueId())) {
                        list.add(event.getConnection().getUniqueId());
                        BanSystem.getInstance().getAccounts().put(address, list);
                    }
                }
                if (BanSystem.getInstance().getBanHandler().hasVPN(address)) {
                    event.setCancelReason(BanSystem.getInstance().getMessages().getString("messages.kickVpn")
                            .replace("%address%", address));
                    event.setCancelled(true);
                }
            }
            IDataBase dataBase = BanSystem.getInstance().getBanHandler().getDataBase();
            if (dataBase.isBanned(uuid)) {
                long duration = dataBase.getDuration(uuid);
                if (duration != -1 && duration <= System.currentTimeMillis()) {
                    BanSystem.getInstance().getBanHandler().resetBan(uuid);
                } else {
                    event.setCancelReason(BanSystem.getInstance().getBanHandler().getBanScreen(dataBase.getReason(uuid), dataBase.getDuration(uuid)));
                    event.setCancelled(true);
                }
            } else if (dataBase.isIpBanned(address)) {
                Reason reason = ProxyCore.getInstance().getBanBypassingReason();
                if (BanSystem.getInstance().getBanHandler().banPlayer(new ProxiedCommandSender(ProxyServer.getInstance().getConsole()), event.getConnection().getName(), reason.getName(), reason.getDuration())) {
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerBanEvent(ProxyCore.getInstance().getConsolePlayer(),
                            uuid, reason));
                }
                event.setCancelReason(BanSystem.getInstance().getBanHandler().getBanScreen(dataBase.getReason(uuid), dataBase.getDuration(uuid)));
                event.setCancelled(true);
            }
            event.completeIntent(ProxyCore.getInstance());
        });
    }
}
