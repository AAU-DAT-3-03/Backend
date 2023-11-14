package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

public class AuthToken extends Model<AuthToken> {
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
    public InsertOneResult insertOne(MongoCollection<Document> collection) throws Exception {
        Document toInsert = this.toDocument();
        return collection.insertOne(toInsert);
    }

    @Override
    public InsertOneResult insertOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        Document toInsert = this.toDocument();
        return collection.insertOne(session, toInsert);
    }

    @Override
    public AuthToken findOne(MongoCollection<Document> collection) throws Exception {
        Document filter = this.toDocument();
        Document result = collection.find(filter).first();
        if (result == null) return null;
        return this.fromDocument(result);
    }

    @Override
    public AuthToken findOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        Document filter = this.toDocument();
        Document result = collection.find(session, filter).first();
        if (result == null) return null;
        return this.fromDocument(result);
    }

    @Override
    public MongoIterable<AuthToken> findMany(MongoCollection<Document> collection) throws Exception {
        Document filter = this.toDocument();
        FindIterable<Document> result = collection.find(filter);
        MongoIterable<AuthToken> mapped = result.map((Document doc) -> {
            try  {
                return this.fromDocument(doc);
            } catch (Exception e) {
                return null;
            }
        });
        return mapped;
    }

    @Override
    public MongoIterable<AuthToken> findMany(MongoCollection<Document> collection, ClientSession session) throws Exception {
        Document filter = this.toDocument();
        FindIterable<Document> result = collection.find(session, filter);
        MongoIterable<AuthToken> mapped = result.map((Document doc) -> {
            try  {
                return this.fromDocument(doc);
            } catch (Exception e) {
                return null;
            }
        });
        return mapped;
    }

    @Override
    public UpdateResult updateOne(MongoCollection<Document> collection, AuthToken filter) throws Exception {
        Document setOperation = new Document("$set", this.toDocument());
        return collection.updateOne(filter.toDocument(), setOperation);
    }

    @Override
    public UpdateResult updateOne(MongoCollection<Document> collection, ClientSession session, AuthToken filter) throws Exception {
        Document setOperation = new Document("$set", this.toDocument());
        return collection.updateOne(session, filter.toDocument(), setOperation);
    }

    @Override
    public UpdateResult updateMany(MongoCollection<Document> collection, AuthToken filter) throws Exception {
        Document setOperation = new Document("$set", this.toDocument());
        return collection.updateMany(filter.toDocument(), setOperation);
    }

    @Override
    public UpdateResult updateMany(MongoCollection<Document> collection, ClientSession session, AuthToken filter) throws Exception {
        Document setOperation = new Document("$set", this.toDocument());
        return collection.updateMany(session, filter.toDocument(), setOperation);
    }

    @Override
    public DeleteResult deleteOne(MongoCollection<Document> collection) throws Exception {
        return collection.deleteOne(this.toDocument());
    }

    @Override
    public DeleteResult deleteOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        return collection.deleteOne(session, this.toDocument());
    }

    @Override
    public DeleteResult deleteMany(MongoCollection<Document> collection) throws Exception {
        return collection.deleteMany(this.toDocument());
    }

    @Override
    public DeleteResult deleteMany(MongoCollection<Document> collection, ClientSession session) throws Exception {
        return collection.deleteMany(session, this.toDocument());
    }

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
