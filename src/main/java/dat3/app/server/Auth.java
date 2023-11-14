package dat3.app.server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.ProjectSettings;
import dat3.app.models.AuthToken;
import dat3.app.models.AuthToken.AuthTokenBuilder;

public abstract class Auth {
    /**
     * Authorizes an http exchange by checking the auth token attached against the database.
     * @param exchange the http exchange that needs to be authorized.
     * @return a document containing only the '_id' of a user.
     */
    public static Document auth(HttpExchange exchange) {
        // First get the collections.
        MongoCollection<Document> tokenCollection;
        ClientSession session;
        try {
            ProjectSettings settings = ProjectSettings.getProjectSettings();
            MongoClient client = MongoClients.create(settings.getDbConnectionString());
            MongoDatabase db = client.getDatabase(settings.getDbName());

            tokenCollection = db.getCollection("tokens");
            session = client.startSession();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Couldn't get collections.");
            return null;
        }

        // Get the auth token. If non-existent, not authorized.
        String tokenName = getAuthTokenFromCookies(exchange);
        if (tokenName == null) return null;

        // Check if the token exists.
        AuthToken token = new AuthTokenBuilder().setName(tokenName).getToken();
        try {
            token = token.findOne(tokenCollection, session);
            if (token == null) return null;
        } catch (Exception e) {
            return null;
        }

        // Determine if it is expired.
        try {
            if (token.getExpiryDate() < System.currentTimeMillis()) return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // Now, update the expiration date.
        try {
            token.updateToken(tokenCollection, session);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something went wrong when updating auth token.");
        }

        // Return the user id. 
        session.close();
        return new Document("_id", token.getUserId());
    }

    /**
     * Logs a user in. This is done by reading the body of the http exchange, looking for credentials. If the user 
     * is successfully logged in, an auth token will be attached to the exchange. 
     * @param exchange the http exchange in which the log in credentials are.
     * @return an auth response containing a message and an auth code. Successful is OK.
     */
    public static AuthResponse login(HttpExchange exchange) {
        // Get connection to database.
        MongoCollection<Document> userCollection;
        MongoCollection<Document> tokenCollection;
        try {
            userCollection = getCollection("users");
            tokenCollection = getCollection("tokens");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Collection(s) not found.");
        }

        // Get the credentials from post body.
        Credentials credentials;
        try {
            int length = Integer.parseInt(exchange.getRequestHeaders().getFirst("Content-Length"));
            if (length > 1000) return new AuthResponse(ResponseCode.InvalidBody, "Post body must not be over 1000 bytes.");
            byte[] buffer = new byte[length];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = (byte)exchange.getRequestBody().read();
            }
            Gson gson = new Gson();
            String body = new String(buffer);
            credentials = gson.fromJson(body, Credentials.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.InvalidBody, "Invalid body from post.");
        }

        // Get the user, if it can be found.
        Document user;
        try {
            Document query = new Document();
            query.put("email", credentials.getEmail());
            query.put("password", credentials.getPassword());
            user = userCollection.find(query).first();
            if (user == null) return new AuthResponse(ResponseCode.InvalidCredentials, "Credentials did not match a user.");
        } catch (Exception e) {
            System.out.println("No user was found with given credentials.");
            return new AuthResponse(ResponseCode.InvalidCredentials, "Credentials did not match a user.");
        }

        // Check for a token on the user.
        Document token;
        try {
            Document query = new Document();
            query.put("userId", user.get("_id"));
            token = tokenCollection.find(query).first();
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception thrown when trying to find user.");
        }

        // Get the name of the token to send to user.
        String tokenName;
        if (token != null) {
            try {
                Document toUpdate = new Document();
                toUpdate.put("expiryDate", System.currentTimeMillis() + 60000 * 24 * 365);

                Document filter = new Document();
                filter.put("_id", token.get("_id"));

                Long expiryDate = token.getLong("expiryDate");
                if (expiryDate < System.currentTimeMillis()) {
                    // Create a new name for a token.
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    String name = exchange.getRemoteAddress().getAddress().toString() + Long.toString(System.currentTimeMillis());
                    byte[] hash = digest.digest(name.getBytes());
                    tokenName = bytesToHex(hash);
                    toUpdate.put("name", tokenName);
                } else {
                    tokenName = token.getString("name");
                }

                Document operation = new Document("$set", toUpdate);
                Document updateResult = tokenCollection.findOneAndUpdate(filter, operation);
                if (updateResult == null) throw new Exception("Update was null. No changes were made.");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception caught when updating existing token.");
                return new AuthResponse(ResponseCode.DatabaseError, "Exception caught when trying to update an existing token.");
            }
        } else { // Create a new token!
            try {
                Document newToken = new Document();

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String name = exchange.getRemoteAddress().getAddress().toString() + Long.toString(System.currentTimeMillis());
                byte[] hash = digest.digest(name.getBytes());
                tokenName = bytesToHex(hash);

                newToken.put("name", tokenName); 
                newToken.put("expiryDate", System.currentTimeMillis() + 60000 * 24 * 365);
                newToken.put("userId", user.get("_id"));

                tokenCollection.insertOne(newToken);
            } catch (Exception e) {
                System.out.println("Exception caught when creating new token!");
                return new AuthResponse(ResponseCode.DatabaseError, "Exception caught when creating a new token.");
            }
        }

        // Set auth token in browser. User is now authorized.
        exchange.getResponseHeaders().add("Set-Cookie", "authToken=" + tokenName);
        return new AuthResponse(ResponseCode.OK, "Successfully found and set auth token cookie.");
    }

    public static AuthResponse registerUser(HttpExchange exchange) {
        // Slagplan:
        // Read user information from the post body. 
        // Check if a user with that email already exists.
        // If not, create the user.
        // Now create a token for the user.
        // Set the token and return with a success.

        MongoCollection<Document> userCollection;
        MongoCollection<Document> tokenCollection;
        try {
            userCollection = getCollection("users");
            tokenCollection = getCollection("tokens");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when getting collections from database");
        }

        Credentials credentials;
        try {
            int contentLength = Integer.parseInt(exchange.getRequestHeaders().get("Content-Length").get(0));
            if (1000 < contentLength) return new AuthResponse(ResponseCode.InvalidBody, "Post body too long.");
            byte[] buffer = new byte[contentLength];
            
            int read;
            int totalRead = 0;
            while (totalRead < contentLength) {
                read = exchange.getRequestBody().read(buffer, totalRead, contentLength - totalRead);
                totalRead += read;
            }
            
            String body = new String(buffer);
            credentials = new Gson().fromJson(body, Credentials.class);
            if (credentials == null) return new AuthResponse(ResponseCode.InvalidBody, "Body couldn't be parsed to credentials.");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.InvalidBody, "Exception was thrown when reading response json.");
        }

        // Check if a user exists, and if one does, return with an error.
        try {
            Document filter = new Document("email", credentials.getEmail());
            Document result = userCollection.find(filter).first();
            if (result != null) return new AuthResponse(ResponseCode.InvalidCredentials, "Email is taken.");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when checking for existing users.");
        }

        // Insert this new user!
        Object id;
        try {
            Document userToInsert = new Document();
            userToInsert.put("email", credentials.getEmail());
            userToInsert.put("password", credentials.getPassword());
            InsertOneResult result = userCollection.insertOne(userToInsert);
            id = result.getInsertedId();
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception thrown when inserting new user.");
        }

        // Create an auth token for the new user.
        try {
            Document token = new Document();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String name = exchange.getRemoteAddress().getAddress().toString() + Long.toString(System.currentTimeMillis());
            byte[] hash = digest.digest(name.getBytes());
            name = bytesToHex(hash);

            token.put("name", name);
            token.put("expiryDate", System.currentTimeMillis() + 60000 * 24 * 365);
            token.put("userId", id);

            tokenCollection.insertOne(token);

            exchange.getResponseHeaders().set("Set-Cookie", "authToken=" + name);
            return new AuthResponse(ResponseCode.OK, "Successfully set auth token.");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was caught when inserting new auth token.");
        }
    }

    /**
     * Gets the auth token name from the cookies sent in the request. If non-existent, returns null.
     * @param exchange the http exchange.
     * @return the auth token name, or null if non-existent.
     */
    private static String getAuthTokenFromCookies(HttpExchange exchange) {
        List<String> headerWithNameCookie = exchange.getRequestHeaders().get("Cookie");
        if (headerWithNameCookie == null || headerWithNameCookie.size() == 0) return null;
        String cookieString = headerWithNameCookie.get(0);
        for (String cookie : cookieString.split("; ")) {
            if (cookie.startsWith("authToken")) {
                String[] pair = cookie.split("=");
                if (pair.length != 2) continue;
                return pair[1];
            }
        }
        return null;
    }

    /**
     * Converts an array of bytes to a hex string.
     * @param bytes the bytes to convert to a string.
     * @return the final hex string.
     */
    private static String bytesToHex(byte[] bytes) {
        BigInteger bigInt = new BigInteger(1, bytes);
        String hexString = bigInt.toString(16);
        
        // Ensure that the hex string has leading zeros
        int paddingLength = (bytes.length * 2) - hexString.length();
        if (paddingLength > 0) {
            return "0".repeat(paddingLength) + hexString;
        } else {
            return hexString;
        }
    }

    /**
     * Connects to the database given by the connection string and database name in project settings, and returns the request collection. 
     * @param collectionName the name of the collection to return.
     * @return a collection of documents.
     * @throws Exception if something goes wrong when connecting. 
     */
    private static MongoCollection<Document> getCollection(String collectionName) throws Exception {
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        return db.getCollection(collectionName);
    }

    public static enum ResponseCode {
        OK,
        DatabaseError,
        InvalidCredentials,
        InvalidBody,
        UnknownError,
    }

    public static class AuthResponse {
        private ResponseCode code;
        private String message;

        public AuthResponse(ResponseCode code, String msg) {
            this.code = code;
            this.message = msg;
        }

        public ResponseCode getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}

class Credentials {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}