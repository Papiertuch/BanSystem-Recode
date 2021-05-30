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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
public class BanSystem {

    @Getter
    private static BanSystem instance;

    private String version;

    private IDataBase muteDataBase;
    private IPlayerDataBase playerDataBase;
    private Config config, messages, blacklist;
    private UUIDFetcher uuidFetcher;
    private BanHandler banHandler;
    private MuteHandler muteHandler;
    private ReportHandler reportHandler;
    private SimpleDateFormat dateFormat;

    private HashMap<UUID, IBanPlayer> banPlayerHashMap;
    private HashMap<String, List<UUID>> accounts;
    private ArrayList<IBanPlayer> notify;
    private ArrayList<Reason> banReason, muteReason;

    public BanSystem(String software, String currentVersion) {
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
        System.out.println("> by Papiertuch | Discord: https://papiertu.ch/go/discord/");
        System.out.println("> Software: " + software);
        System.out.println("> Pluginversion: " + currentVersion);

        this.version = checkUpdate();

        if (!this.version.equalsIgnoreCase(currentVersion)) {
            System.out.println("[BanSystem] A new version is available: " + this.version);
            System.out.println("[BanSystem] Download: https://www.spigotmc.org/resources/bansystem-for-bungeecord-or-bukkit-mysql.57979/");
        }

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
        this.blacklist = new Config("blacklist.yml");

        this.banPlayerHashMap = new HashMap<>();
        this.accounts = new HashMap<>();

        this.banReason = new ArrayList<>();
        this.muteReason = new ArrayList<>();
        this.notify = new ArrayList<>();

        for (int i = 1; i < (config.getInt("settings.banReasons") + 1); i++) {
            this.banReason.add(new Reason(
                    config.getString("settings.banReason." + i + ".name"), i,
                    config.getString("settings.banReason." + i + ".duration"),
                    config.getInt("settings.banReason." + i + ".points"),
                    config.getBoolean("settings.banReason." + i + ".isReportReason")));
        }

        for (int i = 1; i < (config.getInt("settings.muteReasons") + 1); i++) {
            this.muteReason.add(new Reason(
                    config.getString("settings.muteReason." + i + ".name"), i,
                    config.getString("settings.muteReason." + i + ".duration"),
                    config.getInt("settings.muteReason." + i + ".points"),
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

    public String checkUpdate() {
        try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 57979).openStream(); Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext()) {
              return scanner.next();
            }
        } catch (IOException exception) {
            System.out.println("[BanSystem] No connection to the WebServer could be established, you will not receive update notifications");
        }
        return "null";
    }
}
