package de.papiertuch.utils.player.interfaces;

import java.util.UUID;

public interface IBanPlayer {

    public void sendMessage(String message);

    public String getName();

    public UUID getUniqueId();

    public String getDisplayName();

    public String getServer();

    public boolean hasPermission(String permission);
}
