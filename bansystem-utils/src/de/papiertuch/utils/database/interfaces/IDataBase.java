package de.papiertuch.utils.database.interfaces;

import org.bson.Document;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public interface IDataBase {

    boolean isExists(UUID uuid);

    void create(UUID uuid);

    void setBanned(UUID uuid, boolean value);

    void setReason(UUID uuid, String reason);

    void setDate(UUID uuid, String date);

    void setOperator(UUID uuid, String name);

    void setBanPoints(UUID uuid, int points);

    void setDuration(UUID uuid, long duration);

    void setBanInfo(UUID uuid, String info);

    void addBanPoints(UUID uuid, int points);

    void removeBanPoints(UUID uuid, int points);

    void setAddress(UUID uuid, String address);

    void addHistory(UUID uuid, String reason, String operator);

    void editLastHistory(UUID uuid, String type, String info);

    ArrayList<Document> getHistory(UUID uuid);

    boolean isBanned(UUID uuid);

    boolean isIpBanned(String address);

    String getReason(UUID uuid);

    String getDate(UUID uuid);

    String getOperator(UUID uuid);

    int getBanPoints(UUID uuid);

    long getDuration(UUID uuid);

    String getBanInfo(UUID uuid);

    String getAddress(UUID uuid);

    void isExistsAsync(UUID uuid, Consumer<Boolean> consumer);

    void createAsync(UUID uuid);

    void setBannedAsync(UUID uuid, boolean value);

    void setReasonAsync(UUID uuid, String reason);

    void setDateAsync(UUID uuid, String date);

    void setOperatorAsync(UUID uuid, String name);

    void setBanPointsAsync(UUID uuid, int points);

    void setDurationAsync(UUID uuid, long duration);

    void setBanInfoAsync(UUID uuid, String info);

    void addBanPointsAsync(UUID uuid, int points);

    void removeBanPointsAsync(UUID uuid, int points);

    void setAddressAsync(UUID uuid, String address);

    void addHistoryAsync(UUID uuid, String reason, String operator);

    void editLastHistoryAsync(UUID uuid, String type, String info);

    void getHistoryAsync(UUID uuid, Consumer<ArrayList<Document>> consumer);

    void isBannedAsync(UUID uuid, Consumer<Boolean> consumer);

    void isIpBannedAsync(String address, Consumer<Boolean> consumer);

    void getReasonAsync(UUID uuid, Consumer<String> consumer);

    void getDateAsync(UUID uuid, Consumer<String> consumer);

    void getOperatorAsync(UUID uuid, Consumer<String> consumer);

    void getBanPointsAsync(UUID uuid, Consumer<Integer> consumer);

    void getDurationAsync(UUID uuid, Consumer<Long> consumer);

    void getBanInfoAsync(UUID uuid, Consumer<String> consumer);

    void getAddressAsync(UUID uuid, Consumer<String> consumer);
}
