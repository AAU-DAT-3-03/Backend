package dat3.app.utility;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;

import dat3.app.models.Incident;
import dat3.app.models.User;
import dat3.app.testkit.TestData2;

public class NotificationUtility {
    public static boolean sendNotifications() {
        List<String> registrationTokens = getAllRegistrationTokens();

        if (registrationTokens == null || registrationTokens.size() == 0) return false;

        Incident generatedIncident = TestData2.generateIncidents(1).get(0);
        generatedIncident.setResolved(false);
        generatedIncident.setCallIds(new ArrayList<>());
        generatedIncident.setUserIds(new ArrayList<>());
        generatedIncident.setCreationDate(System.currentTimeMillis());
        generatedIncident.setAcknowledgedBy(null);
        
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> incidentCollection = MongoUtility.getCollection(client, "incidents");
                InsertOneResult result = generatedIncident.insertOne(incidentCollection, session); 
                Incident filter = new Incident();
                filter.setId(result.getInsertedId().asObjectId().getValue().toHexString());
                generatedIncident = filter.findOne(incidentCollection, session);
                if (generatedIncident == null) throw new Exception("Couldn't find generated incident in database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        int count = 0;
        for (String token : registrationTokens) {
            Message message;
            try {
                message = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle("Incident Alarm")
                                .setBody("Something has happened! Please check it out.")
                                .build())
                        .putData("type", "alarm")
                        .putData("incidentId", generatedIncident.getId())
                        .setToken(token)
                        .build();
            } catch (Exception e) {
                System.out.println("Failed while building notification message.");
                continue;
            }

            // Send a message to the device corresponding to the provided
            // registration token.
            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Successfully sent message: " + response);
                count++;
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                return false;
            }
        };

        System.out.println("Sent " + count + " messages.\n");
        return true;
    }

    private static List<String> getAllRegistrationTokens() {
        List<String> registrationTokens = new ArrayList<>();
        List<User> users;
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                users = MongoUtility.iterableToList(new User().findMany(userCollection, session));
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }

        if (users == null) return new ArrayList<>();
        
        for (User user : users) {
            String token = user.getRegistrationToken();
            if (token != null) {
                if (token.isBlank()) continue;
                registrationTokens.add(token);
            }
        }
        return registrationTokens;
    }
}
