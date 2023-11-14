package dat3.app;

import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.mongodb.client.*;
import com.sun.net.httpserver.HttpExchange;
import dat3.app.models.Incident;
import org.bson.Document;
import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.server.Auth.AuthResponse;
import dat3.app.server.Auth.ResponseCode;
import dat3.app.models.Incident.IncidentBuilder;
import org.bson.types.ObjectId;

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

    public static void getIncidents(HttpExchange exchange) {
        // Gets url from the request and removes the id= part of the url.
        String requestQuery = exchange.getRequestURI().getQuery();
        String incidentID = requestQuery.replace("id=", "");
        // Splits the remainder of the url into different substrings, where the first substring should always be an incident id or a "*".
        incidentID = incidentID.split("&")[0];
        // First we try to get the incident collection from our DB, if that doesn't work it sends back a response with response code 500.
        Response response = new Response();
        ClientSession session;
        MongoCollection<Document> collection;
        try {
            ProjectSettings settings =  ProjectSettings.getProjectSettings();
            MongoClient client = MongoClients.create(settings.getDbConnectionString());
            MongoDatabase db = client.getDatabase(settings.getDbName());
            collection = db.getCollection("Incidents");
            session = client.startSession();
        } catch (Exception e) {
            System.out.println("Problem connecting to DB");
            response.setMsg("Problem connecting to DB");
            response.setStatusCode(500);
            try {
                response.sendResponse(exchange);
            } catch (IOException e2) {
                e2.printStackTrace();
                return;
            }
            return;
        }

        // If the incidentID is equal to a "*" then the request is for all incidents.
        IncidentBuilder incidentBuilder = new IncidentBuilder();
        MongoIterable<Incident> incidentList;
        Incident query = incidentID.equals("*") ? incidentBuilder.getIncident() : incidentBuilder.setId(new ObjectId(incidentID)).getIncident();
        try {
            incidentList = query.findMany(collection, session);
        } catch(Exception e) {
            System.out.println("Something went wrong finding user in DB");
            response.setMsg("Something went wrong finding user in DB");
            response.setStatusCode(404);
            try {
                response.sendResponse(exchange);
            } catch (IOException e2) {
                e2.printStackTrace();
                return;
            }
            return;
        }
        ArrayList<Document> documentList = new ArrayList<>();
        for (Incident incident : incidentList) {
            documentList.add(incident.toDocumentFormatted());
        }
        // Converts all the incidents into JSON and adds them to the response headers.
        String incidentListJSON = new Document("incidents", documentList).toJson();
        response.setMsg(incidentListJSON);
        response.setStatusCode(200);
        try {
            response.sendResponse(exchange);
        } catch(Exception e) {
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
