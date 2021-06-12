package de.papiertuch.proxy.commands.mute;

import de.papiertuch.proxy.events.ban.ProxiedPlayerBanEvent;
import de.papiertuch.proxy.events.mute.ProxiedPlayerMuteEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.Reason;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TempMuteCommand extends Command {

    public TempMuteCommand() {
        super("tempMute");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.tempMuteCommand"))) {
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
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.selfMuted"));
                    return;
                }
                if (duration.equalsIgnoreCase("-1")) {
                    if (BanSystem.getInstance().getMuteHandler().mutePlayer(banPlayer, name, reason, duration)) {
                        ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerMuteEvent(banPlayer,
                                BanSystem.getInstance().getUuidFetcher().getUUID(name),
                                new Reason(reason, 0, duration, 0, false)));
                        return;
                    }
                } else {
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.tempMuteSyntax"));
                    return;
                }
                break;
            case 4:
                name = args[0];
                reason = args[1];
                duration = args[2];
                String format = args[3];
                if (name.equalsIgnoreCase(player.getName())) {
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.selfMuted"));
                    return;
                }
                switch (format) {
                    case "m":
                    case "h":
                    case "d":
                        if (BanSystem.getInstance().getMuteHandler().mutePlayer(banPlayer, name, reason, duration + " " + format)) {
                            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerMuteEvent(banPlayer,
                                    BanSystem.getInstance().getUuidFetcher().getUUID(name),
                                    new Reason(reason, 0, duration + " " + format, 0, false)));
                        }
                        break;
                    default:
                        player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.tempMuteSyntax"));
                        break;
                }
                break;
            default:
                player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.tempMuteSyntax"));
                break;
        }
    }
}
