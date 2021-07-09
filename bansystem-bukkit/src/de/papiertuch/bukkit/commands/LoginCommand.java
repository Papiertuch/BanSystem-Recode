package de.papiertuch.bukkit.commands;

import de.papiertuch.bukkit.events.BukkitPlayerLoginNotifyEvent;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class LoginCommand extends Command {

    public LoginCommand() {
        super("login");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(BanSystem.getInstance().getMessages().getString("messages.console"));
            return true;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission(BanSystem.getInstance().getConfig().getString("permissions.loginCommand"))) {
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.noPerms"));
            return true;
        }
        IBanPlayer banPlayer = BanSystem.getInstance().getBanPlayer(player.getUniqueId());
        if (BanSystem.getInstance().getNotify().contains(banPlayer)) {
            BanSystem.getInstance().getNotify().remove(banPlayer);
            BanSystem.getInstance().getPlayerDataBase().setNotifyAsync(banPlayer.getUniqueId(), false);
            Bukkit.getPluginManager().callEvent(new BukkitPlayerLoginNotifyEvent(player.getUniqueId(), false, new HandlerList()));
            player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.logout"));
            return true;
        }
        BanSystem.getInstance().getNotify().add(banPlayer);
        BanSystem.getInstance().getPlayerDataBase().setNotifyAsync(banPlayer.getUniqueId(), true);
        Bukkit.getPluginManager().callEvent(new BukkitPlayerLoginNotifyEvent(player.getUniqueId(), true,
                new HandlerList()));
        player.sendMessage(BanSystem.getInstance().getMessages().getString("messages.login"));
        return false;
    }
}
