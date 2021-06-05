package de.papiertuch.proxy.commands;

import de.papiertuch.proxy.events.ProxiedPlayerKickEvent;
import de.papiertuch.proxy.events.ban.ProxiedPlayerUnBanEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class KickCommand extends Command {

    public KickCommand() {
        super("kick");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.kickCommand"))) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.noPerms"));
            return;
        }
        IBanPlayer banPlayer = BanSystem.getInstance().getBanPlayer(player.getUniqueId());
        if (!BanSystem.getInstance().getNotify().contains(banPlayer)) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.notLogin"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.kickSyntax"));
            return;
        }
        String reason = "";
        int i = 1;
        while (i < args.length) {
            reason = reason + args[i] + " ";
            ++i;
        }
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target == null) {
            banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.isOffline"));
            return;
        }
        IBanPlayer targetBanPlayer = BanSystem.getInstance().getBanPlayer(target.getUniqueId());
        if (BanSystem.getInstance().getBanHandler().kickPlayer(banPlayer, targetBanPlayer, reason)) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerKickEvent(banPlayer, targetBanPlayer, reason));
        }
    }
}
