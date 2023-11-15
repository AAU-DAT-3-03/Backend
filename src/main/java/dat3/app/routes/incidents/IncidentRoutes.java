package dat3.app.routes.incidents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.Incident;
import dat3.app.models.StandardModel;
import dat3.app.models.User;
import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;
import dat3.app.utility.MongoUtility;

public class IncidentRoutes {
    public static void getIncident(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            Response response = new Response();
            response.setMsg("Not authorized");
            response.setStatusCode(-1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e) {}
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setMsg("Something went wrong.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (Exception e1) {}
        }
    }

    private static Incident parseQueryString(HttpExchange exchange) {
        try {
            Document document = new Document();
            String[] pairs = exchange.getRequestURI().getQuery().split("&");
            for (String string : pairs) {
                String[] pair = string.split("=");

                if (pair[0].equals("id")) {
                    if (pair[1].equals("*")) return new Incident();
                    document.put("_id", new ObjectId(pair[1]));
                    continue;
                }

                if (pair[0].equals("")) {
                    try {
                        
                    } catch (Exception e) {

                    }
                    continue;
                }

                document.put(pair[0], pair[1]);
            }
            return new Incident().fromDocument(document);
        } catch (Exception e) {
            return null;
        }
    }
}
