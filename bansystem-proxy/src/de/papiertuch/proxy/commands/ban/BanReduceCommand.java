package de.papiertuch.proxy.commands.ban;

import de.papiertuch.proxy.events.ban.ProxiedPlayerBanReduceEvent;
import de.papiertuch.proxy.events.ban.ProxiedPlayerUnBanEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BanReduceCommand extends Command {

    public BanReduceCommand() {
        super("banReduce");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.reduceCommand"))) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.noPerms"));
            return;
        }
        IBanPlayer banPlayer = BanSystem.getInstance().getBanPlayer(player.getUniqueId());
        if (!BanSystem.getInstance().getNotify().contains(banPlayer)) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.notLogin"));
            return;
        }
        switch (args.length) {
            case 1:
                String name = args[0];
                if (BanSystem.getInstance().getBanHandler().unbanPlayer(banPlayer, name)) {
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerBanReduceEvent(
                            player, BanSystem.getInstance().getUuidFetcher().getUUID(name)));
                }
                break;
            default:
                player.sendMessage(BanSystem.getInstance().getConfig().getString("permissions.reduceSyntax"));
                break;
        }
    }
}
