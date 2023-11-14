package dat3.app.server;

import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.ProjectSettings;
import dat3.app.models.AuthToken;
import dat3.app.models.User;
import dat3.app.models.AuthToken.AuthTokenBuilder;
import dat3.app.models.User.UserBuilder;

public abstract class Auth {
    /**
     * Authorizes an http exchange by checking the auth token attached against the database.
     * @param exchange the http exchange that needs to be authorized.
     * @return a document containing only the '_id' of a user.
     */
    public static Document auth(HttpExchange exchange) {
        MongoCollection<Document> userCollection;
        MongoCollection<Document> tokenCollection;
        ClientSession session;
        UserBuilder userBuilder = new UserBuilder();
        AuthTokenBuilder tokenBuilder = new AuthTokenBuilder();

        try {
            ProjectSettings settings =  ProjectSettings.getProjectSettings();
            MongoClient client = MongoClients.create(settings.getDbConnectionString());
            MongoDatabase db = client.getDatabase(settings.getDbName());

            userCollection = db.getCollection("users");
            tokenCollection = db.getCollection("tokens");
            session = client.startSession();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String tokenName = getAuthTokenFromCookies(exchange);
        if (tokenName == null) return null;

        AuthToken token;
        try {
            token = tokenBuilder
                .setName(tokenName).getToken().findOne(tokenCollection, session);
            if (token == null || token.isExpired()) {
                exchange.getResponseHeaders().add("Set-Cookie", "authToken=placeholder; Max-Age=0");
                return null;
            }
            tokenBuilder.setExpiryDate(AuthToken.getNewExpirationDate()).getToken().updateOne(tokenCollection, session, tokenBuilder.setId(token.getId()).getToken());
            token = tokenBuilder.setId(token.getId()).getToken().findOne(tokenCollection, session);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        User user;
        try {
            user = userBuilder.setId(token.getUserId()).getUser().findOne(userCollection, session);
            if (user == null) {
                tokenBuilder.setUserId(token.getUserId()).getToken().deleteMany(tokenCollection, session);
            }
            return user.toDocument();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Logs a user in. This is done by reading the body of the http exchange, looking for credentials. If the user 
     * is successfully logged in, an auth token will be attached to the exchange. 
     * @param exchange the http exchange in which the log in credentials are.
     * @return an auth response containing a message and an auth code. Successful is OK.
     */
    public static AuthResponse login(HttpExchange exchange) {
        MongoCollection<Document> userCollection;
        MongoCollection<Document> tokenCollection;
        ClientSession session;
        UserBuilder userBuilder = new UserBuilder();
        AuthTokenBuilder tokenBuilder = new AuthTokenBuilder();

        try {
            ProjectSettings settings =  ProjectSettings.getProjectSettings();
            MongoClient client = MongoClients.create(settings.getDbConnectionString());
            MongoDatabase db = client.getDatabase(settings.getDbName());

            userCollection = db.getCollection("users");
            tokenCollection = db.getCollection("tokens");
            session = client.startSession();
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when getting collections from database");
        }

        Credentials credentials;
        try {
            credentials = new Gson().fromJson(readBody(exchange, 1000), Credentials.class);
            if (credentials == null) return new AuthResponse(ResponseCode.InvalidCredentials, "Body couldn't be parsed to credentials.");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.InvalidBody, "Exception was thrown when reading body.");
        }

        User user;
        try {
            user = userBuilder
                .setEmail(credentials.getEmail())
                .setPassword(credentials.getPassword())
                .getUser().findOne(userCollection, session);
            if (user == null) return new AuthResponse(ResponseCode.InvalidCredentials, "No user with those credentials exist.");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when finding user by credentials.");
        }

        AuthToken token;
        try {
            token = tokenBuilder
                .setUserId(user.getId())
                .getToken().findOne(tokenCollection, session);
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when searching for available tokens.");
        }

        try {
            if (token == null) {
                token = tokenBuilder
                    .setExpiryDate(AuthToken.getNewExpirationDate())
                    .setName(AuthToken.createUniqueName(exchange))
                    .setUserId(user.getId())
                    .getToken();
                token.insertOne(tokenCollection, session);
            } else {
                tokenBuilder.setExpiryDate(AuthToken.getNewExpirationDate()).getToken()
                    .updateOne(tokenCollection, session, tokenBuilder.setId(token.getId()).getToken());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when updating a users token.");
        }

        exchange.getResponseHeaders().add("Set-Cookie", "authToken=" + token.getName());
        return new AuthResponse(ResponseCode.OK, "Successfully logged user in.");
    }

    public static AuthResponse registerUser(HttpExchange exchange) {
        MongoCollection<Document> userCollection;
        MongoCollection<Document> tokenCollection;
        ClientSession session;
        UserBuilder userBuilder = new UserBuilder();
        AuthTokenBuilder tokenBuilder = new AuthTokenBuilder();

        try {
            ProjectSettings settings =  ProjectSettings.getProjectSettings();
            MongoClient client = MongoClients.create(settings.getDbConnectionString());
            MongoDatabase db = client.getDatabase(settings.getDbName());

            userCollection = db.getCollection("users");
            tokenCollection = db.getCollection("tokens");
            session = client.startSession();
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when getting collections from database");
        }


        Credentials credentials;
        try {
            credentials = new Gson().fromJson(readBody(exchange, 1000), Credentials.class);
            if (credentials == null) return new AuthResponse(ResponseCode.InvalidBody, "Body too big");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.InvalidBody, "Exception was thrown when reading response json.");
        }

        try {
            User user = userBuilder.setEmail(credentials.getEmail()).getUser().findOne(userCollection, session);
            if (user != null) new AuthResponse(ResponseCode.InvalidCredentials, "User with that email already exists.");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when searching for user.");
        }

        User user = userBuilder
            .setEmail(credentials.getEmail())
            .setPassword(credentials.getPassword())
            .setName("placeholder")
            .setOnCall(false)
            .setOnDuty(false)
            .getUser();

        try {
            user.insertOne(userCollection, session);
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when inserting new user.");
        }

        try {
            user = user.findOne(userCollection, session);
            if (user == null) return new AuthResponse(ResponseCode.DatabaseError, "User somehow managed to be null despite insertion one moment ago.");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when immediately retrieving inserted user.");
        }

        AuthToken token;
        try {
            token = tokenBuilder
                .setExpiryDate(AuthToken.getNewExpirationDate())
                .setName(AuthToken.createUniqueName(exchange))
                .setUserId(user.getId())
                .getToken();
            token.insertOne(tokenCollection, session);
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Exception was thrown when inserting new AuthToken.");
        }

        exchange.getResponseHeaders().add("Set-Cookie", "authToken=" + token.getName());
        return new AuthResponse(ResponseCode.OK, "Succesfully created user.");
    }

    private static String readBody(HttpExchange exchange, int maxSize) throws Exception {
        int contentLength = Integer.parseInt(exchange.getRequestHeaders().get("Content-Length").get(0));
        if (maxSize < contentLength) throw new Exception("Content length is bigger than allowed size.");

        byte[] buffer = new byte[contentLength];
        int read;
        int totalRead = 0;
        while (totalRead < contentLength) {
            read = exchange.getRequestBody().read(buffer, totalRead, contentLength - totalRead);
            totalRead += read;
        }

        return new String(buffer);
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