package de.papiertuch.proxy.listener;

import de.papiertuch.utils.BanSystem;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class LoginListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        if (!BanSystem.getInstance().getPlayerDataBase().isConnected()) {
            event.setCancelReason(BanSystem.getInstance().getConfig().getString("messages.prefix") + " §cThere is no dataBase connection. Please check your dataBase data");
            event.setCancelled(true);
            return;
        }

        UUID uuid = event.getConnection().getUniqueId();
        String address = event.getConnection().getAddress().getHostString();

        BanSystem.getInstance().getBanHandler().getDataBase().createAsync(uuid);
        BanSystem.getInstance().getMuteDataBase().createAsync(uuid);
        BanSystem.getInstance().getPlayerDataBase().createPlayerAsync(uuid);

        if (BanSystem.getInstance().getConfig().getBoolean("module.antiBot.enable")) {
            if (BanSystem.getInstance().getAccounts().containsKey(address)) {
                if (BanSystem.getInstance().getAccounts().get(address).size() >= BanSystem.getInstance().getConfig().getInt("module.antiBot.maxAccounts")) {
                    event.setCancelReason("Kick Doppel Acc");
                    event.setCancelled(true);
                    return;
                }
                List<UUID> list = BanSystem.getInstance().getAccounts().containsKey(address) ? BanSystem.getInstance().getAccounts().get(address) : new ArrayList<>();
                if (!list.contains(event.getConnection().getUniqueId())) {
                    list.add(event.getConnection().getUniqueId());
                    BanSystem.getInstance().getAccounts().put(address, list);
                }
            }
            BanSystem.getInstance().getBanHandler().hasVPN(address, aBoolean -> {
                event.setCancelReason("Kick VPN");
                event.setCancelled(true);
                return;
            });
        }
    }
}
