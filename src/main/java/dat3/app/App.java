package dat3.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.classes.Server;

public class App {
    public static void main(String[] args) {
        ServerInfo serverInfo;
        try {
            Gson gson = new Gson();
            serverInfo = gson.fromJson(
                    getProjectSettingsReader(), // Arg1 = filestream 
                    ServerInfo.class // Arg2 = class info
                );
        } catch (FileNotFoundException e) {
            System.out.println("'./projectsettings.json' was not found. Make sure to include it in the project root directory.");
            return;
        } catch (JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
            System.out.println("Failed parsing the projectsettings.");
            return;
        }

        Server server = new Server(serverInfo.getHostname(), serverInfo.getPort());
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

    public static Reader getProjectSettingsReader() throws FileNotFoundException {
        return new FileReader(new File(System.getProperty("user.dir") + "/projectsettings.json"));
    }
}

class ServerInfo {
    private String hostname;
    private int port;

    public String getHostname() {
        return hostname;
    }
    public int getPort() {
        return port;
    }
}