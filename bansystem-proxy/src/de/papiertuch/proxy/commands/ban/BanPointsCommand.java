package de.papiertuch.proxy.commands.ban;

import de.papiertuch.utils.BanSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BanPointsCommand extends Command {

    public BanPointsCommand() {
        super("banPoints", null, "bp");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        BanSystem.getInstance().getBanHandler().getDataBase().getBanPointsAsync(player.getUniqueId(), points -> {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.banPoints")
            .replace("%points%", String.valueOf(points)));
        });
    }
}
