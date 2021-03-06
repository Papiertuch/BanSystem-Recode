package de.papiertuch.utils.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.papiertuch.utils.BanSystem;
import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.database.interfaces.IPlayerDataBase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MongoDB implements IDataBase, IPlayerDataBase {

    private static MongoCredential credential;
    private static MongoClientOptions clientOptions;
    private static MongoClient client;
    private static MongoDatabase mongoDatabase;
    private ExecutorService executorService;
    private String host, dataBase, user, password;
    private int port;
    private MongoCollection<Document> collection, historyCollection;


    public MongoDB(String collection) {
        this.host = BanSystem.getInstance().getConfig().getString("database.host");
        this.port = BanSystem.getInstance().getConfig().getInt("database.port");
        this.dataBase = BanSystem.getInstance().getConfig().getString("database.dataBase");
        this.user = BanSystem.getInstance().getConfig().getString("database.user");
        this.password = BanSystem.getInstance().getConfig().getString("database.password");

        connect();

        this.executorService = Executors.newCachedThreadPool();
        this.collection = mongoDatabase.getCollection(collection);
        this.historyCollection = mongoDatabase.getCollection(collection + "History");
    }

    private void connect() {
        if (mongoDatabase != null) return;
        try {
            credential = MongoCredential.createScramSha256Credential(user, dataBase, password.toCharArray());
            clientOptions = MongoClientOptions.builder().build();
            client = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential), clientOptions);
            mongoDatabase = client.getDatabase(dataBase);
            System.out.println("[Punish] The connection to the MongoDB server was successful");
        } catch (Exception ex) {
            System.out.println("[Punish] The connection to the MongoDB server failed...");
        }
    }


    private Document getDocument(UUID uuid) {
        return this.collection.find(Filters.eq("_id", uuid.toString())).first();
    }

    public void setValue(UUID uuid, String type, Object value) {
        this.collection.updateOne(Filters.eq("_id", uuid.toString()), new Document("$set", new Document(type, value)));
    }

    @Override
    public boolean isConnected() {
        return mongoDatabase != null;
    }

    @Override
    public boolean isExists(UUID uuid) {
        return getDocument(uuid) != null;
    }

    @Override
    public void create(UUID uuid) {
        if (getDocument(uuid) != null) return;
        Document document = new Document();
        document.put("_id", uuid.toString());
        document.put("address", "");
        document.put("banPoints", 0);
        document.put("banned", false);
        document.put("reason", "");
        document.put("banInfo", "");
        document.put("duration", 0l);
        document.put("date", 0l);
        document.put("operator", "");
        this.collection.insertOne(document);
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
        Document document = new Document();
        document.put("_id", uuid.toString() + "#" + getHistory(uuid).size());
        document.put("uuid", uuid.toString());
        document.put("reason", reason);
        document.put("user", operator);
        document.put("date", BanSystem.getInstance().getDateFormat().format(new Date()));
        document.put("banInfo", "");
        document.put("reduce", "");
        document.put("unban", "");
        this.historyCollection.insertOne(document);
    }

    @Override
    public void editLastHistory(UUID uuid, String type, String info) {
        int last = getHistory(uuid).size() - 1;
        String historyId = uuid.toString() + "#" + last;
        this.historyCollection.updateOne(Filters.eq("_id", historyId), new Document("$set", new Document(type, info)));
    }

    @Override
    public ArrayList<Document> getHistory(UUID uuid) {
        ArrayList<Document> list = new ArrayList<>();
        FindIterable<Document> documentAddress = this.historyCollection.find(Filters.eq("uuid", uuid.toString()));
        for (Document document : documentAddress) {
            list.add(document);
        }
        return list;
    }

    @Override
    public boolean isBanned(UUID uuid) {
        return getDocument(uuid).getBoolean("banned");
    }

    @Override
    public boolean isIpBanned(String address) {
        FindIterable<Document> documentAddress = this.collection.find(Filters.eq("address", address));
        for (Document document : documentAddress) {
            if ((boolean) document.get("banned")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getReason(UUID uuid) {
        return getDocument(uuid).getString("reason");
    }

    @Override
    public String getDate(UUID uuid) {
        return BanSystem.getInstance().getDateFormat().format(getDocument(uuid).getLong("date"));
    }

    @Override
    public String getOperator(UUID uuid) {
        return getDocument(uuid).getString("operator");
    }

    @Override
    public int getBanPoints(UUID uuid) {
        return getDocument(uuid).getInteger("banPoints");
    }

    @Override
    public long getDuration(UUID uuid) {
        return getDocument(uuid).getLong("duration");
    }

    @Override
    public String getBanInfo(UUID uuid) {
        return getDocument(uuid).getString("banInfo");
    }

    @Override
    public String getAddress(UUID uuid) {
        return getDocument(uuid).getString("address");
    }

    @Override
    public void isExistsAsync(UUID uuid, Consumer<Boolean> consumer) {
        this.executorService.execute(() -> consumer.accept(isExists(uuid)));
    }

    @Override
    public void getHistoryAsync(UUID uuid, Consumer<ArrayList<Document>> consumer) {
        this.executorService.execute(() -> consumer.accept(getHistory(uuid)));
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
    public void isBannedAsync(UUID uuid, Consumer<Boolean> consumer) {
        this.executorService.execute(() -> consumer.accept(isBanned(uuid)));
    }

    @Override
    public void isIpBannedAsync(String address, Consumer<Boolean> consumer) {
        this.executorService.execute(() -> consumer.accept(isIpBanned(address)));
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
        return getDocument(uuid) != null;
    }

    @Override
    public void createPlayer(UUID uuid) {
        if (getDocument(uuid) != null) return;
        Document document = new Document();
        document.put("_id", uuid.toString());
        document.put("notify", true);
        this.collection.insertOne(document);
    }

    @Override
    public void setNotify(UUID uuid, boolean value) {
        setValue(uuid, "notify", value);
    }

    @Override
    public boolean isNotify(UUID uuid) {
        return getDocument(uuid).getBoolean("notify");
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
