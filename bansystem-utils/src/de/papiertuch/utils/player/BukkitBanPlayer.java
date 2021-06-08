package de.papiertuch.utils.player;

import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitBanPlayer implements IBanPlayer {

    @Getter
    private Player player;

    public BukkitBanPlayer(Player player) {
        this.player = player;
        BanSystem.getInstance().getUuidFetcher().getCache().put(player.getName(), player.getUniqueId());
    }

    @Override
    public PlayerType getType() {
        return PlayerType.BUKKIT_PLAYER;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public void disconnect(String string) {
        player.kickPlayer(string);
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
    public String getAddress() {
        return player.getAddress().getHostString();
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}
