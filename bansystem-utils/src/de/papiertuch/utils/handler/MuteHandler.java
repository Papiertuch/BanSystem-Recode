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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class MuteHandler {

    @Getter
    private IDataBase dataBase;
    private Config config, messages;
    private HashMap<String, Boolean> cache;

    public MuteHandler() {
        switch (BanSystem.getInstance().getConfig().getString("database.type")) {
            case "MongoDB":
                this.dataBase = new MongoDB("muteTest");
                break;
            case "File":
                break;
            default:
                this.dataBase = new MySQL("muteTest");
                break;
        }
        this.config = BanSystem.getInstance().getConfig();
        this.messages = BanSystem.getInstance().getMessages();
        this.cache = new HashMap<>();
    }

    public boolean mutePlayer(IBanPlayer banPlayer, String name, String reason) {
        return mutePlayer(banPlayer, name, reason, getReason(reason).getDuration());
    }

    public boolean mutePlayer(IBanPlayer banPlayer, String name, String reason, String duration, String... info) {
        if (banPlayer.getName().equalsIgnoreCase(name)) {
            banPlayer.sendMessage(messages.getString("messages.selfMuted"));
            return false;
        }
        UUID uuid = BanSystem.getInstance().getUuidFetcher().getUUID(name);
        dataBase.create(uuid);
        if (dataBase.isBanned(uuid)) {
            banPlayer.sendMessage(messages.getString("messages.isMuted"));
            return false;
        }

        IBanPlayer target = BanSystem.getInstance().getBanPlayer(uuid);

        if (!banPlayer.hasPermission(BanSystem.getInstance().getConfig().getString("permission.banEveryone"))) {
            if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v2")) {
                OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(uuid);
                if (offlinePlayer != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName())) {
                        banPlayer.sendMessage(messages.getString("messages.cannotMuted"));
                        return false;
                    }
                }
            } else if (BanSystem.getInstance().getConfig().getBoolean("module.cloudNet.v3")) {
                IPermissionUser permissionUser = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);
                if (permissionUser != null) {
                    if (config.getList("module.cloudNet.teamGroups").contains(CloudNetDriver.getInstance().getPermissionManagement().getHighestPermissionGroup(permissionUser).getName())) {
                        banPlayer.sendMessage(messages.getString("messages.cannotMuted"));
                        return false;
                    }
                }
            } else if (target != null && target.hasPermission(BanSystem.getInstance().getConfig().getString("permission.banBypass"))) {
                banPlayer.sendMessage(messages.getString("messages.cannotMuted"));
                return false;
            }
        }
        long banTime = -1;
        if (!duration.equalsIgnoreCase("-1")) {
            banTime = getDurationLong(reason, duration) + System.currentTimeMillis();
        } else {
            duration = "Permanent";
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
                teamPlayers.sendMessage(messages.getListAsString("messages.notify.mute")
                        .replace("%reason%", reasonObject.getName())
                        .replace("%duration%", BanSystem.getInstance().getRemainingTime(getDurationLong("", reasonObject.getDuration())))
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
            target.disconnect(messages.getListAsString("messages.screen.mute")
                    .replace("%reason%", reason)
                    .replace("%duration%", BanSystem.getInstance().getRemainingTime(getDurationLong("", reasonObject.getDuration()))));
        }

        return true;
    }

    public boolean unmutePlayer(IBanPlayer banPlayer, String name) {
        UUID uuid = BanSystem.getInstance().getUuidFetcher().getUUID(name);
        if (banPlayer.getName().equalsIgnoreCase(name)) {
            banPlayer.sendMessage(messages.getString("messages.selfUnMuted"));
            return false;
        }
        if (dataBase.isExists(uuid) && !dataBase.isBanned(uuid)) {
            banPlayer.sendMessage(messages.getString("messages.notMuted"));
            return false;
        }

        for (IBanPlayer teamPlayers : BanSystem.getInstance().getNotify()) {
            if (teamPlayers != null) {
                teamPlayers.sendMessage(messages.getListAsString("messages.notify.unmute")
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

    public Reason getReason(String string) {
        for (Reason reason : BanSystem.getInstance().getMuteReason()) {
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
