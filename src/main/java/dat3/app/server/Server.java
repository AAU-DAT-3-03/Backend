package dat3.app.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import dat3.app.mongo.MongoConnection;

public class Server {
    private String adress;
    private int port;
    private MongoConnection connection;

    private Map<String, CallRequest> pathToFunction;

    public Server(String adress, int port) {
        this.adress = adress;
        this.port = port;
    }

    public void startServer() throws IOException, DBNotFound {
        InetAddress adresse = InetAddress.getByName(adress);
        InetSocketAddress socketAdress = new InetSocketAddress(adresse, port);
        HttpServer server = HttpServer.create(socketAdress, 0);

        connection = new MongoConnection();
        if (!connection.connectToDb()) {
            throw new DBNotFound();
        }

        server.start();
        server.createContext("/", exchange -> {
            if (connection != null) {
                List<Object> objects = new ArrayList<>();
                objects.add(new RequestType(exchange));
                connection.insert("requests", objects);
            }

            String key = exchange.getRequestMethod() + exchange.getRequestURI().getPath();
            CallRequest function = pathToFunction.get(key);
            if (function == null) {
                try {
                    exchange.sendResponseHeaders(404, 0);
                    exchange.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            function.onRequestRecieved(exchange);
        });
    }

    public void addGetRoute(String route, CallRequest function) {
        String get = "GET" + route;
        pathToFunction.put(get, function);
    }

    public void addPostRoute(String route, CallRequest function) {
        String get = "POST" + route;
        pathToFunction.put(get, function);
    }

    public void addDeleteRoute(String route, CallRequest function) {
        String get = "DELETE" + route;
        pathToFunction.put(get, function);
    }

    public void addUpdateRoute(String route, CallRequest function) {
        String get = "UPDATE" + route;
        pathToFunction.put(get, function);
    }

    @FunctionalInterface
    public interface CallRequest {
        void onRequestRecieved(HttpExchange exchange);
    }
}

class RequestType {
    private String method;
    private String remoteAddress;
    private String uri;
    
    public String getMethod() {
        return method;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getUri() {
        return uri;
    }

    private String[] headers;

    public RequestType(HttpExchange exchange) {
        this.method = exchange.getRequestMethod();
        this.remoteAddress = exchange.getRemoteAddress().getAddress().toString();
        this.uri = exchange.getRequestURI().toString();

        headers = new String[exchange.getRequestHeaders().keySet().size()];
        int i = 0;
        for (String key : exchange.getRequestHeaders().keySet()) {
            headers[i++] = key + " : " + exchange.getRequestHeaders().getFirst(key);
        }
    }
}