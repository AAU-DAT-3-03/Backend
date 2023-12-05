package dat3.app.models;

import static org.junit.jupiter.api.Assertions.*;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import dat3.app.utility.MongoUtility;

public class UserTest {
    @Test
    void TestUserInsertion() {
        User user = new User();
        user.setEmail("madshbyriel@gmail.com");
        user.setName("mads");
        user.setOnCall(false);
        user.setOnDuty(false);
        user.setPassword("1234567890");
        user.setPhoneNumber("1234567890");

        User recUser = null;
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession clientSession = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                user.insertOne(userCollection, clientSession);

                User filter = new User();
                filter.setEmail(user.getEmail());
                filter.setPassword(user.getPassword());

                recUser = filter.findOne(userCollection, clientSession);

                userCollection.drop();
            }
        } catch (Exception e) {
        }

        assertTrue(User.UserEquals(user, recUser));
    }
}