package de.papiertuch.proxy.listener;

import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.database.interfaces.IDataBase;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        IDataBase dataBase = BanSystem.getInstance().getMuteHandler().getDataBase();
        if (event.getMessage().startsWith("/")) {
            for (String command : BanSystem.getInstance().getBlacklist().getList("blacklist")) {
                if (event.getMessage().startsWith(command)) {
                    event.setCancelled(true);
                    //TODO SEND BLOCK COMMAND MESSAGE
                    return;
                }
            }
        }
        for (String command : BanSystem.getInstance().getBlacklist().getList("blacklist")) {
            if (event.getMessage().contains(command)) {
                event.setCancelled(true);
                //TODO SEND MESSAGE BLOCKED
                return;
            }
        }
        dataBase.isBannedAsync(player.getUniqueId(), muted -> {
            if (muted) {
                dataBase.getDurationAsync(player.getUniqueId(), duration -> {
                    if (duration != -1 && duration <= System.currentTimeMillis()) {
                        BanSystem.getInstance().getMuteHandler().resetBan(player.getUniqueId());
                        return;
                    }
                    event.setCancelled(true);
                    //TODO SEND MUTE NOTIFY
                    return;
                });
            }
        });
    }
}
