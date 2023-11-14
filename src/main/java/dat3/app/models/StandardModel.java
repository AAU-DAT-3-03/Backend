package dat3.app.models;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

public abstract class StandardModel<T> extends Model<T> {

    @Override
    public InsertOneResult insertOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        return collection.insertOne(session, this.toDocument());
    }

    @Override
    public T findOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        Document result = collection.find(session, this.toDocument()).first();
        if (result == null) return null;
        return this.fromDocument(result);
    }

    @Override
    public MongoIterable<T> findMany(MongoCollection<Document> collection, ClientSession session) throws Exception {
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
    public <U extends Model<U>> UpdateResult updateMany(MongoCollection<Document> collection, ClientSession session, U filter) throws Exception {
        return collection.updateMany(session, filter.toDocument(), new Document("$set", this.toDocument()));
    }

    @Override
    public <U extends Model<U>> UpdateResult updateOne(MongoCollection<Document> collection, ClientSession session, U filter) throws Exception {
        return collection.updateOne(session, filter.toDocument(), new Document("$set", this.toDocument()));
    }

    @Override
    public DeleteResult deleteOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        return collection.deleteOne(session, this.toDocument());
    }

    @Override
    public DeleteResult deleteMany(MongoCollection<Document> collection, ClientSession session) throws Exception {
        return collection.deleteMany(session, this.toDocument());
    }
}
