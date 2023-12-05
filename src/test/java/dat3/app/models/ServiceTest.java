package dat3.app.models;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import dat3.app.utility.MongoUtility;

public class ServiceTest {
    @Test
    void TestServiceInsertion() {
        Service service = new Service();
        service.setCompanyId("656dd24dd86c1a107a567ed4");
        service.setId("656dd24dd86c1a107a567ed4");
        service.setName("Hulu");

        Service recService = null;
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession clientSession = client.startSession()) {
                MongoCollection<Document> serviceCollection = MongoUtility.getCollection(client, "services");
                service.insertOne(serviceCollection, clientSession);

                Service filter = new Service();
                filter.setId("656dd24dd86c1a107a567ed4");

                recService = filter.findOne(serviceCollection, clientSession);

                serviceCollection.drop();
            }
        } catch (Exception e) {
        }

        assertTrue(Service.ServiceEquals(service, recService));
    }
}
