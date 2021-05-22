package de.papiertuch.utils;

import de.papiertuch.utils.database.MongoDB;
import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.database.interfaces.IPlayerDataBase;
import lombok.Getter;

public class BanSystem {

    @Getter
    private static BanSystem instance;
    @Getter
    private IDataBase banDataBase, muteDataBase;
    @Getter
    private IPlayerDataBase playerDataBase;

    public BanSystem() {
        instance = this;

        //TODO CHECK DB TYPE
        this.banDataBase = new MongoDB("ban", "localhost", 27701, "admin", "papiertuch", "1234");
        this.muteDataBase = new MongoDB("ban", "localhost", 27701, "admin", "papiertuch", "1234");
        this.playerDataBase = new MongoDB("ban", "localhost", 27701, "admin", "papiertuch", "1234");
    }
}
