package dat3.app.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertManyResult;

import dat3.app.ProjectSettings;

public class MongoConnection {
    private String connectionString;
    private String dbName;
    private MongoClient client;

    private boolean hasTriedToConnect = false;

    public MongoConnection() {
        ProjectSettings settings = ProjectSettings.getProjectSettings();

        connectionString = settings != null ? settings.getDbConnectionString() : null;
        dbName = settings != null ? settings.getDbName() : null;
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

    public InsertManyResult insert(String collectionName, List<? extends Object> objects) {
        MongoCollection<Document> collection;
        if ((collection = getCollection(collectionName)) == null) return null;

        if (objects.size() == 0) {
            System.out.println("Trying to insert 0.");
            return null;
        }

        Gson gson = new Gson();
        List<Document> docs = new ArrayList<>();
        for (Object object : objects) {
            docs.add(Document.parse(gson.toJson(object)));
        }

        return collection.insertMany(docs);
    }

    public <T> List<T> find(String collectionName, Class<? extends T> clazz) {
        MongoCollection<Document> collection;
        if ((collection = getCollection(collectionName)) == null) return null;

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

    public <T> List<T> find(String collectionName, Class<? extends T> clazz, Bson filter) {
        MongoCollection<Document> collection;
        if ((collection = getCollection(collectionName)) == null) return null;

        Gson gson = new Gson();
        List<T> objects = new ArrayList<>();

        for (Document document : collection.find(filter)) {
            T obj;
            if ((obj = gson.fromJson(document.toJson(), clazz)) != null) {
                objects.add(obj);
            }
        }
        
        return objects;
    }

    public <T> List<T> find(String collectionName, Class<? extends T> clazz, EqualityTuple ... equalities) {
        MongoCollection<Document> collection;
        if ((collection = getCollection(collectionName)) == null) return null;

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

    private MongoCollection<Document> getCollection(String collectionName) {
        if (!isConnected()) {
            return null;
        }
        MongoDatabase db = client.getDatabase(dbName);
        return db.getCollection(collectionName);
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
