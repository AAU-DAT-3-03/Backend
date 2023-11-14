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

public class Company extends Model<Company> {
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
    public static class CompanyBuilder {
        private Company company = new Company();

        public CompanyBuilder setName(String name) {
            company.setName(name);
            return this;
        }

        public CompanyBuilder setId(ObjectId id) {
            company.setId(id);
            return this;
        }

        public Company getCompany() {
            Company temp = this.company;
            this.company = new Company();
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
    public Company findOne(MongoCollection<Document> collection) throws Exception {
        Document result = collection.find(this.toDocument()).first();
        if (result == null) return null;
        return this.fromDocument(result);
    }

    @Override
    public Company findOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        Document result = collection.find(session, this.toDocument()).first();
        if (result == null) return null;
        return this.fromDocument(result);
    }

    @Override
    public MongoIterable<Company> findMany(MongoCollection<Document> collection) throws Exception {
        FindIterable<Document> findResult = collection.find(this.toDocument());
        return findResult.map((Document doc) -> {
            try {
                return this.fromDocument(doc);
            } catch (Exception e) {
                return null;
            }
        });
    }

    @Override
    public MongoIterable<Company> findMany(MongoCollection<Document> collection, ClientSession session) throws Exception {
        FindIterable<Document> findResult = collection.find(session, this.toDocument());
        return findResult.map((Document doc) -> {
            try {
                return this.fromDocument(doc);
            } catch (Exception e) {
                return null;
            }
        });
    }

    @Override
    public UpdateResult updateOne(MongoCollection<Document> collection, Company filter) throws Exception {
        Document setOperation = new Document("$set", this.toDocument());
        return collection.updateOne(filter.toDocument(), setOperation);
    }

    @Override
    public UpdateResult updateOne(MongoCollection<Document> collection, ClientSession session, Company filter) throws Exception {
        Document setOperation = new Document("$set", this.toDocument());
        return collection.updateOne(session, filter.toDocument(), setOperation);
    }

    @Override
    public UpdateResult updateMany(MongoCollection<Document> collection, Company filter) throws Exception {
        Document setOperation = new Document("$set", this.toDocument());
        return collection.updateMany(filter.toDocument(), setOperation);
    }

    @Override
    public UpdateResult updateMany(MongoCollection<Document> collection, ClientSession session, Company filter) throws Exception {
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
        if (this.name != null) document.put("name", this.name);
        return document;
    }

    @Override
    public Company fromDocument(Document document) {
        Company company = new Company();
        if (document.containsKey("_id")) company._id = document.getObjectId("_id");
        if (document.containsKey("name")) company.name = document.getString("name");
        return company;
    }

    // ---------- Static Methods ---------- //

    // ---------- Object Methods ---------- //
}
