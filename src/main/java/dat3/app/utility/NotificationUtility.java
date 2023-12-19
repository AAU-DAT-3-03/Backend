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
import dat3.app.models.Incident.IncidentPublic;
import dat3.app.testkit.TestData2;

public class NotificationUtility {
    /**
     * Generates a completely new incident with no assigned users, calls, no acknowledgement and a default priority of 4. 
     * Then sends a notification to all users with a registration token (used for notifications).
     * @return Returns true if notifications were actually sent and false if none were sent. 
     */
    public static boolean sendNotifications() {
        // Get all registration tokens across all users
        List<String> registrationTokens = getAllRegistrationTokens();

        // If there are no tokens, then quit. 
        if (registrationTokens == null || registrationTokens.size() == 0) return false;

        // Generates a new incident for use in user test.
        Incident generatedIncident = TestData2.generateIncidents(1).get(0);
        generatedIncident.setResolved(false);
        generatedIncident.setCallIds(new ArrayList<>());
        generatedIncident.setUserIds(new ArrayList<>());
        generatedIncident.setCreationDate(System.currentTimeMillis());
        generatedIncident.setAcknowledgedBy(null);
        generatedIncident.setPriority(4);
        // Remove all alarms but the first one. Inverse for loop.
        for (int i = generatedIncident.getAlarmIds().size() - 1; 0 < i; i--) {
            if (i == 0) continue;
            generatedIncident.getAlarmIds().remove(i);
        }
        
        // Insert the generated incident and retrieve it again (to generate an id as well as make it accessible)
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> incidentCollection = MongoUtility.getCollection(client, "incidents");
                InsertOneResult result = generatedIncident.insertOne(incidentCollection, session); 
                Incident filter = new Incident();
                filter.setId(result.getInsertedId().asObjectId().getValue().toHexString());
                generatedIncident = filter.findOne(incidentCollection, session);
                if (generatedIncident == null || generatedIncident.getId() == null) throw new Exception("Couldn't find generated incident in database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Convert to public for display purposes. 
        IncidentPublic incidentPublic = generatedIncident.toPublic();
        
        // Send the notifications! Ripped straight from the docs. The id of the incident is 
        // also sent as body data, such that when opened on mobile it will instantly go to
        // the actual incident screen.
        int count = 0;
        for (String token : registrationTokens) {
            Message message;
            try {
                message = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle("Incident Alarm")
                                .setBody(incidentPublic.getCompanyPublic().getName() + " #" + incidentPublic.getCaseNumber() + " - " + incidentPublic.getAlarmsPublic().get(0).getName())
                                .build())
                        .putData("type", "alarm")
                        .putData("incidentId", generatedIncident.getId())
                        .setToken(token)
                        .build();
            } catch (Exception e) {
                System.out.println("Failed while building notification message.");
                continue;
            }

            // Send a message to the device corresponding to the provided registration token.
            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Successfully sent message: " + response);
                count++;
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                return false;
            }
        };

        // Debugging output
        System.out.println("Sent " + count + " messages.\n");
        return true;
    }

    /**
     * Retrieves all users by using an empty filter and then gets the registration token from them all.
     * @return Returns a list of registration tokens.
     */
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
