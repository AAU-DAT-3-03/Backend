package dat3.app.server;

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
        this.pathToFunction = new HashMap<>();
    }

    public void startServer() throws IOException, DBNotFound {
        InetAddress adresse = InetAddress.getByName(adress);
        InetSocketAddress socketAdress = new InetSocketAddress(adresse, port);
        HttpServer server = HttpServer.create(socketAdress, 0);

        server.start();
        server.createContext("/", exchange -> {
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

            exchange.close();
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

    public void addPutRoute(String route, CallRequest function) {
        String get = "PUT" + route;
        pathToFunction.put(get, function);
    }

    @FunctionalInterface
    public interface CallRequest {
        void onRequestRecieved(HttpExchange exchange);
    }
}