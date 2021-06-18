package de.papiertuch.utils.database;

import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.database.interfaces.IPlayerDataBase;
import org.bson.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MySQL implements IDataBase, IPlayerDataBase {

    private static Connection connection;

    private final ExecutorService executorService;
    private final String table, host, dataBase, user, password;
    private final int port;

    public MySQL(String table) {
        this.host = BanSystem.getInstance().getConfig().getString("database.host");
        this.port = BanSystem.getInstance().getConfig().getInt("database.port");
        this.dataBase = BanSystem.getInstance().getConfig().getString("database.dataBase");
        this.user = BanSystem.getInstance().getConfig().getString("database.user");
        this.password = BanSystem.getInstance().getConfig().getString("database.password");
        this.table = table;

        connect();

        this.executorService = Executors.newFixedThreadPool(4);
    }

    private void connect() {
        if (connection != null) return;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" +
                    this.port + "/" + this.dataBase +
                    "?autoReconnect=true", this.user, this.password);
            update("CREATE TABLE IF NOT EXISTS banTest (uuid VARCHAR(64), address VARCHAR(64), banPoints INT, banned " +
                    "BOOL, reason VARCHAR(64), banInfo VARCHAR(64), duration LONG, date VARCHAR(64), operator VARCHAR" +
                    "(64));");
            update("CREATE TABLE IF NOT EXISTS muteTest (uuid VARCHAR(64), address VARCHAR(64), banPoints INT, banned" +
                    " BOOL, reason VARCHAR(64), banInfo VARCHAR(64), duration LONG, date VARCHAR(64), operator " +
                    "VARCHAR(64));");
            update("CREATE TABLE IF NOT EXISTS banTestHistory (id VARCHAR(64), uuid VARCHAR(64), reason VARCHAR(64), " +
                    "user VARCHAR(64), date VARCHAR(64), banInfo VARCHAR(64), reduce VARCHAR(64), unban VARCHAR(64))");
            update("CREATE TABLE IF NOT EXISTS muteTestHistory (id VARCHAR(64), uuid VARCHAR(64), reason VARCHAR(64)," +
                    " user VARCHAR(64), date VARCHAR(64), banInfo VARCHAR(64), reduce VARCHAR(64), unban VARCHAR(64))");
            update("CREATE TABLE IF NOT EXISTS playerTest (uuid VARCHAR(64), notify BOOL);");
            System.out.println("[Punish] The connection to the MySQL server was successful");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("[Punish] The connection to the MySQL server failed...");
        }
    }

    public void update(String query) {
        if (connection == null) return;
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setValue(UUID uuid, String type, Object value) {
        try (PreparedStatement preparedStatement =
                     this.connection.prepareStatement("UPDATE " + table + " SET " + type + " = ? WHERE uuid = ?")) {
            preparedStatement.setObject(1, value);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Object getValueHistory(String id, String type) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM " + table +
                "History WHERE id = ?")) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getObject(type);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Object getValue(UUID uuid, String type) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM " + table + " " +
                "WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getObject(type);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public boolean isExists(UUID uuid) {
        return getValue(uuid, "uuid") != null;
    }

    @Override
    public void create(UUID uuid) {
        if (isExists(uuid)) return;
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(
                "INSERT INTO " + table + " (uuid, address, banPoints, banned, reason, banInfo, duration, date, " +
                        "operator) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, "");
            preparedStatement.setInt(3, 0);
            preparedStatement.setBoolean(4, false);
            preparedStatement.setString(5, "");
            preparedStatement.setString(6, "");
            preparedStatement.setLong(7, 0L);
            preparedStatement.setString(8, "");
            preparedStatement.setString(9, "");
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setBanned(UUID uuid, boolean value) {
        setValue(uuid, "banned", value);
    }

    @Override
    public void setReason(UUID uuid, String reason) {
        setValue(uuid, "reason", reason);
    }

    @Override
    public void setDate(UUID uuid, String date) {
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
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(
                "INSERT INTO " + table + "History (id, uuid, reason, user, date, banInfo, reduce, unban) VALUES (?, " +
                        "?, ?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, uuid + "#" + getHistory(uuid).size());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, operator);
            preparedStatement.setString(5, BanSystem.getInstance().getDateFormat().format(new Date()));
            preparedStatement.setString(6, "");
            preparedStatement.setString(7, "");
            preparedStatement.setString(8, "");
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void editLastHistory(UUID uuid, String type, String info) {
        int last = getHistory(uuid).size() - 1;
        String historyId = uuid.toString() + "#" + last;

        try (PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE " + table + "History SET "
                + type + " = ? WHERE id = ?")) {
            preparedStatement.setObject(1, info);
            preparedStatement.setString(2, historyId);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getHistorySize(UUID uuid) {
        int i = 0;
        try (PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM " + table + " " +
                "WHERE uuid = ?")) {
            preparedStatement.setString(1, uuid.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    i++;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return i;
    }

    @Override
    public ArrayList<Document> getHistory(UUID uuid) {
        ArrayList<Document> list = new ArrayList<>();
        for (int i = 1; i < getHistorySize(uuid); i++) {
            String id = uuid + "#" + i;
            Document document = new Document();
            document.put("id", id);
            document.put("uuid", uuid.toString());
            document.put("reason", getValueHistory(id, "reason"));
            document.put("date", getValueHistory(id, "date"));
            document.put("banInfo", getValueHistory(id, "banInfo"));
            document.put("reduce", getValueHistory(id, "reduce"));
            document.put("unban", getValueHistory(id, "unban"));
            list.add(document);
        }
        return list;
    }

    @Override
    public boolean isBanned(UUID uuid) {
        return (boolean) getValue(uuid, "banned");
    }

    @Override
    public boolean isIpBanned(String address) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " +
                "address = ?")) {
            preparedStatement.setString(1, address);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("banned");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public String getReason(UUID uuid) {
        return (String) getValue(uuid, "reason");
    }

    @Override
    public String getDate(UUID uuid) {
        return BanSystem.getInstance().getDateFormat().format(getValue(uuid, "date"));
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
        return Long.valueOf(String.valueOf(getValue(uuid, "duration")));
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
    public void setReasonAsync(UUID uuid, String reason) {
        this.executorService.execute(() -> setReason(uuid, reason));
    }

    @Override
    public void setDateAsync(UUID uuid, String date) {
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
    public void getHistoryAsync(UUID uuid, Consumer<ArrayList<Document>> consumer) {
        this.executorService.execute(() -> getHistory(uuid));
    }

    @Override
    public void isBannedAsync(UUID uuid, Consumer<Boolean> consumer) {
        this.executorService.execute(() -> consumer.accept(isBanned(uuid)));
    }

    @Override
    public void isIpBannedAsync(String address, Consumer<Boolean> consumer) {
        this.executorService.execute(() -> isIpBanned(address));
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
        if (isExistsPlayer(uuid)) return;
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(
                "INSERT INTO " + table + " (uuid, notify) VALUES (?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setBoolean(2, true);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
