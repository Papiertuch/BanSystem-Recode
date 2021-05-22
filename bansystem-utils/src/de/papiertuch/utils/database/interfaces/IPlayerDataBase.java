package de.papiertuch.utils.database.interfaces;

import java.util.UUID;

public interface IPlayerDataBase {

    public boolean isExitsPlayer(UUID uuid);

    public void createPlayer(UUID uuid);

    public void setNotify(UUID uuid, boolean value);

    public boolean isNotify(UUID uuid);

    public boolean isExitsPlayerAsync(UUID uuid);

    public void createPlayerAsync(UUID uuid);

    public void setNotifyAsync(UUID uuid, boolean value);

    public boolean isNotifyAsync(UUID uuid);
}
