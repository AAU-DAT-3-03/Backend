package dat3.app.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dat3.app.ProjectSettings;
import dat3.app.models.Company;
import dat3.app.models.Model;
import dat3.app.models.Service;
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
        MongoCollection<Document> incidentCollection = getCollection(client, "incidents");
        MongoCollection<Document> companyCollection = getCollection(client, "companies");
        MongoCollection<Document> servicesCollection = getCollection(client, "services");

        for (Company company : TestData.randomCompanies()) {
            company.insertOne(companyCollection, session);
        }
        List<Company> companies = MongoUtility.iterableToList(new Company().findMany(companyCollection, session));
        for (Service service : TestData.randomServices()) {
            service.setCompanyId(companies.get(TestData.randomIntExcl(companies.size())).getId());
            service.insertOne(servicesCollection, session);
        }

        for (User user : TestData.personalizedUsers()) {
            user.insertOne(userCollection, session);
        }
        for (User user : TestData.randomValidUsers()) {
            user.insertOne(userCollection, session);
        }


        List<User> users = iterableToList(userBuilder.getUser().findMany(userCollection, session));
        Iterator<String> headers = TestData.randomIncidentnames().iterator();
        IncidentBuilder incidentBuilder = new IncidentBuilder();
        for (int i = 0; i < 150; i++) {
            List<String> userIds = new ArrayList<>();
            
            for (int j = 0; j < TestData.randomIntExcl(10); j++) {
                userIds.add(users.get(TestData.randomIntExcl(users.size())).getId());
            }

            String acknowledgedBy = null;
            if (TestData.randomBoolean()) acknowledgedBy = users.get(TestData.randomIntExcl(users.size())).getId();
            incidentBuilder
                .setAcknowledgedBy(acknowledgedBy)
                .setAlarms(null)
                .setCreationDate(System.currentTimeMillis())
                .setHeader(headers.hasNext() ? headers.next() : null)
                .setPriority(TestData.randomIntExcl(4) + 1)
                .setResolved(TestData.randomIntExcl(4) == 0)
                .setUsers(userIds)
                .getIncident().insertOne(incidentCollection, session);
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

    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T item : iterable) {
            list.add(item);
        }
        return list;
    }

    public static <T extends Model<T>> List<Document> modelsToDocs(List<T> models) {
        List<Document> docs = new ArrayList<>(models.size());
        for (T model : models) {
            docs.add(model.toDocument());
        }
        return docs;
    }

    public static <T extends StandardModel<T>> List<Document> iterableToDocs(Iterable<T> iterable) {
        List<Document> list = new ArrayList<>();
        for (T t : iterable) {
            list.add(t.toDocument());
        }
        return list;
    }
}
