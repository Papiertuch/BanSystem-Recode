package de.papiertuch.utils;

import de.papiertuch.utils.database.MongoDB;
import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.database.interfaces.IPlayerDataBase;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

public class BanSystem {

    @Getter
    private static BanSystem instance;
    @Getter
    private IDataBase banDataBase, muteDataBase;
    @Getter
    private IPlayerDataBase playerDataBase;
    @Getter
    private HashMap<UUID, IBanPlayer> banPlayerHashMap;

    public BanSystem(String string, String version) {
        instance = this;
        System.out.print(" ____               _____           _                 ");
        System.out.print("|  _ \\             / ____|         | |                ");
        System.out.print("| |_) | __ _ _ __ | (___  _   _ ___| |_ ___ _ __ ___  ");
        System.out.print("|  _ < / _` | '_ \\ \\___ \\| | | / __| __/ _ \\ '_ ` _ \\ ");
        System.out.print("| |_) | (_| | | | |____) | |_| \\__ \\ ||  __/ | | | | |");
        System.out.print("|____/ \\__,_|_| |_|_____/ \\__, |___/\\__\\___|_| |_| |_|");
        System.out.print("                           __/ |                      ");
        System.out.print("                          |___/                       ");
        System.out.print("                                                       ");
        System.out.println("> " + string + " | Discord: https://papiertu.ch/go/discord/");
        System.out.println("> Pluginversion: " + version);


        //TODO CHECK DB TYPE
        this.banDataBase = new MongoDB("banData", "nachhilfemc.de", 27017, "test", "mongo", "fyUMRnZV5nRevsFS");
        this.muteDataBase = new MongoDB("muteData", "nachhilfemc.de", 27017, "test", "mongo", "fyUMRnZV5nRevsFS");
        this.playerDataBase = new MongoDB("playerData", "nachhilfemc.de", 27017, "test", "mongo", "fyUMRnZV5nRevsFS");

        this.banPlayerHashMap = new HashMap<>();


    }

    public void loadBanPlayer(IBanPlayer banPlayer) {
        this.banPlayerHashMap.put(banPlayer.getUniqueId(), banPlayer);
    }

    public IBanPlayer getBanPlayer(UUID uuid) {
        if (this.banPlayerHashMap.containsKey(uuid)) {
            return this.banPlayerHashMap.get(uuid);
        }
        return null;
    }
}
