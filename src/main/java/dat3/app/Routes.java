package dat3.app;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.net.httpserver.HttpExchange;
import dat3.app.classes.Incident;
import org.bson.Document;
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

    public static void getIncidents(HttpExchange exchange) {
        // Gets url from the request and removes the id= part of the url.
        String requestQuery = exchange.getRequestURI().getQuery();
        String incidentID = requestQuery.replace("id=", "");
        // Splits the remainder of the url into different substrings, where the first substring should always be an incident id or a "*".
        incidentID = incidentID.split("&")[0];
        Response response = new Response();
        // First we try to get the incident collection from our DB, if that doesn't work it sends back a response with response code 500.
        MongoCollection<Document> collection;
        try {
            collection = getCollection("Incidents");
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
        if(incidentID.equals("*")) {
            // Here we first create a list of mongoDB documents that contain all the incidents.
            FindIterable<Document> incidentDocumentList;
            try {
                incidentDocumentList = collection.find();
            } catch(Exception e) {
                System.out.println("If this ever triggers something is very wrong");
                e.printStackTrace();
                return;
            }

            // Afterward each incident is first converted from a mongoDB document into JSON, and then from JSON into an Incident class.
            // If the monogoDB documents can't be converted to Incident classes, a response with the error code 500 is sent back.
            ArrayList<Incident> incidentList = new ArrayList<Incident>();
            try {
                for (Document incidentDocument : incidentDocumentList) {
                    Incident incident = new Gson().fromJson(incidentDocument.toJson(), Incident.class);
                    incidentList.add(incident);
                }
            } catch(Exception e) {
                System.out.println("Problem converting incidents from DB to class");
                response.setMsg("Problem converting incidents from DB to class");
                response.setStatusCode(500);
                try {
                    response.sendResponse(exchange);
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return;
                }
                return;
            }
            ArrayList<Document> documentList = new ArrayList<Document>();
            for (Incident incident : incidentList) {
                documentList.add(incident.toDocument());
            }

            // Converts all the incidents into JSON and adds them to the response headers.
            String incidentListJSON = new Gson().toJson(documentList);
            exchange.getResponseHeaders().add("Incidents", incidentListJSON);
        } else {
            // In the case that the incidentID isn't equal to "*", the request is for a singular incident.
            // Therefor a filter is created, and we only search for incidents with matching incident IDs.
            // In the case that no incident is found with that specific ID, a response is sent with the error code 404.
            Document incidentDocument;
            try {
                Document filter = new Document();
                filter.put("ObjectID", incidentID);
                incidentDocument = collection.find(filter).first();
            } catch(Exception e) {
                System.out.println("Problem finding incident in DB");
                response.setMsg("Resource not found");
                response.setStatusCode(404);
                try {
                    response.sendResponse(exchange);
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return;
                }
                return;
            }
            // Here the incident is converted from a mongoDB document into JSON, and then from JSON into an Incident class.
            // If this isn't possible a response is sent with the error code 500.
            Incident incident;
            try {
                incident = new Gson().fromJson(incidentDocument.toJson(), Incident.class);
            } catch(Exception e) {
                System.out.println("Problem converting from DB to class");
                response.setMsg("Problem converting from DB to class");
                response.setStatusCode(500);
                try {
                    response.sendResponse(exchange);
                } catch (IOException e2) {
                    e2.printStackTrace();
                    return;
                }
                return;
            }

            // Converts the incident into JSON and adds it to the response headers.
            String incidentJSON = new Gson().toJson(incident.toDocument());
            exchange.getResponseHeaders().add("Incidents", incidentJSON);
        }
        // Finally the response code is set to 200 and the response is sent.
        response.setStatusCode(200);
        try {
            response.sendResponse(exchange);
        } catch (IOException e) {
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
    public static MongoCollection<Document> getCollection(String collectionName) throws Exception {
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        return db.getCollection(collectionName);
    }

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
