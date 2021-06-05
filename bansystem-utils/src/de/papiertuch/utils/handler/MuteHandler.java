package de.papiertuch.utils.handler;

import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.config.Config;
import de.papiertuch.utils.database.MongoDB;
import de.papiertuch.utils.database.MySQL;
import de.papiertuch.utils.database.interfaces.IDataBase;
import lombok.Getter;
import java.util.UUID;

public class MuteHandler {

    @Getter
    private IDataBase dataBase;
    private Config config;

    public MuteHandler() {
        switch (BanSystem.getInstance().getConfig().getString("database.type")) {
            case "MongoDB":
                this.dataBase = new MongoDB("muteTest");
                break;
            default:
                this.dataBase = new MySQL("muteTest");
                break;
        }
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
