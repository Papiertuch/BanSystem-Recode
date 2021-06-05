package de.papiertuch.utils.player;

import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class ProxiedBanPlayer implements IBanPlayer {

    @Getter
    private ProxiedPlayer player;

    public ProxiedBanPlayer(ProxiedPlayer player) {
        this.player = player;
    }

    @Override
    public PlayerType getType() {
        return PlayerType.PROXIED_PLAYER;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public void disconnect(String string) {
        player.disconnect(string);
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
        return player.getServer().getInfo().getName();
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
