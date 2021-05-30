package de.papiertuch.utils.handler;

import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.config.Config;
import de.papiertuch.utils.database.MongoDB;
import de.papiertuch.utils.database.interfaces.IDataBase;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

public class MuteHandler {

    @Getter
    private IDataBase dataBase;
    private Config config;

    public MuteHandler() {
        this.dataBase = new MongoDB("muteData", "nachhilfemc.de", 27017, "test", "mongo", "fyUMRnZV5nRevsFS");
        this.config = BanSystem.getInstance().getConfig();
    }

    public void resetBan(UUID uuid) {
        dataBase.setBannedAsync(uuid, false);
        dataBase.setDurationAsync(uuid, 0);
        dataBase.setReasonAsync(uuid, "");
        dataBase.setOperatorAsync(uuid, "");
        dataBase.setDateAsync(uuid, "");
        dataBase.setBanInfoAsync(uuid, "");
    }
}
