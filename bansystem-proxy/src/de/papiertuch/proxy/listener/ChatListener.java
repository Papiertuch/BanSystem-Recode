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
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.blockCommand"));
                    return;
                }
            }
        }
        for (String command : BanSystem.getInstance().getBlacklist().getList("blacklist")) {
            if (event.getMessage().contains(command)) {
                event.setCancelled(true);
                player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.blockMessage"));
                return;
            }
        }
        if (dataBase.isBanned(player.getUniqueId())) {
            long duration = dataBase.getDuration(player.getUniqueId());
            if (duration != -1 && duration <= System.currentTimeMillis()) {
                BanSystem.getInstance().getMuteHandler().resetBan(player.getUniqueId());
                return;
            }
            event.setCancelled(true);
            player.sendMessage(BanSystem.getInstance().getMessages().getListAsString("messages.screen.mute")
                    .replace("%duration%", BanSystem.getInstance().getRemainingTime(dataBase.getDuration(player.getUniqueId())))
                    .replace("%reason%", dataBase.getReason(player.getUniqueId())));
            return;
        }
    }
}
