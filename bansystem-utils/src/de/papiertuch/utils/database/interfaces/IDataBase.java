package de.papiertuch.utils.database.interfaces;

import java.util.UUID;
import java.util.function.Consumer;

public interface IDataBase {

    public boolean isExists(UUID uuid);

    public void create(UUID uuid);

    public void setBanned(UUID uuid, boolean value);

    public void setIpBanned(UUID uuid, boolean value);

    public void setReason(UUID uuid, String reason);

    public void setDate(UUID uuid, long date);

    public void setOperator(UUID uuid, String name);

    public void setBanPoints(UUID uuid, int points);

    public void setDuration(UUID uuid, long duration);

    public void setBanInfo(UUID uuid, String info);

    public void addBanPoints(UUID uuid, int points);

    public void removeBanPoints(UUID uuid, int points);

    public void setAddress(UUID uuid, String address);

    public void addHistory(UUID uuid, String reason, String operator);

    public void editLastHistory(UUID uuid, String type, String info);

    public boolean getBanned(UUID uuid);

    public boolean getIpBanned(UUID uuid);

    public String getReason(UUID uuid);

    public String getDate(UUID uuid);

    public String getOperator(UUID uuid);

    public int getBanPoints(UUID uuid);

    public long getDuration(UUID uuid);

    public String getBanInfo(UUID uuid);

    public String getAddress(UUID uuid);

    public void isExistsAsync(UUID uuid, Consumer<Boolean> consumer);

    public void createAsync(UUID uuid);

    public void setBannedAsync(UUID uuid, boolean value);

    public void setIpBannedAsync(UUID uuid, boolean value);

    public void setReasonAsync(UUID uuid, String reason);

    public void setDateAsync(UUID uuid, long date);

    public void setOperatorAsync(UUID uuid, String name);

    public void setBanPointsAsync(UUID uuid, int points);

    public void setDurationAsync(UUID uuid, long duration);

    public void setBanInfoAsync(UUID uuid, String info);

    public void addBanPointsAsync(UUID uuid, int points);

    public void removeBanPointsAsync(UUID uuid, int points);

    public void setAddressAsync(UUID uuid, String address);

    public void addHistoryAsync(UUID uuid, String reason, String operator);

    public void editLastHistoryAsync(UUID uuid, String type, String info);

    public void getBannedAsync(UUID uuid, Consumer<Boolean> consumer);

    public void getIpBannedAsync(UUID uuid, Consumer<Boolean> consumer);

    public void getReasonAsync(UUID uuid, Consumer<String> consumer);

    public void getDateAsync(UUID uuid, Consumer<String> consumer);

    public void getOperatorAsync(UUID uuid, Consumer<String> consumer);

    public void getBanPointsAsync(UUID uuid, Consumer<Integer> consumer);

    public void getDurationAsync(UUID uuid, Consumer<Long> consumer);

    public void getBanInfoAsync(UUID uuid, Consumer<String> consumer);

    public void getAddressAsync(UUID uuid, Consumer<String> consumer);
}
