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

import dat3.app.models.User;

public class NotificationUtility {
    public static void sendNotifications() {
        List<String> registrationTokens = getAllRegistrationTokens();

        if (registrationTokens == null || registrationTokens.size() == 0) return;


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
            }
        };
        System.out.println("Sent " + count + " messages.");
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
