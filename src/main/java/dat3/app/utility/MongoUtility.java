package dat3.app.utility;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dat3.app.ProjectSettings;
import dat3.app.models.User;
import dat3.app.testkit.TestData;

public abstract class MongoUtility {
    private static ProjectSettings settings = ProjectSettings.getProjectSettings();

    public static void wipeDatabaseWithMock() throws Exception {
        MongoClient client = getClient();
        ClientSession session = client.startSession();
        MongoDatabase db = getDatabase(client);
        db.drop(session);
        
        MongoCollection<Document> userCollection = getCollection(client, "users");
        for (User user : TestData.personalizedUsers()) {
            user.insertOne(userCollection, session);
        }
        for (User user : TestData.randomValidUsers()) {
            user.insertOne(userCollection, session);
        }

        session.close();
        client.close();
    }

    public static MongoClient getClient() {
        return MongoClients.create(settings.getDbConnectionString());
    }

    public static MongoCollection<Document> getCollection(MongoClient client, String collectionName) {
        return client.getDatabase(settings.getDbName()).getCollection(collectionName);
    }

    public static MongoDatabase getDatabase(MongoClient client) {
        return client.getDatabase(settings.getDbName());
    }
}
