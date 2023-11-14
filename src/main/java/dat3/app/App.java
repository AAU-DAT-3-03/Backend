package dat3.app;

import java.io.IOException;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dat3.app.models.User;
import dat3.app.routes.users.UserRoutes;
import dat3.app.server.DBNotFound;
import dat3.app.server.Server;
import dat3.app.testkit.TestData;

public class App {
    public static void main(String[] args) {
        ProjectSettings projectSettings = ProjectSettings.getProjectSettings();
        if (projectSettings == null) return;

        MongoClient client = MongoClients.create(projectSettings.getDbConnectionString());
        MongoDatabase db = client.getDatabase(projectSettings.getDbName());
        MongoCollection<Document> userCollection = db.getCollection("users");
        ClientSession session = client.startSession();
        
        db.drop(session);

        for (User user : TestData.randomValidUsers()) {
            try {
                user.insertOne(userCollection, session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Server server = new Server(projectSettings.getHostname(), projectSettings.getPort());
        
        server.addGetRoute("/", Routes::index);
        server.addGetRoute("/incidents", Routes::getIncidents);
        server.addGetRoute("/auth", Routes::authenticateRequest);
        server.addGetRoute("/users", UserRoutes::getUser);

        server.addPostRoute("/register", Routes::registerUser);
        server.addPostRoute("/login", Routes::loginUser);

        try {
            server.startServer();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (DBNotFound dbe) {
            dbe.printStackTrace();
            System.out.println("Database wasn't found.");
        }
    }
}