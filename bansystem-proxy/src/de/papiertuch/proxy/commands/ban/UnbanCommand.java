package de.papiertuch.proxy.commands.ban;

import de.papiertuch.proxy.events.ban.ProxiedPlayerUnBanEvent;
import de.papiertuch.utils.BanSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        super("unban");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage("Kein Spieler");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (!player.hasPermission("bungeecord.command.list")) {
            player.sendMessage("Keine Rechte");
            return;
        }
        switch (args.length) {
            case 1:
                String name = args[0];
                if (BanSystem.getInstance().getBanHandler().unbanPlayer(BanSystem.getInstance().getBanPlayer(player.getUniqueId()), name)) {
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerUnBanEvent(BanSystem.getInstance().getBanPlayer(player.getUniqueId()), BanSystem.getInstance().getUuidFetcher().getUUID(name)));
                }
                break;
            default:
                player.sendMessage("SYNTAX");
                break;
        }
    }
}
