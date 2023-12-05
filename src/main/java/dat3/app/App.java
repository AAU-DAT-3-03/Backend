package dat3.app;

import java.io.IOException;

import dat3.app.routes.companies.CompanyRoutes;
import dat3.app.routes.incidents.IncidentRoutes;
import dat3.app.routes.services.ServiceRoutes;
import dat3.app.routes.users.UserRoutes;
import dat3.app.server.DBNotFound;
import dat3.app.server.Server;
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
        server.addGetRoute("/services", ServiceRoutes::get);
        server.addDeleteRoute("/services", ServiceRoutes::delete);
        server.addPutRoute("/services", ServiceRoutes::put);
        server.addPostRoute("/services", ServiceRoutes::post);

        // Incidents
        server.addGetRoute("/incidents", IncidentRoutes::get);
        server.addDeleteRoute("/incidents", IncidentRoutes::delete);
        server.addPutRoute("/incidents", IncidentRoutes::put);
        server.addPostRoute("/incidents", IncidentRoutes::post);
        server.addPostRoute("/merge", IncidentRoutes::merge);

        // Users
        server.addGetRoute("/users", UserRoutes::get);
        server.addPutRoute("/users", UserRoutes::put);
        server.addDeleteRoute("/users", UserRoutes::delete);

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