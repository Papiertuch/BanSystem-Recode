package de.papiertuch.utils.player;

import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitBanPlayer implements IBanPlayer {

    @Getter
    private Player player;

    public BukkitBanPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String getDisplayName() {
        return player.getDisplayName();
    }

    @Override
    public String getServer() {
        return player.getWorld().getName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}
