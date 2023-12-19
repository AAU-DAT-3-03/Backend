package dat3.app.server;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.AuthToken;
import dat3.app.models.User;
import dat3.app.models.AuthToken.AuthTokenBuilder;
import dat3.app.models.User.UserBuilder;
import dat3.app.utility.ExchangeUtility;
import dat3.app.utility.MongoUtility;

public abstract class Auth {
    /**
     * Authorizes an http exchange by checking the attached auth token against the database.
     * @param exchange the http exchange that needs to be authorized.
     * @return Returns the User that has just been authenticated. 
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

                // Find the token in the database. 
                AuthToken token = tokenBuilder.setName(tokenName).getToken().findOne(tokenCollection, session);
                if (token == null) return null;
                
                // Delete the token from the browser if it is too old. 
                if (token.isExpired()) {
                    exchange.getResponseHeaders().add("Set-Cookie", "authToken=nothing; Max-Age:0");
                    return null;
                }
                
                // Update the token in the database with a new expiration date.
                tokenBuilder.setExpiryDate(AuthToken.getNewExpirationDate()).getToken().updateOne(tokenCollection, session, token);

                // Return the user associated with the token.
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

                // Read the body data and parse to Credentials class. 
                Credentials credentials;
                try {
                    credentials = ExchangeUtility.parseJsonBody(exchange, 1000, Credentials.class);
                    if (credentials == null) throw new Exception("Credentials were null.");
                } catch (Exception e) {
                    return new AuthResponse(ResponseCode.InvalidBody, "Body couldn't be read.");
                }

                UserBuilder userBuilder = new UserBuilder();
                AuthTokenBuilder tokenBuilder = new AuthTokenBuilder();

                // Find user with this email and password.
                User user = userBuilder
                    .setEmail(credentials.getEmail())
                    .setPassword(credentials.getPassword())
                    .getUser().findOne(userCollection, session);

                if (user == null) return new AuthResponse(ResponseCode.InvalidCredentials, "No user with those credentials exists");
                
                // If this user already has a token, update it. If not, create one. 
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

                // Update the expiration date of the token stored in the browser. 
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

                // Read the body data and parse to Credentials class. 
                Credentials credentials;
                try {
                    credentials = ExchangeUtility.parseJsonBody(exchange, 1000, Credentials.class);
                    if (credentials == null) throw new Exception("Credentials were null");
                } catch (Exception e) {
                    return new AuthResponse(ResponseCode.InvalidBody, "Body couldn't be read.");
                }

                UserBuilder userBuilder = new UserBuilder();
                AuthTokenBuilder tokenBuilder = new AuthTokenBuilder();

                // Tries to find a user with the specified email address.
                User user = userBuilder
                    .setEmail(credentials.getEmail())
                    .getUser().findOne(userCollection, session);

                // If the email is taken, fail. 
                if (user != null) return new AuthResponse(ResponseCode.InvalidCredentials, "User with this email already exists.");
                
                // Create and insert user with these credentials. 
                user = userBuilder
                    .setEmail(credentials.getEmail())
                    .setPassword(credentials.getPassword())
                    .setOnCall(false)
                    .setOnDuty(false)
                    .getUser();
                user.insertOne(userCollection, session);

                // Retrieve from database (db generated an id for us at insertion)
                user = userBuilder.setEmail(credentials.getEmail()).getUser().findOne(userCollection, session);

                // Create and insert a brand new auth token. 
                AuthToken token = tokenBuilder
                    .setName(AuthToken.createUniqueName(exchange))
                    .setUserId(user.getId())
                    .setExpiryDate(AuthToken.getNewExpirationDate())
                    .getToken();
                token.insertOne(tokenCollection, session);

                // Update auth token in the browser.
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

    /**
     * Gets the auth token name from the cookies sent in the request. If non-existent, returns null.
     * @param exchange the http exchange.
     * @return the auth token name, or null if non-existent.
     */
    private static String getAuthTokenFromCookies(HttpExchange exchange) {
        // Cookies are stored in a header like this Cookie: "authToken={value}; otherToken={othervalue}"
        // It returns a list of values since there may be more than one header with the same key. 
        // We only want the first.
        List<String> headersWithNameCookie = exchange.getRequestHeaders().get("Cookie");
        if (headersWithNameCookie == null || headersWithNameCookie.size() == 0) return null;
        String cookieString = headersWithNameCookie.get(0);
        
        // split such that it is ["authToken={value}", "otherToken={otherValue}"]
        for (String cookie : cookieString.split("; ")) {
            if (cookie.startsWith("authToken")) {
                String[] pair = cookie.split("=");
                if (pair.length != 2) continue;

                // Premature exit if a value is found. 
                return pair[1];
            }
        }
        
        return null;
    }

    /**
     * Enum for parsing response codes of the auth functions.
     */
    public static enum ResponseCode {
        OK,
        DatabaseError,
        InvalidCredentials,
        InvalidBody,
        UnknownError,
    }
    
    /**
     * The return object for login and register functions.
     */
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

/**
 * Class used to encapsulate some values associated with credentials and is used such that GSON can parse body data. 
 */
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