package de.papiertuch.utils.database.interfaces;

import java.util.UUID;
import java.util.function.Consumer;

public interface IPlayerDataBase {

    boolean isConnected();

    boolean isExistsPlayer(UUID uuid);

    void createPlayer(UUID uuid);

    void setNotify(UUID uuid, boolean value);

    boolean isNotify(UUID uuid);

    void isExistsPlayerAsync(UUID uuid, Consumer<Boolean> consumer);

    void createPlayerAsync(UUID uuid);

    void setNotifyAsync(UUID uuid, boolean value);

    void isNotifyAsync(UUID uuid, Consumer<Boolean> consumer);
}
