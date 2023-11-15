package dat3.app.utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import dat3.app.ProjectSettings;
import dat3.app.models.Incident;
import dat3.app.models.User;
import dat3.app.models.Incident.IncidentBuilder;
import dat3.app.models.StandardModel;
import dat3.app.models.User.UserBuilder;
import dat3.app.testkit.TestData;

public abstract class MongoUtility {
    private static ProjectSettings settings = ProjectSettings.getProjectSettings();

    public static void wipeDatabaseWithMock() throws Exception {
        MongoClient client = getClient();
        ClientSession session = client.startSession();
        MongoDatabase db = getDatabase(client);
        db.drop(session);
        UserBuilder userBuilder = new UserBuilder();
        MongoCollection<Document> userCollection = getCollection(client, "users");
    
        for (User user : TestData.personalizedUsers()) {
            user.insertOne(userCollection, session);
        }
        for (User user : TestData.randomValidUsers()) {
            user.insertOne(userCollection, session);
        }


        {
            List<Incident> incidents = new ArrayList<>();
            IncidentBuilder incidentBuilder = new IncidentBuilder();
            for (int i = 0; i < 150; i++) {
                boolean acknowledged = TestData.randomBoolean();
                incidents.add(incidentBuilder
                    .setAcknowledged(acknowledged)
                    .setAcknowledgedBy(null)
                    .setAlarms(null)
                    .setCreationDate(new Date())
                    .setPriority(TestData.randomIntExcl(4) + 1)
                    .setUsers(null)
                    .getIncident());
            }

            for (User user : userBuilder.getUser().findMany(userCollection, session)) {

            }
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

    public static <T extends StandardModel<T>> List<Document> iterableToDocs(Iterable<T> iterable) {
        List<Document> list = new ArrayList<>();
        for (T t : iterable) {
            list.add(t.toDocument());
        }
        return list;
    }
}
