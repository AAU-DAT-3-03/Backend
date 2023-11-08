package dat3.app;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import dat3.app.classes.Server;

public class App {
    public static void main(String[] args) {
        ProjectSettings projectSettings = ProjectSettings.getProjectSettings();

        if (projectSettings == null) return;

        Server server = new Server(projectSettings.getHostname(), projectSettings.getPort());
        server.addGetRoute("/", App::testIfItWorksIndex);
        try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testIfItWorksIndex(HttpExchange exchange) {
        String response = "Hello from Index!";
        try {
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        exchange.close();
    }
}