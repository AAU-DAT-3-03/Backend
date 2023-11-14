package dat3.app.routes.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.ProjectSettings;
import dat3.app.models.User;
import dat3.app.models.User.UserBuilder;
import dat3.app.server.Response;

public abstract class UserRoutes {
    public static void getUser(HttpExchange exchange) {
        String id = getIdOrNull(exchange);
        if (id == null) {
            // invalid
            Response response = new Response();
            response.setMsg("Must specify an id to request, or all by '*'.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        MongoCollection<Document> userCollection;
        ClientSession session;
        UserBuilder userBuilder = new UserBuilder();

        try {
            ProjectSettings settings =  ProjectSettings.getProjectSettings();
            MongoClient client = MongoClients.create(settings.getDbConnectionString());
            MongoDatabase db = client.getDatabase(settings.getDbName());

            userCollection = db.getCollection("users");
            session = client.startSession();
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setMsg("Something went wrong when getting user collection");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {
            }
            return;
        }

        User query = id.equals("*") ? userBuilder.getUser() : userBuilder.setId(new ObjectId(id)).getUser();
        try {
            MongoIterable<User> users = query.findMany(userCollection, session);
            List<Document> userDocs = new ArrayList<>();
            for (User user : users) {
                userDocs.add(user.toDocument());
            }
            Response response = new Response();
            response.setMsg(new Document("users", userDocs).toJson());
            response.setStatusCode(0);
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            Response response = new Response();
            response.setMsg("Something went wrong when searching for users");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        session.close();
    }

    private static String getIdOrNull(HttpExchange exchange) {
        String[] pairs = exchange.getRequestURI().getQuery().split("&");
        for (String pairNotSplit : pairs) {
            String[] pair = pairNotSplit.split("=");
            if (pair.length != 2) {
                continue;
            }
            System.out.println(pair[0] + "=" + pair[1]);
            if (pair[0].equals("id")) {
                return pair[1];
            }
        }
        return null;
    }
}
