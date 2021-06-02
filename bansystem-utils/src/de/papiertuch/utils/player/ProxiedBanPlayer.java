package de.papiertuch.utils.player;

import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.util.UUID;

public class ProxiedBanPlayer implements IBanPlayer {

    @Getter
    private ProxiedPlayer player;

    public ProxiedBanPlayer(ProxiedPlayer player) {
        this.player = player;
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
        System.out.println(player.getName() + " -> hasPermission -> " + permission + " | " + player.hasPermission(permission));
        return player.hasPermission(permission);
    }
}
