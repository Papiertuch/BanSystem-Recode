package de.papiertuch.utils.handler;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.Reason;
import de.papiertuch.utils.config.Config;
import de.papiertuch.utils.database.MongoDB;
import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class BanHandler {

    @Getter
    private IDataBase dataBase;
    private Config config;
    private HashMap<String, Boolean> cache;

    public BanHandler() {
        this.dataBase = new MongoDB("banData", "nachhilfemc.de", 27017, "test", "mongo", "fyUMRnZV5nRevsFS");
        this.config = BanSystem.getInstance().getConfig();
        this.cache = new HashMap<>();
    }

    public void hasVPN(String address, Consumer<Boolean> consumer) {
        try {
            if (this.cache.containsKey(address)) {
                consumer.accept(this.cache.get(address));
            }
            int block;
            OkHttpClient caller = new OkHttpClient();
            Request request = new Request.Builder().url("http://v2.api.iphub.info/ip/" + address)
                    .addHeader("X-Key", BanSystem.getInstance().getConfig().getString("module.antiBot.vpnKey")).build();
            try {
                Response response = caller.newCall(request).execute();
                JSONObject json = new JSONObject(response.body().string());
                block = (int) json.get("block");
                if (block == 1) {
                    consumer.accept(this.cache.put(address, true));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            consumer.accept(this.cache.put(address, false));
        } catch (JSONException e) {
            ProxyServer.getInstance().getConsole().sendMessage("[BanSystem] §cNo VPN key was found...");
        }
        consumer.accept(false);
    }

    public boolean banPlayer(IBanPlayer banPlayer, String name, String reason) {
        return banPlayer(banPlayer, name, reason, getDurationLong(reason));
    }

    public boolean banPlayer(IBanPlayer banPlayer, String name, String reason, long duration) {
        UUID uuid = BanSystem.getInstance().getUuidFetcher().getUUID(name);
        if (banPlayer.getName().equalsIgnoreCase(name)) {
            banPlayer.sendMessage("§cDu kannst dich nicht bannen");
            return false;
        }
        dataBase.create(uuid);
        if (dataBase.isBanned(uuid)) {
            banPlayer.sendMessage("§cDieser Spieler ist bereits gebannt");
            return false;
        }
        if (!banPlayer.hasPermission("system.opBan")) {
            if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v2")) {
                OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(uuid);
                if (offlinePlayer != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName())) {
                        banPlayer.sendMessage("§cDen Spieler kannst du nicht bannen");
                        return false;
                    }
                }
            } else if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v3")) {
                IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);
                if (permissionUser != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUser).getName())) {
                        banPlayer.sendMessage("§cDen Spieler kannst du nicht bannen");
                        return false;
                    }
                }
            } else if (BanSystem.getInstance().getBanPlayer(uuid) != null) {
                if (BanSystem.getInstance().getBanPlayer(uuid).hasPermission("system.bypass")) {
                    banPlayer.sendMessage("§cDen Spieler kannst du nicht bannen");
                    return false;
                }
            }
        }
        long banTime;
        if (duration != -1) {
            banTime = duration + System.currentTimeMillis();
        }
        if (dataBase.getBanPoints(uuid) >= 100) {
            banTime = -1;
        }
        IBanPlayer target = BanSystem.getInstance().getBanPlayer(uuid);
        String display = target == null ? "§7" + name : target.getDisplayName();
        for (IBanPlayer teamPlayers : BanSystem.getInstance().getNotify()) {
            if (teamPlayers != null) {
                teamPlayers.sendMessage(display + " §7wurde von " + banPlayer.getDisplayName() + " §7gebannt");
                teamPlayers.sendMessage("§7Grund: §e" + getExactReason(reason) + " §7| Dauer: §e" + getDuration(reason));
            }
        }
        return true;
    }

    public long getDurationLong(String string) {
        String format = "";
        long duration = 0;
        long time = 0;
        for (Reason reason : BanSystem.getInstance().getBanReason()) {
            format = reason.getDuration();
            if (reason.getName().equalsIgnoreCase(string)) {
                if (reason.getDuration().equalsIgnoreCase("-1")) {
                    duration = -1;
                    break;
                }
                duration = Long.parseLong(format.split(" ")[0]);
                break;
            }
            if (String.valueOf(reason.getId()).equalsIgnoreCase(string)) {
                if (reason.getDuration().equalsIgnoreCase("-1")) {
                    duration = -1;
                    break;
                }
                duration = Long.parseLong(format.split(" ")[0]);
                break;
            }
        }
        if (format.contains("s")) {
            time = duration * 1000;
        } else if (format.contains("m")) {
            time = duration * 1000 * 60;
        } else if (format.contains("h")) {
            time = duration * 1000 * 60 * 60;
        } else if (format.contains("d")) {
            time = duration * 1000 * 60 * 60 * 24;
        }
        return time;
    }

    public String getExactReason(String string) {
        for (Reason reason : BanSystem.getInstance().getBanReason()) {
            if (reason.getName().equalsIgnoreCase(string)) {
                return reason.getName();
            }
            if (String.valueOf(reason.getId()).equalsIgnoreCase(string)) {
                return reason.getName();
            }
        }
        return "null";
    }

    public String getDuration(String string) {
        for (Reason reason : BanSystem.getInstance().getBanReason()) {
            if (reason.getName().equalsIgnoreCase(string)) {
                return reason.getDuration();
            }
            if (String.valueOf(reason.getId()).equalsIgnoreCase(string)) {
                return reason.getDuration();
            }
        }
        return "null";
    }
}
