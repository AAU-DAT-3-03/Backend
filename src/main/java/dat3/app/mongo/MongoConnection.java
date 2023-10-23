package dat3.app.mongo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;

import dat3.app.App;

public class MongoConnection {
    private String connectionString;
    private String dbName;
    private MongoClient client;

    private boolean hasTriedToConnect = false;

    public MongoConnection() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
        Gson gson = new Gson();

        ProjectInfo info = gson.fromJson(App.getProjectSettingsReader(), ProjectInfo.class);

        connectionString = info != null ? info.getDbConnectionString() : null;
        dbName = info != null ? info.getDbName() : null;
    }

    public boolean connectToDb() {
        this.hasTriedToConnect = true;
        try {
            this.client = MongoClients.create(this.connectionString);
            return true;
        } catch (Exception e) {
            this.client = null;
            return false;
        }
    }

    public InsertOneResult insert(String collectionName, String json) {
        if (!isConnected()) {
            return null;
        }
        MongoDatabase db = client.getDatabase(dbName);
        MongoCollection<Document> collection = db.getCollection(collectionName);

        return collection.insertOne(Document.parse(json));
    }

    public InsertManyResult insert(String collectionName, String ... jsons) {
        if (!isConnected()) {
            return null;
        }
        if (jsons.length == 0) {
            System.out.println("Trying to insert 0.");
            return null;
        }

        
        MongoDatabase db = client.getDatabase(dbName);
        MongoCollection<Document> collection = db.getCollection(collectionName);

        List<Document> docs = new ArrayList<>();
        for (String json : jsons) {
            docs.add(Document.parse(json));
        }

        return collection.insertMany(docs);
    }

    public <T> List<T> find(String collectionName, Class<? extends T> clazz) {
        if (!isConnected()) {
            return null;
        }
        MongoDatabase db = client.getDatabase(dbName);
        MongoCollection<Document> collection = db.getCollection(collectionName);

        Gson gson = new Gson();
        List<T> objects = new ArrayList<>();

        for (Document document : collection.find()) {
            T obj;
            if ((obj = gson.fromJson(document.toJson(), clazz)) != null) {
                objects.add(obj);
            }
        }

        return objects;
    }

    public <T> List<T> find(String collectionName, Class<? extends T> clazz, EqualityTuple ... equalities) {
        if (!isConnected()) {
            return null;
        }
        MongoDatabase db = client.getDatabase(dbName);
        MongoCollection<Document> collection = db.getCollection(collectionName);

        Gson gson = new Gson();
        List<T> objects = new ArrayList<>();

        Bson[] filters = new Bson[equalities.length];
        for (int i = 0; i < filters.length; i++) {
            filters[i] = eq(equalities[i].getFieldName(), equalities[i].getValue());
        }

        for (Document document : collection.find(and(filters))) {
            T obj;
            if ((obj = gson.fromJson(document.toJson(), clazz)) != null) {
                objects.add(obj);
            }
        }

        return objects;
    }

    private boolean isConnected() {
        if (!hasTriedToConnect) {
            System.out.println("Connect with 'connectToDb' first.");
            return false;
        }
        if (client == null) {
            System.out.println("Is not connected to the database.");
            return false;
        }
        return true;
    }


}

class ProjectInfo {
    private String dbConnectionString;
    private String dbName;

    public String getDbName() {
        return dbName;
    }

    public String getDbConnectionString() {
        return dbConnectionString;
    }
}
