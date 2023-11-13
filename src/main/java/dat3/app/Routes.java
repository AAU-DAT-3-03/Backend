package dat3.app;

import java.io.IOException;
import java.net.http.HttpClient;

import com.google.gson.Gson;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.classes.Incident;
import dat3.app.server.Response;
import org.bson.Document;

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
        String requestQuery = exchange.getRequestURI().getQuery();
        String incidentID = requestQuery.replace("id=", "");
        Response response = new Response();
        if(incidentID.equals("*")) {
            //Fetch all
        } else {
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
            //TODO update class from db with relevant information
            String incidentJSON = new Gson().toJson(incident);
            exchange.getResponseHeaders().add("Incidents", incidentJSON);
        }
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
    private static MongoCollection<Document> getCollection(String collectionName) throws Exception {
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        return db.getCollection(collectionName);
    }
}
