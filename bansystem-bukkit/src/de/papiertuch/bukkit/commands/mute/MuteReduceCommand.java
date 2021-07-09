package de.papiertuch.proxy.commands.mute;

import de.papiertuch.proxy.events.ban.ProxiedPlayerBanReduceEvent;
import de.papiertuch.proxy.events.mute.ProxiedPlayerMuteReduceEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MuteReduceCommand extends Command {

    public MuteReduceCommand() {
        super("mutereduce");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.muteReduceCommand"))) {
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
                if (BanSystem.getInstance().getMuteHandler().reduceMute(banPlayer, name,
                        BanSystem.getInstance().getConfig().getInt("settings.reduceMute"))) {
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerMuteReduceEvent(
                            player, BanSystem.getInstance().getUuidFetcher().getUUID(name)));
                }
                break;
            default:
                player.sendMessage(BanSystem.getInstance().getConfig().getString("permissions.reduceMuteSyntax"));
                break;
        }
    }
}
