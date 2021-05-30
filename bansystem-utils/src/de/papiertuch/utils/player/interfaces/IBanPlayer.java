package de.papiertuch.utils.player.interfaces;

import java.util.UUID;

public interface IBanPlayer {

    public void sendMessage(String message);

    public void disconnect(String string);

    public String getName();

    public UUID getUniqueId();

    public String getDisplayName();

    public String getServer();

    public String getAddress();

    public boolean hasPermission(String permission);
}
