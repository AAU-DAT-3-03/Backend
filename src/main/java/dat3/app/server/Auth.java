package dat3.app.server;

import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.AuthToken;
import dat3.app.models.User;
import dat3.app.models.AuthToken.AuthTokenBuilder;
import dat3.app.models.User.UserBuilder;
import dat3.app.utility.MongoUtility;

public abstract class Auth {
    /**
     * Authorizes an http exchange by checking the auth token attached against the database.
     * @param exchange the http exchange that needs to be authorized.
     * @return a document containing only the '_id' of a user.
     */
    public static User auth(HttpExchange exchange) {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                MongoCollection<Document> tokenCollection = MongoUtility.getCollection(client, "tokens");
                String tokenName = getAuthTokenFromCookies(exchange);
                if (tokenName == null) return null;

                UserBuilder userBuilder = new UserBuilder();
                AuthTokenBuilder tokenBuilder = new AuthTokenBuilder();

                AuthToken token = tokenBuilder.setName(tokenName).getToken().findOne(tokenCollection, session);
                if (token == null) return null;
                if (token.isExpired()) {
                    exchange.getResponseHeaders().add("Set-Cookie", "authToken=nothing; Max-Age:0");
                    return null;
                }
                
                tokenBuilder.setExpiryDate(AuthToken.getNewExpirationDate()).getToken().updateMany(tokenCollection, session, token);
                return userBuilder.setId(token.getUserId()).getUser().findOne(userCollection, session);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
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
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                MongoCollection<Document> tokenCollection = MongoUtility.getCollection(client, "tokens");

                Credentials credentials;
                try {
                    credentials = parseJsonBody(exchange, 1000, Credentials.class);
                    if (credentials == null) throw new Exception("Credentials were null.");
                } catch (Exception e) {
                    return new AuthResponse(ResponseCode.InvalidBody, "Body couldn't be read.");
                }

                UserBuilder userBuilder = new UserBuilder();
                AuthTokenBuilder tokenBuilder = new AuthTokenBuilder();

                User user = userBuilder
                    .setEmail(credentials.getEmail())
                    .setPassword(credentials.getPassword())
                    .getUser().findOne(userCollection, session);
                if (user == null) return new AuthResponse(ResponseCode.InvalidCredentials, "No user with those credentials exists");
                
                AuthToken token = tokenBuilder.setUserId(user.getId()).getToken().findOne(tokenCollection, session);
                if (token != null) {
                    AuthToken updateValues;
                    if (token.isExpired()) {
                        updateValues = tokenBuilder
                            .setExpiryDate(AuthToken.getNewExpirationDate())
                            .setName(AuthToken.createUniqueName(exchange))
                            .getToken();
                    } else {
                        updateValues = tokenBuilder
                            .setExpiryDate(AuthToken.getNewExpirationDate())
                            .getToken();
                    }
                    updateValues.updateOne(tokenCollection, session, token);
                    token = tokenBuilder.setId(token.getId()).getToken().findOne(tokenCollection, session);
                } else {
                    token = tokenBuilder
                        .setExpiryDate(AuthToken.getNewExpirationDate())
                        .setName(AuthToken.createUniqueName(exchange))
                        .setUserId(user.getId())
                        .getToken();
                    token.insertOne(tokenCollection, session);
                }

                exchange.getResponseHeaders().add("Set-Cookie", "authToken=" + token.getName() + "; Max-Age=" + token.getExpiryDate());
                return new AuthResponse(ResponseCode.OK, "Successfully logged user in.");
            } catch (Exception e) {
                e.printStackTrace();
                return new AuthResponse(ResponseCode.DatabaseError, "Something went wrong with database connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Something went wrong with database connection");
        }
    }

    public static AuthResponse registerUser(HttpExchange exchange) {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                MongoCollection<Document> tokenCollection = MongoUtility.getCollection(client, "tokens");

                Credentials credentials;
                try {
                    credentials = parseJsonBody(exchange, 1000, Credentials.class);
                    if (credentials == null) throw new Exception("Credentials were null");
                } catch (Exception e) {
                    return new AuthResponse(ResponseCode.InvalidBody, "Body couldn't be read.");
                }

                UserBuilder userBuilder = new UserBuilder();
                AuthTokenBuilder tokenBuilder = new AuthTokenBuilder();

                User user = userBuilder
                    .setEmail(credentials.getEmail())
                    .getUser().findOne(userCollection, session);

                if (user != null) return new AuthResponse(ResponseCode.InvalidCredentials, "User with this email already exists.");
                
                user = userBuilder
                    .setEmail(credentials.getEmail())
                    .setPassword(credentials.getPassword())
                    .setName(credentials.getEmail())
                    .setOnCall(false)
                    .setOnDuty(false)
                    .setPhoneNumber("00 00 00 00")
                    .getUser();
                user.insertOne(userCollection, session);
                user = userBuilder.setEmail(credentials.getEmail()).getUser().findOne(userCollection, session);

                AuthToken token = tokenBuilder
                    .setName(AuthToken.createUniqueName(exchange))
                    .setUserId(user.getId())
                    .setExpiryDate(AuthToken.getNewExpirationDate())
                    .getToken();
                token.insertOne(tokenCollection, session);

                exchange.getResponseHeaders().add("Set-Cookie", "authToken=" + token.getName() + "; Max-Age=" + token.getExpiryDate());
                return new AuthResponse(ResponseCode.OK, "Successfully registered user.");
            } catch (Exception e) {
                e.printStackTrace();
                return new AuthResponse(ResponseCode.DatabaseError, "Something went wrong with database connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(ResponseCode.DatabaseError, "Something went wrong with database connection");
        }
    }

    private static <T> T parseJsonBody(HttpExchange exchange, int maxSize, Class<T> type) throws Exception {
        int contentLength = Integer.parseInt(exchange.getRequestHeaders().get("Content-Length").get(0));
        if (maxSize < contentLength) throw new Exception("Content length is bigger than allowed size.");

        byte[] buffer = new byte[contentLength];
        int read;
        int totalRead = 0;
        while (totalRead < contentLength) {
            read = exchange.getRequestBody().read(buffer, totalRead, contentLength - totalRead);
            totalRead += read;
        }

        String bodyJson = new String(buffer);
        return new Gson().fromJson(bodyJson, type);
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