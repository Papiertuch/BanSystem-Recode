package de.papiertuch.utils.handler;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.Reason;
import de.papiertuch.utils.config.Config;
import de.papiertuch.utils.database.MongoDB;
import de.papiertuch.utils.database.MySQL;
import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.player.interfaces.IBanPlayer;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class BanHandler {

    @Getter
    private IDataBase dataBase;
    private Config config;
    private HashMap<String, Boolean> cache;

    public BanHandler() {
        System.out.println(BanSystem.getInstance().getConfig().getString("database.type"));
        switch (BanSystem.getInstance().getConfig().getString("database.type")) {
            case "MongoDB":
                this.dataBase = new MongoDB("banTest");
                break;
            case "File":
                break;
            default:
                this.dataBase = new MySQL("banTest");
                break;
        }
        this.config = BanSystem.getInstance().getConfig();
        this.cache = new HashMap<>();
        System.out.println("finish");
    }

    public boolean hasVPN(String address) {
        try {
            if (address.equalsIgnoreCase("127.0.0.1")) return false;
            if (this.cache.containsKey(address)) return this.cache.get(address);
            int block;
            OkHttpClient caller = new OkHttpClient();
            Request request = new Request.Builder().url("http://v2.api.iphub.info/ip/" + address)
                    .addHeader("X-Key", BanSystem.getInstance().getConfig().getString("module.antiBot.vpnKey")).build();
            try {
                Response response = caller.newCall(request).execute();
                JSONObject json = new JSONObject(response.body().string());
                block = (int) json.get("block");
                if (block == 1) {
                    this.cache.put(address, true);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.cache.put(address, true);
            return false;
        } catch (JSONException e) {
            System.out.println("[BanSystem] No VPN key was found...");
        }
        return false;
    }

    public boolean banPlayer(IBanPlayer banPlayer, String name, String reason) {
        return banPlayer(banPlayer, name, reason, getReason(reason).getDuration());
    }

    public boolean banPlayer(IBanPlayer banPlayer, String name, String reason, String duration, String... info) {
        if (banPlayer.getName().equalsIgnoreCase(name)) {
            banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.selfBanned"));
            return false;
        }
        UUID uuid = BanSystem.getInstance().getUuidFetcher().getUUID(name);
        dataBase.create(uuid);
        if (dataBase.isBanned(uuid)) {
            banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.isBanned"));
            return false;
        }

        IBanPlayer target = BanSystem.getInstance().getBanPlayer(uuid);

        if (!banPlayer.hasPermission(BanSystem.getInstance().getConfig().getString("permission.banEveryone"))) {
            if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v2")) {
                OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(uuid);
                if (offlinePlayer != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName())) {
                        banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.cannotBanned"));
                        return false;
                    }
                }
            } else if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v3")) {
                IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);
                if (permissionUser != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUser).getName())) {
                        banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.cannotBanned"));
                        return false;
                    }
                }
            } else if (target != null && target.hasPermission(BanSystem.getInstance().getConfig().getString("permission.banBypass"))) {
                System.out.println(target.getName());
                banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.cannotBanned"));
                return false;
            }
        }
        long banTime = -1;
        if (!duration.equalsIgnoreCase("-1")) {
            banTime = getDurationLong(reason) + System.currentTimeMillis();
        }
        if (dataBase.getBanPoints(uuid) >= 100) {
            banTime = -1;
        }

        Reason reasonObject = getReason(reason);
        if (reasonObject == null) {
            reasonObject = new Reason(reason, 0, duration, 10, false);
        }

        String display = target == null ? name : target.getDisplayName();

        for (IBanPlayer teamPlayers : BanSystem.getInstance().getNotify()) {
            if (teamPlayers != null) {
                teamPlayers.sendMessage(BanSystem.getInstance().getMessages().getListAsString("messages.notify.ban")
                        .replace("%reason%", reasonObject.getName())
                        .replace("%duration%", BanSystem.getInstance().getRemainingTime(banTime))
                        .replace("%target%", display)
                        .replace("%player%", banPlayer.getDisplayName()));
            }
        }
        dataBase.addHistoryAsync(uuid, reasonObject.getName(), banPlayer.getName());
        if (info.length == 1) {
            dataBase.setBanInfoAsync(uuid, info[0]);
            dataBase.editLastHistoryAsync(uuid, "banInfo", info[0]);
        }
        dataBase.setDurationAsync(uuid, banTime);
        dataBase.addBanPointsAsync(uuid, reasonObject.getPoints());
        dataBase.setReason(uuid, reasonObject.getName());
        dataBase.setBannedAsync(uuid, true);
        dataBase.setDateAsync(uuid, BanSystem.getInstance().getDateFormat().format(new Date()));
        dataBase.setOperatorAsync(uuid, banPlayer.getName());

        if (target != null) {
            dataBase.setAddressAsync(uuid, target.getAddress());
            target.disconnect(getBanScreen(reasonObject.getName(), banTime));
        }

        return true;
    }

    public boolean kickPlayer(IBanPlayer banPlayer, IBanPlayer target, String reason) {
        if (target == null) {
            banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.isOffline"));
            return false;
        }
        if (!banPlayer.hasPermission(BanSystem.getInstance().getConfig().getString("permission.banEveryone"))) {
            if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v2")) {
                OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(target.getUniqueId());
                if (offlinePlayer != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName())) {
                        banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.cannotBanned"));
                        return false;
                    }
                }
            } else if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v3")) {
                IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(target.getUniqueId());
                if (permissionUser != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUser).getName())) {
                        banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.cannotBanned"));
                        return false;
                    }
                }
            } else if (target != null && target.hasPermission(BanSystem.getInstance().getConfig().getString("permission.banBypass"))) {
                System.out.println(target.getName());
                banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.cannotBanned"));
                return false;
            }
        }
        for (IBanPlayer teamPlayers : BanSystem.getInstance().getNotify()) {
            if (teamPlayers != null) {
                teamPlayers.sendMessage(BanSystem.getInstance().getMessages().getListAsString("messages.notify.kick")
                        .replace("%target%", target.getDisplayName())
                        .replace("%reason%", reason)
                        .replace("%player%", banPlayer.getDisplayName()));
            }
        }
        target.disconnect(BanSystem.getInstance().getMessages().getListAsString("messages.screen.kick")
                .replace("%reason%", reason)
                .replace("%operator%", banPlayer.getName()));
        return true;
    }

    public boolean unbanPlayer(IBanPlayer banPlayer, String name) {
        UUID uuid = BanSystem.getInstance().getUuidFetcher().getUUID(name);
        if (banPlayer.getName().equalsIgnoreCase(name)) {
            banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.selfUnBanned"));
            return false;
        }
        if (dataBase.isExists(uuid) && !dataBase.isBanned(uuid)) {
            banPlayer.sendMessage(BanSystem.getInstance().getMessages().getString("messages.notBanned"));
            return false;
        }

        IBanPlayer target = BanSystem.getInstance().getBanPlayer(uuid);
        String display = target == null ? name : target.getDisplayName();

        for (IBanPlayer teamPlayers : BanSystem.getInstance().getNotify()) {
            if (teamPlayers != null) {
                teamPlayers.sendMessage(BanSystem.getInstance().getMessages().getListAsString("messages.notify.unban")
                        .replace("%target%", display)
                        .replace("%player%", banPlayer.getDisplayName()));
            }
        }

        dataBase.removeBanPointsAsync(uuid, getReason(dataBase.getReason(uuid)).getPoints());
        dataBase.setBannedAsync(uuid, false);
        dataBase.setDurationAsync(uuid, 0);
        dataBase.setReasonAsync(uuid, "");
        dataBase.setOperatorAsync(uuid, "");
        dataBase.setDateAsync(uuid, "");
        dataBase.setBanInfoAsync(uuid, "");
        dataBase.editLastHistoryAsync(uuid, "unban", banPlayer.getName() + "-" + BanSystem.getInstance().getDateFormat().format(new Date()));
        return true;
    }

    public void resetBan(UUID uuid) {
        dataBase.setBannedAsync(uuid, false);
        dataBase.setDurationAsync(uuid, 0);
        dataBase.setReasonAsync(uuid, "");
        dataBase.setOperatorAsync(uuid, "");
        dataBase.setDateAsync(uuid, "");
        dataBase.setBanInfoAsync(uuid, "");
    }

    public long getDurationLong(String string) {
        String format = "";
        long duration = 0;
        long time = -1;
        for (Reason reason : BanSystem.getInstance().getBanReason()) {
            format = reason.getDuration();
            if (reason.getName().equalsIgnoreCase(string)) {
                if (!reason.getDuration().equalsIgnoreCase("-1")) {
                    duration = Long.parseLong(format.split(" ")[0]);
                    break;
                }
            }
            if (String.valueOf(reason.getId()).equalsIgnoreCase(string)) {
                if (!reason.getDuration().equalsIgnoreCase("-1")) {
                    duration = Long.parseLong(format.split(" ")[0]);
                    break;
                }
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

    public String getBanScreen(String reason, long duration) {
        return BanSystem.getInstance().getMessages().getListAsString("messages.screen.ban")
                .replace("%reason%", reason)
                .replace("%duration%", BanSystem.getInstance().getRemainingTime(duration));
    }

    public Reason getReason(String string) {
        for (Reason reason : BanSystem.getInstance().getBanReason()) {
            if (reason.getName().equalsIgnoreCase(string)) {
                return reason;
            }
            if (String.valueOf(reason.getId()).equalsIgnoreCase(string)) {
                return reason;
            }
        }
        return null;
    }
}
