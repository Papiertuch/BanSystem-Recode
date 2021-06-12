package de.papiertuch.proxy.commands.mute;

import de.papiertuch.proxy.events.ban.ProxiedPlayerUnBanEvent;
import de.papiertuch.proxy.events.mute.ProxiedPlayerUnMuteEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnmuteCommand extends Command {

    public UnmuteCommand() {
        super("unmute");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.unmuteCommand"))) {
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
                if (BanSystem.getInstance().getMuteHandler().unmutePlayer(banPlayer, name)) {
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerUnMuteEvent(banPlayer, BanSystem.getInstance().getUuidFetcher().getUUID(name)));
                }
                break;
            default:
                player.sendMessage(BanSystem.getInstance().getMessages().getString("permissions.unmuteSyntax"));
                break;
        }
    }
}
