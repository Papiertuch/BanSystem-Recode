package de.papiertuch.utils.database;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.papiertuch.utils.database.interfaces.IDataBase;
import de.papiertuch.utils.database.interfaces.IPlayerDataBase;
import org.bson.Document;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MongoDB implements IDataBase, IPlayerDataBase {

    private ExecutorService executorService;
    private String host, dataBase, user, password;
    private int port;

    private MongoCredential credential;
    private MongoClientOptions clientOptions;
    private MongoClient client;
    private MongoDatabase mongoDatabase;

    private MongoCollection<Document> collection, historyCollection;


    public MongoDB(String type, String host, int port, String dataBase, String user, String password) {
        this.executorService = Executors.newCachedThreadPool();

        this.host = host;
        this.port = port;
        this.dataBase = dataBase;
        this.user = user;
        this.password = password;

        this.credential = MongoCredential.createCredential(user, dataBase, password.toCharArray());
        this.clientOptions = MongoClientOptions.builder().writeConcern(WriteConcern.JOURNALED).build();
        this.client = new MongoClient(new ServerAddress("127.0.0.1", 27017), Arrays.asList(this.credential), this.clientOptions);
        this.mongoDatabase = this.client.getDatabase(dataBase);

        this.collection = this.mongoDatabase.getCollection(type);
        this.historyCollection = this.mongoDatabase.getCollection(type + "History");

    }

    public Document getDocument(UUID uuid) {
        return this.collection.find(Filters.eq("_id", uuid.toString())).first();
    }

    public void getDocumentAsync(UUID uuid, Consumer<Document> consumer) {
        this.executorService.execute(() -> consumer.accept(getDocument(uuid)));
    }

    public void setValue(UUID uuid, String type, Object value) {
        collection.updateOne(Filters.eq("_id", uuid), new Document("$set", new Document(type, value)));
    }

    @Override
    public boolean isExits(UUID uuid) {
        return getDocument(uuid) != null;
    }

    @Override
    public void create(UUID uuid) {
        if (getDocument(uuid) == null) {
            return;
        }
        Document document = new Document();
        document.put("_id", uuid.toString());
        document.put("address", "");
        document.put("banPoints", 0);
        document.put("banned", false);
        document.put("ipBanned", false);
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
    public void setIpBanned(UUID uuid, boolean value) {
        setValue(uuid, "ipBanned", value);
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
