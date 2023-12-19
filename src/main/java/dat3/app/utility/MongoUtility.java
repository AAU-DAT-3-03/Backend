package dat3.app.utility;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dat3.app.ProjectSettings;

public abstract class MongoUtility {
    private static ProjectSettings settings = ProjectSettings.getProjectSettings();

    /**
     * Gets the client by reading the connection string from the project-settings.
     * @return A new MongoClient
     */
    public static MongoClient getClient() {
        return MongoClients.create(settings.getDbConnectionString());
    }

    /**
     * Gets the specified mongo collection.
     * @param client The client used for connection.
     * @param collectionName The name of the collection to find.
     * @return The Mongo collection.
     */
    public static MongoCollection<Document> getCollection(MongoClient client, String collectionName) {
        return client.getDatabase(settings.getDbName()).getCollection(collectionName);
    }


    /**
     * Gets the MongoDatabase based on the projectsettings.
     * @param client The client used for connection.
     * @return Returns the database used in this project.
     */
    public static MongoDatabase getDatabase(MongoClient client) {
        return client.getDatabase(settings.getDbName());
    }

    /**
     * Converts an iterable to a list. Mostly used to convert MongoIterable (query results) to a nice List. 
     * @param <T> The type of the iterable.
     * @param iterable The iterable.
     * @return A new list, containing all the values in the iterable.
     */
    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T item : iterable) {
            list.add(item);
        }
        return list;
    }
}