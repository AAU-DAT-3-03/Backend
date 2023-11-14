package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

public class AuthToken extends StandardModel<AuthToken> {
    private ObjectId _id = null;
    private String name = null;
    private Long expiryDate = null;
    private ObjectId userId = null;

    // ---------- Getters & Setters ---------- //
    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
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

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    // ---------- Builder subclass ---------- //
    public static class AuthTokenBuilder {
        private AuthToken token;

        public AuthTokenBuilder() {
            this.token = new AuthToken();
        }

        public AuthTokenBuilder setId(ObjectId _id) {
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

        public AuthTokenBuilder setUserId(ObjectId userId) {
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
        if (this._id != null) document.put("_id", this._id);
        if (this.expiryDate != null) document.put("expiryDate", this.expiryDate);
        if (this.name != null) document.put("name", this.name);
        if (this.userId != null) document.put("userId", this.userId);
        return document;        
    }

    @Override
    public AuthToken fromDocument(Document document) {
        AuthTokenBuilder builder = new AuthTokenBuilder();
        if (document.containsKey("_id")) builder.setId(document.getObjectId("_id"));
        if (document.containsKey("expiryDate")) builder.setExpiryDate(document.getLong("expiryDate"));
        if (document.containsKey("userId")) builder.setUserId(document.getObjectId("userId"));
        if (document.containsKey("name")) builder.setName(document.getString("name"));
        return builder.getToken();
    }

    // ---------- Static Methods ---------- //
    public static Long getNewExpirationDate() {
        return System.currentTimeMillis() + 1314000000;  // One year.
    }

    // ---------- Object Methods ---------- //
    public UpdateResult updateToken(MongoCollection<Document> collection, ClientSession session) throws Exception {
        AuthTokenBuilder builder = new AuthTokenBuilder();
        AuthToken filter = builder.setId(this._id).getToken();
        AuthToken values = builder.setExpiryDate(AuthToken.getNewExpirationDate()).getToken();
        return values.updateOne(collection, session, filter);
    }
}
