package dat3.app;

import java.io.IOException;
import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dat3.app.models.Incident;
import dat3.app.models.User;
import dat3.app.routes.companies.CompanyRoutes;
import dat3.app.routes.companies.CompanyRoutes;
import dat3.app.routes.incidents.IncidentRoutes2;
import dat3.app.routes.services.ServiceRoutes;
import dat3.app.routes.services.ServiceRoutes2;
import dat3.app.routes.users.UserRoutes;
import dat3.app.routes.users.UserRoutes2;
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
        
        // Index page
        server.addGetRoute("/", Routes::index);
        
        // Authentication
        server.addGetRoute("/auth", Routes::authenticateRequest);
        server.addPostRoute("/register", Routes::registerUser);
        server.addPostRoute("/login", Routes::loginUser);

        // Company
        server.addGetRoute("/companies", CompanyRoutes::get);
        server.addPutRoute("/companies", CompanyRoutes::put);
        server.addDeleteRoute("/companies", CompanyRoutes::delete);
        server.addPostRoute("/companies", CompanyRoutes::post);

        // Services
        server.addGetRoute("/services", ServiceRoutes2::get);
        server.addDeleteRoute("/services", ServiceRoutes2::delete);
        server.addPutRoute("/services", ServiceRoutes2::put);
        server.addPostRoute("/services", ServiceRoutes2::post);

        // Incidents
        server.addGetRoute("/incidents", IncidentRoutes2::get);
        server.addDeleteRoute("/incidents", IncidentRoutes2::delete);
        server.addPutRoute("/incidents", IncidentRoutes2::put);
        server.addPostRoute("/incidents", IncidentRoutes2::post);

        // Users
        server.addGetRoute("/users", UserRoutes2::get);
        server.addPutRoute("/users", UserRoutes2::put);
        server.addDeleteRoute("/users", UserRoutes2::delete);

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