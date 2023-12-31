package dat3.app;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import dat3.app.models.User;

import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.server.Auth.AuthResponse;
import dat3.app.server.Auth.ResponseCode;

public abstract class Routes {
    /**
     * Useless index page. Simply displays the projectsettings for the sake of testing the ProjectSettings class when deployed on the server.  
     * @param exchange The HttpExchange that is tied to the current client communication
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
    
    /**
     * POST /login. Creates/updates an auth token. 
     * @param exchange The HttpExchange that is tied to the current client communication
     */
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

    /**
     * Endpoint for authentication. Returns the user that is authenticated. GET /auth.
     * @param exchange The HttpExchange that is tied to the current client communication
     */
    public static void authenticateRequest(HttpExchange exchange) {
        User user = Auth.auth(exchange);

        Response response = new Response();
        response.setMsg(user);
        response.setStatusCode(user != null ? 0 : 1);

        try {
            response.sendResponse(exchange);
        } catch (IOException e) {
        }
    }
}
