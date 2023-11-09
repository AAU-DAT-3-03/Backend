package dat3.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import dat3.app.mongo.MongoConnection;
import dat3.app.server.DBNotFound;
import dat3.app.server.Server;

public class App {
    public static void main(String[] args) {
        ProjectSettings projectSettings = ProjectSettings.getProjectSettings();

        if (projectSettings == null) return;

        Server server = new Server(projectSettings.getHostname(), projectSettings.getPort());
        server.addGetRoute("/", App::testIfItWorksIndex);
        
        try {
            server.startServer();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (DBNotFound dbe) {
            dbe.printStackTrace();
            System.out.println("Database wasn't found.");
        }
    }

    private static void testIfItWorksIndex(HttpExchange exchange) {
        try {
            String response = "Hello from Index!";

            RequestData data = RequestData.fromHttpExchange(exchange);
            List<RequestData> datas = new ArrayList<>();
            datas.add(data);
            MongoConnection connection = new MongoConnection();
            connection.connectToDb();
            connection.insert("requests", datas);

            try {
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            exchange.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

class RequestData {
    private String method = "";
    private String query = "";
    private String sender = "";
    private String[] headers = new String[0];

    public static RequestData fromHttpExchange(HttpExchange httpExchange) {
        RequestData data = new RequestData();

        data.method = httpExchange.getRequestMethod();
        data.query = httpExchange.getRequestURI().toString();
        data.sender = httpExchange.getRemoteAddress().getAddress().toString();
       
        List<String> headers = new ArrayList<>();
        httpExchange.getRequestHeaders().forEach((String s, List<String> list) -> {
            headers.add(s + ": " + list.get(0));
        });

        data.headers = headers.toArray(new String[0]);
        
        return data;
        
    }

    public String getMethod() {
        return method;
    }

    public String getQuery() {
        return query;
    }

    public String getSender() {
        return sender;
    }

    public String[] getHeaders() {
        return headers;
    }
}