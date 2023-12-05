package dat3.app.models;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import dat3.app.utility.MongoUtility;

public class EventTest {
    @Test
    void TestEventInsertion() {
        Event event = new Event();
        event.setAffectedObjectId("656dd24dd86c1a107a567ed4");
        event.setDate(203020l);
        event.setId("656dd24dd86c1a107a567ed4");
        event.setMessage("Helløj der!");
        event.setUserId("656dd24dd86c1a107a567ed4");
        event.setUserName("Mads Hvid Byriel");

        Event recEvent = null;
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession clientSession = client.startSession()) {
                MongoCollection<Document> eventCollection = MongoUtility.getCollection(client, "events");
                event.insertOne(eventCollection, clientSession);

                Event filter = new Event();
                filter.setId("656dd24dd86c1a107a567ed4");

                recEvent = filter.findOne(eventCollection, clientSession);

                eventCollection.drop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(Event.EventEquals(event, recEvent));
    }
}
