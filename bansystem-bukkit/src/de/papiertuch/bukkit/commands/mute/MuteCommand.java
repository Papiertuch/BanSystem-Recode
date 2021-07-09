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

public class MuteCommand extends Command {

    public MuteCommand() {
        super("mute");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.muteCommand"))) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.noPerms"));
            return;
        }
        IBanPlayer banPlayer = BanSystem.getInstance().getBanPlayer(player.getUniqueId());
        if (!BanSystem.getInstance().getNotify().contains(banPlayer)) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.notLogin"));
            return;
        }
        if (args.length >= 2) {
            String name = args[0];
            String reason = args[1];
            if (name.equalsIgnoreCase(player.getName())) {
                player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.selfMuted"));
                return;
            }
            Reason reasonObject = BanSystem.getInstance().getMuteHandler().getReason(reason);
            if (reasonObject == null) {
                int i = 0;
                player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.reasons").replace(
                        "%amount%", String.valueOf(BanSystem.getInstance().getMuteReason().size())));
                for (Reason banReason : BanSystem.getInstance().getMuteReason()) {
                    i++;
                    player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.syntaxReason")
                            .replace("%reason%", banReason.getName())
                            .replace("%id%", String.valueOf(i))
                            .replace("%duration%", banReason.getDuration().replace("-1", "Permanent")));
                }
                player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.muteSyntax"));
                return;
            }
            if (BanSystem.getInstance().getMuteHandler().mutePlayer(banPlayer, name,
                    reasonObject.getName(), reasonObject.getDuration(),
                    args.length == 3 ? args[2] : ""))
                ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerMuteEvent(banPlayer,
                        BanSystem.getInstance().getUuidFetcher().getUUID(name),
                        reasonObject));
        } else {
            int i = 0;
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.reasons").replace(
                    "%amount%", String.valueOf(BanSystem.getInstance().getMuteReason().size())));
            for (Reason banReason : BanSystem.getInstance().getMuteReason()) {
                i++;
                player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.syntaxReason")
                        .replace("%reason%", banReason.getName())
                        .replace("%id%", String.valueOf(i))
                        .replace("%duration%", banReason.getDuration().replace("-1", "Permanent")));
            }
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.muteSyntax"));
        }
    }
}
