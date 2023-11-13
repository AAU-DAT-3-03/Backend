package dat3.app;

import java.io.IOException;
import java.net.http.HttpClient;

import com.sun.net.httpserver.HttpExchange;

import dat3.app.server.Response;

public abstract class Routes {
    /**
     * Index page. Also works as a template for future endpoints.
     * @param exchange the http exchange.
     */
    public static void index(HttpExchange exchange) {
        Response response = new Response();

        response.setMsg("Hello from index page.");
        response.setStatusCode(200);

        try {
            response.sendResponse(exchange);
        } catch (IOException e) {
            // Connection may have closed.
            e.printStackTrace();
        }
    }    
}
