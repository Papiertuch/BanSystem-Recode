package de.papiertuch.utils.handler;

import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.config.Config;
import de.papiertuch.utils.database.MongoDB;
import de.papiertuch.utils.database.interfaces.IDataBase;
import lombok.Getter;

import java.util.HashMap;

public class MuteHandler {

    @Getter
    private IDataBase dataBase;
    private Config config;

    public MuteHandler() {
        this.dataBase = new MongoDB("muteData", "nachhilfemc.de", 27017, "test", "mongo", "fyUMRnZV5nRevsFS");
        this.config = BanSystem.getInstance().getConfig();
    }
}
