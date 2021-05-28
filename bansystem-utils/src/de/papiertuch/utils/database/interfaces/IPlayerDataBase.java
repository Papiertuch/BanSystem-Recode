package de.papiertuch.utils.database.interfaces;

import java.util.UUID;
import java.util.function.Consumer;

public interface IPlayerDataBase {

    public boolean isConnected();

    public boolean isExistsPlayer(UUID uuid);

    public void createPlayer(UUID uuid);

    public void setNotify(UUID uuid, boolean value);

    public boolean isNotify(UUID uuid);

    public void isExistsPlayerAsync(UUID uuid, Consumer<Boolean> consumer);

    public void createPlayerAsync(UUID uuid);

    public void setNotifyAsync(UUID uuid, boolean value);

    public void isNotifyAsync(UUID uuid, Consumer<Boolean> consumer);
}
