package de.papiertuch.bukkit.commands;

import de.papiertuch.bukkit.events.BukkitPlayerKickEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class KickCommand extends Command {

    public KickCommand() {
        super("kick");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return true;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.kickCommand"))) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.noPerms"));
            return true;
        }
        IBanPlayer banPlayer = BanSystem.getInstance().getBanPlayer(player.getUniqueId());
        if (!BanSystem.getInstance().getNotify().contains(banPlayer)) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.notLogin"));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.kickSyntax"));
            return true;
        }
        String reason = "";
        int i = 1;
        while (i < args.length) {
            reason = reason + args[i] + " ";
            ++i;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.isOffline"));
            return true;
        }
        IBanPlayer targetBanPlayer = BanSystem.getInstance().getBanPlayer(target.getUniqueId());
        if (BanSystem.getInstance().getBanHandler().kickPlayer(banPlayer, targetBanPlayer, reason)) {
            Bukkit.getPluginManager().callEvent(new BukkitPlayerKickEvent(banPlayer,
                    targetBanPlayer, reason, new HandlerList()));
        }
        return false;
    }
}
