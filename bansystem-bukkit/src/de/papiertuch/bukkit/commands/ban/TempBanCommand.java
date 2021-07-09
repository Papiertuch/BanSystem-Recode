package de.papiertuch.proxy.commands.ban;

import de.papiertuch.proxy.events.ban.ProxiedPlayerBanEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.Reason;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TempBanCommand extends Command {

    public TempBanCommand() {
        super("tempBan");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.tempBanCommand"))) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.noPerms"));
            return;
        }
        IBanPlayer banPlayer = BanSystem.getInstance().getBanPlayer(player.getUniqueId());
        if (!BanSystem.getInstance().getNotify().contains(banPlayer)) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.notLogin"));
            return;
        }
        String name;
        String reason;
        String duration;
        switch (args.length) {
            case 3:
                name = args[0];
                reason = args[1];
                duration = args[2];
                if (name.equalsIgnoreCase(player.getName())) {
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.selfBanned"));
                    return;
                }
                if (duration.equalsIgnoreCase("-1")) {
                    if (BanSystem.getInstance().getBanHandler().banPlayer(banPlayer, name, reason, duration)) {
                        ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerBanEvent(banPlayer,
                                BanSystem.getInstance().getUuidFetcher().getUUID(name),
                                new Reason(reason, 0, duration, 0, false)));
                        return;
                    }
                } else {
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.tempBanSyntax"));
                    return;
                }
                break;
            case 4:
                name = args[0];
                reason = args[1];
                duration = args[2];
                String format = args[3];
                if (name.equalsIgnoreCase(player.getName())) {
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.selfBanned"));
                    return;
                }
                switch (format) {
                    case "m":
                    case "h":
                    case "d":
                        if (BanSystem.getInstance().getBanHandler().banPlayer(banPlayer, name, reason, duration + " " + format)) {
                            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerBanEvent(banPlayer,
                                    BanSystem.getInstance().getUuidFetcher().getUUID(name),
                                    new Reason(reason, 0, duration + " " + format, 0, false)));
                        }
                        break;
                    default:
                        player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.tempBanSyntax"));
                        break;
                }
                break;
            default:
                player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.tempBanSyntax"));
                break;
        }
    }
}
