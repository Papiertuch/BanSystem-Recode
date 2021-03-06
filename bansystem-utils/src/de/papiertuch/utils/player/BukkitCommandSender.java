package de.papiertuch.utils.player;

import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class BukkitCommandSender implements IBanPlayer {

    @Getter
    private CommandSender commandSender;

    public BukkitCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Override
    public PlayerType getType() {
        return PlayerType.CONSOLE;
    }

    @Override
    public void sendMessage(String message) {
        commandSender.sendMessage(message);
    }

    @Override
    public void disconnect(String string) {

    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public UUID getUniqueId() {
        return UUID.fromString("0-0-0-0-0");
    }

    @Override
    public String getDisplayName() {
        return "§6CONSOLE";
    }

    @Override
    public String getServer() {
        return null;
    }

    @Override
    public String getAddress() {
        return "127.0.0.1";
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }
}
