package dat3.app.models;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import dat3.app.utility.MongoUtility;

public class AlarmTest {
    @Test
    void TestAlarmInsertions() {
        Alarm alarm = new Alarm();
        alarm.setAlarmNote("Bla bla bla bla. Alarm notater.");
        alarm.setId("656dd24dd86c1a107a567ed4");
        alarm.setName("Dette er en alarm");
        alarm.setServiceId("656dd24dd86c1a107a567ed4");
        
        Alarm recAlarm = null;
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession clientSession = client.startSession()) {
                MongoCollection<Document> alarmCollection = MongoUtility.getCollection(client, "alarms");
                alarm.insertOne(alarmCollection, clientSession);

                Alarm filter = new Alarm();
                filter.setId("656dd24dd86c1a107a567ed4");
                
                recAlarm = filter.findOne(alarmCollection, clientSession);
                
                alarmCollection.drop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertTrue(Alarm.AlarmEquals(alarm, recAlarm));
    } 
}
