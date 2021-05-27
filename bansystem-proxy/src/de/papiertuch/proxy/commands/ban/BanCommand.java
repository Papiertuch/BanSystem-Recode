package de.papiertuch.proxy.commands.ban;

import de.papiertuch.proxy.events.ban.ProxiedPlayerBanEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.Reason;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BanCommand extends Command {

    public BanCommand() {
        super("ban");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage("Kein Spieler");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("system.ban")) {
            player.sendMessage("Keine Rechte");
            return;
        }
        switch (args.length) {
            case 2:
                String name = args[0];
                String reason = args[1];
                if (name.equalsIgnoreCase(player.getName())) {
                    player.sendMessage("Darfst dich nicht selber bannen");
                    return;
                }
                if (!isExists(reason)) {
                    player.sendMessage("Gibt es nicht");
                    return;
                }
                if (BanSystem.getInstance().getBanHandler().banPlayer(
                        BanSystem.getInstance().getBanPlayer(player.getUniqueId()),
                        name, reason)) {
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerBanEvent(player.getUniqueId(),
                            name, reason,
                            BanSystem.getInstance().getBanHandler().getDurationLong(reason)));
                }
                return;
        }
    }

    private boolean isExists(String string) {
        for (Reason reason : BanSystem.getInstance().getBanReason()) {
            if (reason.getName().equalsIgnoreCase(string)) {
                return true;
            }
            if (String.valueOf(reason.getId()).equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }
}
