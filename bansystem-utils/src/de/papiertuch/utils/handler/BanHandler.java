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
    private Config config, messages;
    private HashMap<String, Boolean> cache;

    public BanHandler() {
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
        this.messages = BanSystem.getInstance().getMessages();
        this.cache = new HashMap<>();
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
            System.out.println("[Punish] No VPN key was found...");
        }
        return false;
    }

    public boolean banPlayer(IBanPlayer banPlayer, String name, String reason) {
        return banPlayer(banPlayer, name, reason, getReason(reason).getDuration());
    }

    public boolean banPlayer(IBanPlayer banPlayer, String name, String reason, String duration, String... info) {
        if (banPlayer.getName().equalsIgnoreCase(name)) {
            banPlayer.sendMessage(messages.getString("messages.selfBanned"));
            return false;
        }
        UUID uuid = BanSystem.getInstance().getUuidFetcher().getUUID(name);
        dataBase.create(uuid);
        if (dataBase.isBanned(uuid)) {
            banPlayer.sendMessage(messages.getString("messages.isBanned"));
            return false;
        }

        IBanPlayer target = BanSystem.getInstance().getBanPlayer(uuid);

        if (!banPlayer.hasPermission(BanSystem.getInstance().getConfig().getString("permission.banEveryone"))) {
            if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v2")) {
                OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(uuid);
                if (offlinePlayer != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName())) {
                        banPlayer.sendMessage(messages.getString("messages.cannotBanned"));
                        return false;
                    }
                }
            } else if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v3")) {
                IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);
                if (permissionUser != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUser).getName())) {
                        banPlayer.sendMessage(messages.getString("messages.cannotBanned"));
                        return false;
                    }
                }
            } else if (target != null && target.hasPermission(BanSystem.getInstance().getConfig().getString("permission.banBypass"))) {
                banPlayer.sendMessage(messages.getString("messages.cannotBanned"));
                return false;
            }
        }
        long banTime = -1;
        if (!duration.equalsIgnoreCase("-1")) {
            banTime = getDurationLong(reason, duration) + System.currentTimeMillis();
        }
        if (dataBase.getBanPoints(uuid) >= 100) {
            banTime = -1;
        }

        Reason reasonObject = getReason(reason);
        if (reasonObject == null) {
            reasonObject = new Reason(reason, 0, duration, 0, false);
        }

        String display = target == null ? name : target.getDisplayName();

        for (IBanPlayer teamPlayers : BanSystem.getInstance().getNotify()) {
            if (teamPlayers != null) {
                teamPlayers.sendMessage(messages.getListAsString("messages.notify.ban")
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
            target.disconnect(messages.getListAsString("messages.screen.ban")
                    .replace("%reason%", reason)
                    .replace("%duration%", BanSystem.getInstance().getRemainingTime(banTime)));
        }

        return true;
    }

    public boolean reduceBan(IBanPlayer banPlayer, String name, int reduce) {
        UUID uuid = BanSystem.getInstance().getUuidFetcher().getUUID(name);
        if (dataBase.isExists(uuid) && !dataBase.isBanned(uuid)) {
            banPlayer.sendMessage(messages.getString("messages.notBanned"));
            return false;
        }
        long duration = (dataBase.getDuration(uuid) / reduce);
        for (IBanPlayer teamPlayers : BanSystem.getInstance().getNotify()) {
            if (teamPlayers != null) {
                teamPlayers.sendMessage(messages.getListAsString("messages.notify.reduce")
                        .replace("%target%", name)
                        .replace("%duration%", String.valueOf(BanSystem.getInstance().getRemainingTime(duration)))
                        .replace("%player%", banPlayer.getDisplayName()));
            }
        }
        dataBase.setDurationAsync(uuid, duration);
        dataBase.editLastHistoryAsync(uuid, "reduce", banPlayer.getName() + "-" + BanSystem.getInstance().getDateFormat().format(new Date()));
        return true;
    }

    public boolean kickPlayer(IBanPlayer banPlayer, IBanPlayer target, String reason) {
        if (target == null) {
            banPlayer.sendMessage(messages.getString("messages.isOffline"));
            return false;
        }
        if (target.getName().equalsIgnoreCase(banPlayer.getName())) {
            banPlayer.sendMessage(messages.getString("messages.selfKicked"));
            return false;
        }
        if (!banPlayer.hasPermission(BanSystem.getInstance().getConfig().getString("permission.banEveryone"))) {
            if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v2")) {
                OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(target.getUniqueId());
                if (offlinePlayer != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName())) {
                        banPlayer.sendMessage(messages.getString("messages.cannotKicked"));
                        return false;
                    }
                }
            } else if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v3")) {
                IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(target.getUniqueId());
                if (permissionUser != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUser).getName())) {
                        banPlayer.sendMessage(messages.getString("messages.cannotKicked"));
                        return false;
                    }
                }
            } else if (target != null && target.hasPermission(BanSystem.getInstance().getConfig().getString("permission.banBypass"))) {
                System.out.println(target.getName());
                banPlayer.sendMessage(messages.getString("messages.cannotKicked"));
                return false;
            }
        }
        for (IBanPlayer teamPlayers : BanSystem.getInstance().getNotify()) {
            if (teamPlayers != null) {
                teamPlayers.sendMessage(messages.getListAsString("messages.notify.kick")
                        .replace("%target%", target.getDisplayName())
                        .replace("%reason%", reason)
                        .replace("%player%", banPlayer.getDisplayName()));
            }
        }
        target.disconnect(messages.getListAsString("messages.screen.kick")
                .replace("%reason%", reason)
                .replace("%operator%", banPlayer.getName()));
        return true;
    }

    public boolean unbanPlayer(IBanPlayer banPlayer, String name) {
        UUID uuid = BanSystem.getInstance().getUuidFetcher().getUUID(name);
        if (banPlayer.getName().equalsIgnoreCase(name)) {
            banPlayer.sendMessage(messages.getString("messages.selfUnBanned"));
            return false;
        }
        if (dataBase.isExists(uuid) && !dataBase.isBanned(uuid)) {
            banPlayer.sendMessage(messages.getString("messages.notBanned"));
            return false;
        }

        for (IBanPlayer teamPlayers : BanSystem.getInstance().getNotify()) {
            if (teamPlayers != null) {
                teamPlayers.sendMessage(messages.getListAsString("messages.notify.unban")
                        .replace("%target%", name)
                        .replace("%player%", banPlayer.getDisplayName()));
            }
        }
        Reason reason = getReason(dataBase.getReason(uuid));
        if (reason != null) {
            dataBase.removeBanPointsAsync(uuid, reason.getPoints());
        }

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

    public long getDurationLong(String name, String durationAsString) {
        long duration = Long.parseLong(durationAsString.split(" ")[0]);
        long time = -1;
        Reason reason = getReason(name);
        if (reason == null) {
            if (durationAsString.contains("s")) {
                time = duration * 1000;
            } else if (durationAsString.contains("m")) {
                time = duration * 1000 * 60;
            } else if (durationAsString.contains("h")) {
                time = duration * 1000 * 60 * 60;
            } else if (durationAsString.contains("d")) {
                time = duration * 1000 * 60 * 60 * 24;
            }
            return time;
        }
        if (reason.getDuration().contains("s")) {
            time = duration * 1000;
        } else if (reason.getDuration().contains("m")) {
            time = duration * 1000 * 60;
        } else if (reason.getDuration().contains("h")) {
            time = duration * 1000 * 60 * 60;
        } else if (reason.getDuration().contains("d")) {
            time = duration * 1000 * 60 * 60 * 24;
        }
        return time;
    }

    public String getBanScreen(String reason, long duration) {
        return messages.getListAsString("messages.screen.ban")
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
