package dat3.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server("localhost", 31415);
        server.addGetRoute("/", exchange -> {
            String response = "Hello from Index!";
            try {
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            server.startServer();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}