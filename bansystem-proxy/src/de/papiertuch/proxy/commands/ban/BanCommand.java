package de.papiertuch.proxy.commands.ban;

import de.papiertuch.proxy.events.ban.ProxiedPlayerBanEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.Reason;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
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
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.banCommand"))) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.noPerms"));
            return;
        }
        IBanPlayer banPlayer = BanSystem.getInstance().getBanPlayer(player.getUniqueId());
        if (!BanSystem.getInstance().getNotify().contains(banPlayer)) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.notLogin"));
            return;
        }
        switch (args.length) {
            case 2:
                String name = args[0];
                String reason = args[1];
                if (name.equalsIgnoreCase(player.getName())) {
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.selfBanned"));
                    return;
                }
                if (!isExists(reason)) {
                    int i = 0;
                    for (Reason banReason : BanSystem.getInstance().getBanReason()) {
                        i++;
                        player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.banSyntaxReason")
                                .replace("%reason%", banReason.getName())
                                .replace("%id%", String.valueOf(i))
                                .replace("%duration%", banReason.getDuration().replace("-1", "Permanent")));
                    }
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.banSyntax"));
                    return;
                }
                if (BanSystem.getInstance().getBanHandler().banPlayer(banPlayer, name, reason)) {
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerBanEvent(banPlayer,
                            BanSystem.getInstance().getUuidFetcher().getUUID(name),
                            BanSystem.getInstance().getBanHandler().getReason(reason)));
                }
                break;
            default:
                int i = 0;
                for (Reason banReason : BanSystem.getInstance().getBanReason()) {
                    i++;
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.banSyntaxReason")
                            .replace("%reason%", banReason.getName())
                            .replace("%id%", String.valueOf(i))
                            .replace("%duration%", banReason.getDuration().replace("-1", "Permanent")));
                }
                player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.banSyntax"));
                break;
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
