package dat3.app;

import java.io.IOException;
import java.net.http.HttpClient;

import org.bson.Document;

import com.sun.net.httpserver.HttpExchange;

import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.server.Auth.AuthResponse;
import dat3.app.server.Auth.ResponseCode;

public abstract class Routes {
    /**
     * Index page. Also works as a template for future endpoints.
     * @param exchange the http exchange.
     */
    public static void index(HttpExchange exchange) {
        Response response = new Response();

        response.setMsg(ProjectSettings.getProjectSettings());
        response.setStatusCode(200);

        try {
            response.sendResponse(exchange);
        } catch (IOException e) {
            // Connection may have closed.
            e.printStackTrace();
        }
    }    

    // STJERNEMARKERING ER MULIGVIS ET ID OG MULIGVIS EN STJERNE. STJERNE ER ALLE BRUGERE.

    // Prio 1
    // Guldbæk
    // Get: /incidents?id=*  ->  giver alle incidents med en user inkluderet

    // Byriel
    // Get: /auth  ->  Giver en user

    // Rasmus
    // Get: /users?id=*  ->  giv en enkelt eller alle brugere.

    public static void registerUser(HttpExchange exchange) {
        AuthResponse result = Auth.registerUser(exchange);

        Response response = new Response();
        response.setMsg(result.getMessage());
        response.setStatusCode(ResponseCode.OK == result.getCode() ? 0 : 1);

        try {
            response.sendResponse(exchange);
        } catch (IOException e) {
        }
    }

    public static void loginUser(HttpExchange exchange) {
        AuthResponse result = Auth.login(exchange);

        Response response = new Response();
        response.setMsg(result.getMessage());
        response.setStatusCode(ResponseCode.OK == result.getCode() ? 0 : 1);

        try {
            response.sendResponse(exchange);
        } catch (IOException e) {
        }
    }

    public static void authenticateRequest(HttpExchange exchange) {
        Document user = Auth.auth(exchange);

        Response response = new Response();
        response.setMsg(user != null ? user.toJson() : null);
        response.setStatusCode(user != null ? 0 : 1);

        try {
            response.sendResponse(exchange);
        } catch (IOException e) {
        }
    }
}
