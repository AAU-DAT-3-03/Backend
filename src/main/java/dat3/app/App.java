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
import dat3.app.routes.incidents.IncidentRoutes;
import dat3.app.routes.services.ServiceRoutes;
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
        // server.addGetRoute("/incidents", Routes::getIncidents);
        server.addGetRoute("/auth", Routes::authenticateRequest);

        server.addPostRoute("/register", Routes::registerUser);
        server.addPostRoute("/login", Routes::loginUser);

        // Company
        server.addGetRoute("/companies", CompanyRoutes::getCompanies);
        server.addPutRoute("/companies", CompanyRoutes::putCompanies);
        server.addDeleteRoute("/companies", CompanyRoutes::deleteCompanies);
        server.addPostRoute("/companies", CompanyRoutes::postCompanies);

        // Services
        server.addGetRoute("/services", ServiceRoutes::getService);
        server.addDeleteRoute("/services", ServiceRoutes::deleteService);
        server.addPutRoute("/services", ServiceRoutes::putService);
        server.addPostRoute("/services", ServiceRoutes::postService);

        // Incidents
        server.addGetRoute("/incidents", IncidentRoutes::getIncident);
        server.addDeleteRoute("/incidents", IncidentRoutes::deleteIncident);
        server.addPutRoute("/incidents", IncidentRoutes::putIncident);
        server.addPostRoute("/incidents", IncidentRoutes::postIncident);

        // Users
        server.addGetRoute("/users", UserRoutes::getUser);
        server.addPutRoute("/users", UserRoutes::updateUser);
        server.addDeleteRoute("/users", UserRoutes::deleteUser);
        

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