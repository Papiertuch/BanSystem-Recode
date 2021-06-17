package de.papiertuch.utils.player.interfaces;

import java.util.UUID;

public interface IBanPlayer {

    PlayerType getType();

    void sendMessage(String message);

    void disconnect(String string);

    String getName();

    UUID getUniqueId();

    String getDisplayName();

    String getServer();

    String getAddress();

    boolean hasPermission(String permission);

    enum PlayerType {
        CONSOLE,
        BUKKIT_PLAYER,
        PROXIED_PLAYER
    }
}


