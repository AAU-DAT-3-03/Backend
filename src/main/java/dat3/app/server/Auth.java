package dat3.app.server;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpExchange;

import static com.mongodb.client.model.Filters.eq;

import dat3.app.ProjectSettings;

public abstract class Auth {
    public static boolean auth(HttpExchange exchange) {
        // Get the auth token. If non-existent, not authorized.
        String tokenName = getAuthTokenOrNull(exchange);
        if (tokenName == null) return false;

        // Establish a connection to the database and get the database.
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoClient client = MongoClients.create(settings.getDbConnectionString());
        MongoCollection<Document> collection = client.getDatabase(settings.getDbName()).getCollection("tokens");

        // Retrieve the auth token that matches the one from the cookie.
        FindIterable<Document> tokens = collection.find(eq("name", tokenName));
        Document tokenDocument = tokens.first();

        // If the token wasn't found, they are not authorized.
        if (tokenDocument == null) return false;
        
        // Parse to a token class, and now check if the date is OK. If the date is OK, update the expiry date.
        Token token = new Gson().fromJson(tokenDocument.toJson(), Token.class);
        if (token.getExpiryDate() < System.currentTimeMillis()) {
            return false; // Token expired.
        }

        // Do something to update the token.
        

        System.out.println(new Gson().toJson(token));

        return false;
    }

    private static String getAuthTokenOrNull(HttpExchange exchange) {
        List<String> headerWithNameCookie = exchange.getRequestHeaders().get("Cookie");
        if (headerWithNameCookie == null || headerWithNameCookie.size() == 0) return null;
        String cookieString = headerWithNameCookie.get(0);
        for (String cookie : cookieString.split("; ")) {
            System.out.println(cookie);
            if (cookie.startsWith("authToken")) {
                String[] pair = cookie.split("=");
                if (pair.length != 2) continue;
                return pair[1];
            }
        }
        return null;
    }
}

class Token {
    private ObjectId _id;
    private String name;
    private Long expiryDate;
    
    public ObjectId get_id() {
        return _id;
    }
    
    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }
}
