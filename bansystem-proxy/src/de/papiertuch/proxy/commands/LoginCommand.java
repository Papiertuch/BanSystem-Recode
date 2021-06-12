package de.papiertuch.proxy.commands;

import de.papiertuch.proxy.events.ProxiedPlayerLoginNotifyEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LoginCommand extends Command {

    public LoginCommand() {
        super("login");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.loginCommand"))) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.noPerms"));
            return;
        }
        IBanPlayer banPlayer = BanSystem.getInstance().getBanPlayer(player.getUniqueId());
        if (BanSystem.getInstance().getNotify().contains(banPlayer)) {
            BanSystem.getInstance().getNotify().remove(banPlayer);
            BanSystem.getInstance().getPlayerDataBase().setNotifyAsync(banPlayer.getUniqueId(), false);
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerLoginNotifyEvent(player.getUniqueId(), false));
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.logout"));
            return;
        }
        BanSystem.getInstance().getNotify().add(banPlayer);
        BanSystem.getInstance().getPlayerDataBase().setNotifyAsync(banPlayer.getUniqueId(), true);
        ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerLoginNotifyEvent(player.getUniqueId(), false));
        player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.login"));
    }
}
