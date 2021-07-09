package de.papiertuch.utils;

import de.papiertuch.utils.config.Config;
import de.papiertuch.utils.database.MongoDB;
import de.papiertuch.utils.database.MySQL;
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
import java.util.concurrent.TimeUnit;

@Getter
public class BanSystem {

    @Getter
    private static BanSystem instance;

    private String version;

    private IPlayerDataBase playerDataBase;
    private Config config, messages, blacklist;
    private UUIDFetcher uuidFetcher;
    private BanHandler banHandler;
    private MuteHandler muteHandler;
    private ReportHandler reportHandler;
    private SimpleDateFormat dateFormat;
    private Reason banBypassingReason;

    private HashMap<UUID, IBanPlayer> banPlayerHashMap;
    private HashMap<String, List<UUID>> accounts;
    private ArrayList<IBanPlayer> notify;
    private ArrayList<Reason> banReason, muteReason;

    public BanSystem(String software, String currentVersion) {
        instance = this;
        System.out.print(" ___                           _     ");
        System.out.print("(  _`\\                _       ( )    ");
        System.out.print("| |_) ) _   _   ___  (_)  ___ | |__  ");
        System.out.print("| ,__/'( ) ( )/' _ `\\| |/',__)|  _ `\\");
        System.out.print("| |    | (_) || ( ) || |\\__, \\| | | |");
        System.out.print("(_)    `\\___/'(_) (_)(_)(____/(_) (_)");
        System.out.print("                                     ");
        System.out.println("> by Papiertuch | Discord: https://papiertu.ch/go/discord/");
        System.out.println("> Software: " + software);
        System.out.println("> Pluginversion: " + currentVersion);
        System.out.println("");

        this.version = checkUpdate();

        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        this.config = new Config("config.yml");
        this.messages = new Config("messages.yml");
        this.blacklist = new Config("blacklist.yml");

        this.uuidFetcher = new UUIDFetcher();
        this.banHandler = new BanHandler();
        this.muteHandler = new MuteHandler();
        this.reportHandler = new ReportHandler();

        this.banPlayerHashMap = new HashMap<>();
        this.accounts = new HashMap<>();

        this.banReason = new ArrayList<>();
        this.muteReason = new ArrayList<>();
        this.notify = new ArrayList<>();

        switch (BanSystem.getInstance().getConfig().getString("database.type")) {
            case "MongoDB":
                this.playerDataBase = new MongoDB("playerTest");
                break;
            default:
                this.playerDataBase = new MySQL("playerTest");
                break;
        }

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

        this.banBypassingReason = new Reason(BanSystem.getInstance().getConfig().getString("settings.banBypassing.name"), 0,
                BanSystem.getInstance().getConfig().getString("settings.banBypassing.duration"), 0, false);
    }

    public IBanPlayer loadBanPlayer(IBanPlayer banPlayer) {
        this.banPlayerHashMap.put(banPlayer.getUniqueId(), banPlayer);
        return banPlayer;
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
            System.out.println("[Punish] No connection to the WebServer could be established, you will not receive update notifications");
        }
        return "null";
    }

    public String getRemainingTime(Long duration) {
        if (duration == -1) {
            return this.messages.getString("messages.timeFormat.permanent");
        }
        SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
        today.format(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1));

        SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
        future.format(duration);

        long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
        int days = (int) (time / (1000 * 60 * 60 * 24));
        int hours = (int) (time / (1000 * 60 * 60) % 24);
        int minutes = (int) (time / (1000 * 60) % 60);

        String day = this.messages.getString("messages.timeFormat.days");
        if (days == 1) {
            day = this.messages.getString("messages.timeFormat.day");
        }

        String hour = this.messages.getString("messages.timeFormat.hours");
        if (hours == 1) {
            hour = this.messages.getString("messages.timeFormat.hour");
        }

        String minute = this.messages.getString("messages.timeFormat.minutes");
        if (minutes == 1) {
            minute = this.messages.getString("messages.timeFormat.minute");
        }
        if (minutes < 1 && days == 0 && hours == 0) {
            return this.messages.getString("messages.timeFormat.lessMinute");
        }
        if (hours == 0 && days == 0) {
            return minutes + " " + minute;
        }
        if (days == 0 && minutes == 0) {
            return hours + " " + hour;
        }
        if (days == 0) {
            return hours + " " + hour + " " + minutes + " " + minute;
        }
        if (minutes == 0 && hours != 0) {
            return days + " " + day + " " + hours + " " + hour;
        }
        if (hours == 0) {
            return days + " " + day;
        }
        return days + " " + day + " " + hours + " " + hour;
    }
}
