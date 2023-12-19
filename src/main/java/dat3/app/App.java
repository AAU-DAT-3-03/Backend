package dat3.app;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import dat3.app.routes.companies.CompanyRoutes;
import dat3.app.routes.incidents.IncidentRoutes;
import dat3.app.routes.notifications.NotificationRoutes;
import dat3.app.routes.services.ServiceRoutes;
import dat3.app.routes.users.UserRoutes;
import dat3.app.server.Server;
import dat3.app.testkit.TestData2;

public class App {
    public static void main(String[] args) {
        // Initialize the database with some dummy data.
        try {
            TestData2.SetupDatabase();
        } catch (Exception e) {
            return;
        }

        // Initialization of firebase application, does some stuff such that we can send notifications basically.
        // Docs say it's done like this.
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(ProjectSettings.getProjectSettings().getCertificationPath())))
                .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reads the project setttings file. Shouldn't be possible for it to be null.
        ProjectSettings projectSettings = ProjectSettings.getProjectSettings();
        if (projectSettings == null) return;

        // Initializes the server with default/specified hostname and port. 
        Server server = new Server(projectSettings.getHostname(), projectSettings.getPort());
        
        // Set up the routes:
        // Index page
        server.addGetRoute("/", Routes::index);
        
        // Authentication
        server.addGetRoute("/auth", Routes::authenticateRequest);
        server.addPostRoute("/login", Routes::loginUser);

        // Company
        server.addGetRoute("/companies", CompanyRoutes::get);
        server.addPutRoute("/companies", CompanyRoutes::put);
        server.addDeleteRoute("/companies", CompanyRoutes::delete);
        server.addPostRoute("/companies", CompanyRoutes::post);

        // Services
        server.addGetRoute("/services", ServiceRoutes::get);

        // Incidents
        server.addGetRoute("/incidents", IncidentRoutes::get);
        server.addPutRoute("/incidents", IncidentRoutes::put);
        server.addPostRoute("/merge", IncidentRoutes::merge);

        // Users
        server.addGetRoute("/users", UserRoutes::get);

        // Notifications
        server.addPostRoute("/notification", NotificationRoutes::addRegistrationToken);
        server.addGetRoute("/sendNotifications", NotificationRoutes::sendNotifications);
        
        // Finally, start the server. If an error occurs in starting the server then crash and burn. 
        try {
            server.startServer();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}