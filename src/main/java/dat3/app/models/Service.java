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

public class Service extends Model<Service> {
    private ObjectId _id = null;
    private String name = null;

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

    // ---------- Builder subclass ---------- //
    public static class ServiceBuilder  {
        private Service service = new Service();

        public ServiceBuilder setId(ObjectId id) {
            service.setId(id);
            return this;
        }

        public ServiceBuilder setName(String name) {
            service.setName(name);
            return this;
        }

        public Service getService() {
            Service temp = this.service;
            this.service = new Service();
            return temp;
        }
    }

    // ---------- Abstract method overrides ---------- //
    @Override
    public InsertOneResult insertOne(MongoCollection<Document> collection) throws Exception {
        return collection.insertOne(this.toDocument());
    }

    @Override
    public InsertOneResult insertOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        return collection.insertOne(session, this.toDocument());
    }

    @Override
    public Service findOne(MongoCollection<Document> collection) throws Exception {
        Document result = collection.find(this.toDocument()).first();
        if (result == null) return null;
        return this.fromDocument(result);
    }

    @Override
    public Service findOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        Document result = collection.find(session, this.toDocument()).first();
        if (result == null) return null;
        return this.fromDocument(result);
    }

    @Override
    public MongoIterable<Service> findMany(MongoCollection<Document> collection) throws Exception {
        FindIterable<Document> result = collection.find(this.toDocument());
        return result.map((Document document) -> {
            try {
                return this.fromDocument(document);
            } catch (Exception e) {
                return null;
            }
        });
    }

    @Override
    public MongoIterable<Service> findMany(MongoCollection<Document> collection, ClientSession session) throws Exception {
        FindIterable<Document> result = collection.find(session, this.toDocument());
        return result.map((Document document) -> {
            try {
                return this.fromDocument(document);
            } catch (Exception e) {
                return null;
            }
        });
    }

    @Override
    public UpdateResult updateOne(MongoCollection<Document> collection, Service filter) throws Exception {
        return collection.updateOne(filter.toDocument(), new Document("$set", this.toDocument()));
    }

    @Override
    public UpdateResult updateOne(MongoCollection<Document> collection, ClientSession session, Service filter) throws Exception {
        return collection.updateOne(session, filter.toDocument(), new Document("$set", this.toDocument()));
    }

    @Override
    public UpdateResult updateMany(MongoCollection<Document> collection, Service filter) throws Exception {
        return collection.updateMany(filter.toDocument(), new Document("$set", this.toDocument()));
    }

    @Override
    public UpdateResult updateMany(MongoCollection<Document> collection, ClientSession session, Service filter) throws Exception {
        return collection.updateMany(session, filter.toDocument(), new Document("$set", this.toDocument()));
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
        if (this.name != null) document.put("name", this.name);
        return document;
    }

    @Override
    public Service fromDocument(Document document) {
        Service service = new Service();
        if (document.containsKey("_id")) service._id = document.getObjectId("_id");
        if (document.containsKey("name")) service.name = document.getString("name");
        return service;
    }

    // ---------- Static Methods ---------- //
    

    // ---------- Object Methods ---------- //

}
