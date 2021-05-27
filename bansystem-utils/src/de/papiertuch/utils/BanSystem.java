package de.papiertuch.utils;

import de.papiertuch.utils.config.Config;
import de.papiertuch.utils.database.MongoDB;
import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.database.interfaces.IPlayerDataBase;
import de.papiertuch.utils.handler.BanHandler;
import de.papiertuch.utils.handler.MuteHandler;
import de.papiertuch.utils.handler.ReportHandler;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Getter
public class BanSystem {

    @Getter
    private static BanSystem instance;

    private IDataBase muteDataBase;
    private IPlayerDataBase playerDataBase;
    private Config config, messages;
    private UUIDFetcher uuidFetcher;
    private BanHandler banHandler;
    private MuteHandler muteHandler;
    private ReportHandler reportHandler;
    private SimpleDateFormat dateFormat;

    private HashMap<UUID, IBanPlayer> banPlayerHashMap;
    private ArrayList<IBanPlayer> notify;
    private ArrayList<Reason> banReason, muteReason;

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

        this.uuidFetcher = new UUIDFetcher();
        this.banHandler = new BanHandler();
        this.muteHandler = new MuteHandler();
        this.reportHandler = new ReportHandler();

        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        //TODO CHECK DB TYPE
        this.muteDataBase = new MongoDB("muteData", "nachhilfemc.de", 27017, "test", "mongo", "fyUMRnZV5nRevsFS");
        this.playerDataBase = new MongoDB("playerData", "nachhilfemc.de", 27017, "test", "mongo", "fyUMRnZV5nRevsFS");

        this.config = new Config("config.yml");
        this.messages = new Config("messages.yml");

        this.banPlayerHashMap = new HashMap<>();

        this.banReason = new ArrayList<>();
        this.muteReason = new ArrayList<>();
        this.notify = new ArrayList<>();

        for (int i = 1; i < (config.getInt("settings.banReasons") + 1); i++) {
            this.banReason.add(new Reason(
                    config.getString("settings.banReason." + i + ".name"), i,
                    config.getString("settings.banReason." + i + ".duration"),
                    config.getBoolean("settings.banReason." + i + ".isReportReason")));
        }

        for (int i = 1; i < (config.getInt("settings.muteReasons") + 1); i++) {
            this.muteReason.add(new Reason(
                    config.getString("settings.muteReason." + i + ".name"), i,
                    config.getString("settings.muteReason." + i + ".duration"),
                    config.getBoolean("settings.muteReason." + i + ".isReportReason")));
        }
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
