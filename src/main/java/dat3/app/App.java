package dat3.app;

import java.io.IOException;

import dat3.app.server.DBNotFound;
import dat3.app.server.Server;

public class App {
    public static void main(String[] args) {
        ProjectSettings projectSettings = ProjectSettings.getProjectSettings();

        if (projectSettings == null) return;

        Server server = new Server(projectSettings.getHostname(), projectSettings.getPort());
        
        server.addGetRoute("/", Routes::index);
        server.addGetRoute("/incidents", Routes::getIncidents);
        server.addGetRoute("/auth", Routes::authenticateRequest);
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