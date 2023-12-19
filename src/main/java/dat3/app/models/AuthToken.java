package dat3.app.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;

public class AuthToken extends StandardModel<AuthToken> {
    private String _id = null;
    private String name = null;
    private Long expiryDate = null;
    private String userId = null;

    // ---------- Getters & Setters ---------- //
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
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

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // ---------- Builder subclass ---------- //
    public static class AuthTokenBuilder {
        private AuthToken token;

        public AuthTokenBuilder() {
            this.token = new AuthToken();
        }

        public AuthTokenBuilder setId(String _id) {
            token._id = _id;
            return this;
        }

        public AuthTokenBuilder setName(String name) {
            token.name = name;
            return this;
        }

        public AuthTokenBuilder setExpiryDate(Long expiryDate) {
            token.expiryDate = expiryDate;
            return this;
        }

        public AuthTokenBuilder setUserId(String userId) {
            token.userId = userId;
            return this;
        }

        public AuthToken getToken() {
            AuthToken temp = this.token;
            this.token = new AuthToken();
            return temp;
        }
    }

    // ---------- Abstract method overrides ---------- //
    @Override
    public Document toDocument() {
        Document document = new Document();
        if (this._id != null) document.put("_id", new ObjectId(this._id));
        if (this.expiryDate != null) document.put("expiryDate", this.expiryDate);
        if (this.name != null) document.put("name", this.name);
        if (this.userId != null) document.put("userId", new ObjectId(this.userId));
        return document;        
    }

    @Override
    public AuthToken fromDocument(Document document) {
        AuthToken token = new AuthToken();
        if (document.containsKey("_id")) token._id = document.getObjectId("_id").toHexString();
        if (document.containsKey("expiryDate")) token.expiryDate = document.getLong("expiryDate");
        if (document.containsKey("userId")) token.userId = document.getObjectId("userId").toHexString();
        if (document.containsKey("name")) token.name = document.getString("name");
        return token;
    }

    // ---------- Static Methods ---------- //
    /**
     * @return Returns a new random unix timestamp within 365 days.
     */
    public static Long getNewExpirationDate() {
        return System.currentTimeMillis() + 1314000000;  // One year.
    }

    /**
     * Creates a unique name by using SHA-256 to hash the address of an exchange + the timestamp in ms. 
     * @param exchange The exchange.
     * @return Returns the hash represented as hex. 
     * @throws NoSuchAlgorithmException
     */
    public static String createUniqueName(HttpExchange exchange) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String name = exchange.getRemoteAddress().getAddress().toString() + Long.toString(System.currentTimeMillis());
        byte[] hash = digest.digest(name.getBytes());
        return Hex.encodeHexString(hash);
    }

    // ---------- Object Methods ---------- //
    /**
     * Updates the token with a new expiration date.
     * @param collection The collection to use.
     * @param session The session.
     * @return Returns an UpdateResult, showing if the token was updated.
     * @throws Exception Throws an exception if something went wrong underway.
     */
    public UpdateResult updateToken(MongoCollection<Document> collection, ClientSession session) throws Exception {
        AuthTokenBuilder builder = new AuthTokenBuilder();
        AuthToken filter = builder.setId(this._id).getToken();
        AuthToken values = builder.setExpiryDate(AuthToken.getNewExpirationDate()).getToken();
        return values.updateOne(collection, session, filter);
    }

    /**
     * Simply tells if a token is expired. 
     * @return Returns false if the token is valid, true if it is expired (invalid)
     */
    public boolean isExpired() {
        return expiryDate < System.currentTimeMillis();
    }
}
