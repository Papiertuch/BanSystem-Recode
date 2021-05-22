package de.papiertuch.utils.database;

import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.database.interfaces.IPlayerDataBase;
import org.bson.Document;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MySQL implements IDataBase, IPlayerDataBase {

    private ExecutorService executorService;
    private String host, dataBase, user, password;
    private int port;
    private Connection connection;
    private String table;

    public MySQL(String table, String host, int port, String dataBase, String user, String password) {
        this.host = host;
        this.port = port;
        this.dataBase = dataBase;
        this.user = user;
        this.password = password;
        this.table = table;
        this.executorService = Executors.newCachedThreadPool();
        connect();
    }

    private void connect() {
        if (connection != null) return;
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + dataBase + "?autoReconnect=true", user, password);
            System.out.println("[BanSystem] The connection to the MySQL server was successful");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("[BanSystem] The connection to the MySQL server failed...");
        }
    }

    public void update(String query) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setValue(UUID uuid, String type, Object value) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE " + table + " SET " + type + " = ? WHERE uuid = ?")) {
            preparedStatement.setObject(1, value);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Object getValue(UUID uuid, String type) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM " + type + " WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getObject(type);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isExists(UUID uuid) {
        return getValue(uuid, "uuid") != null;
    }

    @Override
    public void create(UUID uuid) {
        //TODO CREATE
    }

    @Override
    public void setBanned(UUID uuid, boolean value) {
        setValue(uuid, "banned", value);
    }

    @Override
    public void setIpBanned(UUID uuid, boolean value) {
        setValue(uuid, "ipBanned", value);
    }

    @Override
    public void setReason(UUID uuid, String reason) {
        setValue(uuid, "reason", reason);
    }

    @Override
    public void setDate(UUID uuid, long date) {
        setValue(uuid, "date", date);
    }

    @Override
    public void setOperator(UUID uuid, String name) {
        setValue(uuid, "operator", name);
    }

    @Override
    public void setBanPoints(UUID uuid, int points) {
        setValue(uuid, "banPoints", points);
    }

    @Override
    public void setDuration(UUID uuid, long duration) {
        setValue(uuid, "duration", duration);
    }

    @Override
    public void setBanInfo(UUID uuid, String info) {
        setValue(uuid, "banInfo", info);
    }

    @Override
    public void addBanPoints(UUID uuid, int points) {
        setValue(uuid, "banPoints", (getBanPoints(uuid) + points));
    }

    @Override
    public void removeBanPoints(UUID uuid, int points) {
        setValue(uuid, "banPoints", (getBanPoints(uuid) - points));
    }

    @Override
    public void setAddress(UUID uuid, String address) {
        setValue(uuid, "address", address);
    }

    @Override
    public void addHistory(UUID uuid, String reason, String operator) {

    }

    @Override
    public void editLastHistory(UUID uuid, String type, String info) {

    }

    @Override
    public boolean getBanned(UUID uuid) {
        return (boolean) getValue(uuid, "banned");
    }

    @Override
    public boolean getIpBanned(UUID uuid) {
        return (boolean) getValue(uuid, "ipBanned");
    }

    @Override
    public String getReason(UUID uuid) {
        return (String) getValue(uuid, "reason");
    }

    @Override
    public String getDate(UUID uuid) {
        return new SimpleDateFormat("dd.MM.yyyy").format(getValue(uuid, "date"));
    }

    @Override
    public String getOperator(UUID uuid) {
        return (String) getValue(uuid, "operator");
    }

    @Override
    public int getBanPoints(UUID uuid) {
        return (int) getValue(uuid, "banPoints");
    }

    @Override
    public long getDuration(UUID uuid) {
        return (long) getValue(uuid, "duration");
    }

    @Override
    public String getBanInfo(UUID uuid) {
        return (String) getValue(uuid, "banInfo");
    }

    @Override
    public String getAddress(UUID uuid) {
        return (String) getValue(uuid, "address");
    }

    @Override
    public void isExistsAsync(UUID uuid, Consumer<Boolean> consumer) {
        this.executorService.execute(() -> consumer.accept(isExists(uuid)));
    }

    @Override
    public void createAsync(UUID uuid) {
        this.executorService.execute(() -> create(uuid));
    }

    @Override
    public void setBannedAsync(UUID uuid, boolean value) {
        this.executorService.execute(() -> setBanned(uuid, value));
    }

    @Override
    public void setIpBannedAsync(UUID uuid, boolean value) {
        this.executorService.execute(() -> setIpBanned(uuid, value));
    }

    @Override
    public void setReasonAsync(UUID uuid, String reason) {
        this.executorService.execute(() -> setReason(uuid, reason));
    }

    @Override
    public void setDateAsync(UUID uuid, long date) {
        this.executorService.execute(() -> setDate(uuid, date));
    }

    @Override
    public void setOperatorAsync(UUID uuid, String name) {
        this.executorService.execute(() -> setOperator(uuid, name));
    }

    @Override
    public void setBanPointsAsync(UUID uuid, int points) {
        this.executorService.execute(() -> setBanPoints(uuid, points));
    }

    @Override
    public void setDurationAsync(UUID uuid, long duration) {
        this.executorService.execute(() -> setDuration(uuid, duration));
    }

    @Override
    public void setBanInfoAsync(UUID uuid, String info) {
        this.executorService.execute(() -> setBanInfo(uuid, info));
    }

    @Override
    public void addBanPointsAsync(UUID uuid, int points) {
        this.executorService.execute(() -> addBanPoints(uuid, points));
    }

    @Override
    public void removeBanPointsAsync(UUID uuid, int points) {
        this.executorService.execute(() -> removeBanPoints(uuid, points));
    }

    @Override
    public void setAddressAsync(UUID uuid, String address) {
        this.executorService.execute(() -> setAddress(uuid, address));
    }

    @Override
    public void addHistoryAsync(UUID uuid, String reason, String operator) {
        this.executorService.execute(() -> addHistory(uuid, reason, operator));
    }

    @Override
    public void editLastHistoryAsync(UUID uuid, String type, String info) {
        this.executorService.execute(() -> editLastHistory(uuid, type, info));
    }

    @Override
    public void getBannedAsync(UUID uuid, Consumer<Boolean> consumer) {
        this.executorService.execute(() -> consumer.accept(getBanned(uuid)));
    }

    @Override
    public void getIpBannedAsync(UUID uuid, Consumer<Boolean> consumer) {
        this.executorService.execute(() -> consumer.accept(getIpBanned(uuid)));
    }

    @Override
    public void getReasonAsync(UUID uuid, Consumer<String> consumer) {
        this.executorService.execute(() -> consumer.accept(getReason(uuid)));
    }

    @Override
    public void getDateAsync(UUID uuid, Consumer<String> consumer) {
        this.executorService.execute(() -> consumer.accept(getDate(uuid)));
    }

    @Override
    public void getOperatorAsync(UUID uuid, Consumer<String> consumer) {
        this.executorService.execute(() -> consumer.accept(getOperator(uuid)));
    }

    @Override
    public void getBanPointsAsync(UUID uuid, Consumer<Integer> consumer) {
        this.executorService.execute(() -> consumer.accept(getBanPoints(uuid)));
    }

    @Override
    public void getDurationAsync(UUID uuid, Consumer<Long> consumer) {
        this.executorService.execute(() -> consumer.accept(getDuration(uuid)));
    }

    @Override
    public void getBanInfoAsync(UUID uuid, Consumer<String> consumer) {
        this.executorService.execute(() -> consumer.accept(getBanInfo(uuid)));
    }

    @Override
    public void getAddressAsync(UUID uuid, Consumer<String> consumer) {
        this.executorService.execute(() -> consumer.accept(getAddress(uuid)));
    }

    @Override
    public boolean isExistsPlayer(UUID uuid) {
        return getValue(uuid, "uuid") != null;
    }

    @Override
    public void createPlayer(UUID uuid) {
    }

    @Override
    public void setNotify(UUID uuid, boolean value) {
        setValue(uuid, "notify", value);
    }

    @Override
    public boolean isNotify(UUID uuid) {
        return (boolean) getValue(uuid, "notify");
    }

    @Override
    public void isExistsPlayerAsync(UUID uuid, Consumer<Boolean> consumer) {
        this.executorService.execute(() -> consumer.accept(isExistsPlayer(uuid)));
    }

    @Override
    public void createPlayerAsync(UUID uuid) {
        this.executorService.execute(() -> createPlayer(uuid));
    }

    @Override
    public void setNotifyAsync(UUID uuid, boolean value) {
        this.executorService.execute(() -> setNotify(uuid, value));
    }

    @Override
    public void isNotifyAsync(UUID uuid, Consumer<Boolean> consumer) {
        this.executorService.execute(() -> consumer.accept(isNotify(uuid)));
    }
}
