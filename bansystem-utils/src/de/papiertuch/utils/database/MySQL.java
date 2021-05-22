package de.papiertuch.utils.database;

import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.database.interfaces.IPlayerDataBase;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MySQL implements IDataBase, IPlayerDataBase {

    private ExecutorService executorService;

    private Connection connection;

    public MySQL(String type, String host, int port, String dataBase, String user, String password) {
        this.executorService = Executors.newCachedThreadPool();

        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dataBase + "?autoReconnect=true", user, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void update(String query) {
        if (connection == null) {
            return;
        }
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isExits(UUID uuid) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM ban WHERE name = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("uuid") != null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void create(UUID uuid) {

    }

    @Override
    public void setBanned(UUID uuid, boolean value) {

    }

    @Override
    public void setIpBanned(UUID uuid, boolean value) {

    }

    @Override
    public void setReason(UUID uuid, String reason) {

    }

    @Override
    public void setDate(UUID uuid, long date) {

    }

    @Override
    public void setOperator(UUID uuid, String name) {

    }

    @Override
    public void setBanPoints(UUID uuid, int points) {

    }

    @Override
    public void setDuration(UUID uuid, long duration) {

    }

    @Override
    public void setBanInfo(UUID uuid, String info) {

    }

    @Override
    public void addBanPoints(UUID uuid, int points) {

    }

    @Override
    public void removeBanPoints(UUID uuid, int points) {

    }

    @Override
    public void setAddress(UUID uuid, String address) {

    }

    @Override
    public void addHistory(UUID uuid, String reason, String operator) {

    }

    @Override
    public void editLastHistory(UUID uuid, String type, String info) {

    }

    @Override
    public boolean getBanned(UUID uuid) {
        return false;
    }

    @Override
    public boolean getIpBanned(UUID uuid) {
        return false;
    }

    @Override
    public String getReason(UUID uuid) {
        return null;
    }

    @Override
    public String getDate(UUID uuid) {
        return null;
    }

    @Override
    public String getOperator(UUID uuid) {
        return null;
    }

    @Override
    public int getBanPoints(UUID uuid) {
        return 0;
    }

    @Override
    public long getDuration(UUID uuid) {
        return 0;
    }

    @Override
    public String getBanInfo(UUID uuid) {
        return null;
    }

    @Override
    public String getAddress(UUID uuid) {
        return null;
    }

    @Override
    public void isExitsAsync(UUID uuid, Consumer<Boolean> consumer) {

    }

    @Override
    public void createAsync(UUID uuid) {

    }

    @Override
    public void setBannedAsync(UUID uuid, boolean value) {

    }

    @Override
    public void setIpBannedAsync(UUID uuid, boolean value) {

    }

    @Override
    public void setReasonAsync(UUID uuid, String reason) {

    }

    @Override
    public void setDateAsync(UUID uuid, long date) {

    }

    @Override
    public void setOperatorAsync(UUID uuid, String name) {

    }

    @Override
    public void setBanPointsAsync(UUID uuid, int points) {

    }

    @Override
    public void setDurationAsync(UUID uuid, long duration) {

    }

    @Override
    public void setBanInfoAsync(UUID uuid, String info) {

    }

    @Override
    public void addBanPointsAsync(UUID uuid, int points) {

    }

    @Override
    public void removeBanPointsAsync(UUID uuid, int points) {

    }

    @Override
    public void setAddressAsync(UUID uuid, String address) {

    }

    @Override
    public void addHistoryAsync(UUID uuid, String reason, String operator) {

    }

    @Override
    public void editLastHistoryAsync(UUID uuid, String type, String info) {

    }

    @Override
    public void getBannedAsync(UUID uuid, Consumer<Boolean> consumer) {

    }

    @Override
    public void getIpBannedAsync(UUID uuid, Consumer<Boolean> consumer) {

    }

    @Override
    public void getReasonAsync(UUID uuid, Consumer<String> consumer) {

    }

    @Override
    public void getDateAsync(UUID uuid, Consumer<String> consumer) {

    }

    @Override
    public void getOperatorAsync(UUID uuid, Consumer<String> consumer) {

    }

    @Override
    public void getBanPointsAsync(UUID uuid, Consumer<Integer> consumer) {

    }

    @Override
    public void getDurationAsync(UUID uuid, Consumer<Long> consumer) {

    }

    @Override
    public void getBanInfoAsync(UUID uuid, Consumer<String> consumer) {

    }

    @Override
    public void getAddressAsync(UUID uuid, Consumer<String> consumer) {

    }

    @Override
    public boolean isExitsPlayer(UUID uuid) {
        return false;
    }

    @Override
    public void createPlayer(UUID uuid) {

    }

    @Override
    public void setNotify(UUID uuid, boolean value) {

    }

    @Override
    public boolean isNotify(UUID uuid) {
        return false;
    }

    @Override
    public boolean isExitsPlayerAsync(UUID uuid) {
        return false;
    }

    @Override
    public void createPlayerAsync(UUID uuid) {

    }

    @Override
    public void setNotifyAsync(UUID uuid, boolean value) {

    }

    @Override
    public boolean isNotifyAsync(UUID uuid) {
        return false;
    }
}
