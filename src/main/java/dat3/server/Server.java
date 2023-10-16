package dat3.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class Server {
    private String adress;
    private int port;
    private Map<String, CallRequest> pathToFunction;

    public Server(String adress, int port) {
        this.adress = adress;
        this.port = port;
        pathToFunction = new HashMap<>();
    }

    public void startServer() throws IOException {
        // Creates different socket and server object from adress and port to start our
        // server.
        InetAddress adresse = InetAddress.getByName(adress);
        InetSocketAddress socketAdress = new InetSocketAddress(adresse, port);
        HttpServer server = HttpServer.create(socketAdress, 0);
        server.start();
        // Creates a context function that accepts all requests
        server.createContext("/", exchange -> {
            // Creates a key from the request method and request path
            String key = exchange.getRequestMethod() + exchange.getRequestURI().getPath();
            // Checks the servers hashmap to see if there is a function connected to the
            // requests method and path. (We register these in Main)
            CallRequest function = pathToFunction.get(key);
            // If the functions equals null, we send a 404 response back and close the
            // request. (This happens when we havent registered the function in Main)
            if (function == null) {
                try {
                    exchange.sendResponseHeaders(404, 0);
                    exchange.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            // If the function is found we then run it
            function.onRequestRecieved(exchange);
        });
    }

    public void addGetRoute(String route, CallRequest function) {
        String path = "GET" + route;
        pathToFunction.put(path, function);
    }

    public void addPostRoute(String route, CallRequest function) {
        String path = "POST" + route;
        pathToFunction.put(path, function);
    }

    public void addDeleteRoute(String route, CallRequest function) {
        String path = "DELETE" + route;
        pathToFunction.put(path, function);
    }

    public void addUpdateRoute(String route, CallRequest function) {
        String path = "UPDATE" + route;
        pathToFunction.put(path, function);
    }

    @FunctionalInterface
    interface CallRequest {
        void onRequestRecieved(HttpExchange exchange);
    }
}
