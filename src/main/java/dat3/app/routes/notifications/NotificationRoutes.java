package dat3.app.routes.notifications;

import java.io.IOException;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.User;
import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;
import dat3.app.utility.MongoUtility;
import dat3.app.utility.NotificationUtility;

public class NotificationRoutes {
    public static void addRegistrationToken(HttpExchange exchange) {
        User user = Auth.auth(exchange);
        
        if (user == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        RegistationTokenJSON jsonClass;
        try {
            jsonClass = ExchangeUtility.parseJsonBody(exchange, 1000, RegistationTokenJSON.class);
        } catch (Exception e) {
            e.printStackTrace();
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        user.setRegistrationToken(jsonClass.registrationToken);
        User filter = new User();
        filter.setId(user.getId());

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                
                UpdateResult result = user.updateOne(userCollection, session, filter);
                if (!result.wasAcknowledged()) {
                    throw new Exception("Update query wasn't acknowledged.");
                }

                Response response = new Response();
                response.setMsg("Successfully set registration token.");
                response.setStatusCode(0);                
                response.sendResponse(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }
    }

    public static void sendNotifications(HttpExchange exchange) {
        NotificationUtility.sendNotifications();

        Response response = new Response();
        response.setMsg("Notifications sent.");
        response.setStatusCode(0);
        try {
            response.sendResponse(exchange);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class RegistationTokenJSON {
        private String registrationToken;

        public String getRegistrationToken() {
            return registrationToken;
        }

        public void setRegistrationToken(String registrationToken) {
            this.registrationToken = registrationToken;
        }
    }
}
