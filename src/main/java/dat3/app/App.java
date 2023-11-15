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
import dat3.app.utility.MongoUtility;

public class App {
    public static void main(String[] args) {
        try {
            MongoUtility.wipeDatabaseWithMock();
        } catch (Exception e) {
            System.out.println("Exception caught when wiping and repopulating database");
            return;
        }

        ProjectSettings projectSettings = ProjectSettings.getProjectSettings();
        if (projectSettings == null) return;

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