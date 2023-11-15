package dat3.app.models;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

public abstract class Model <T> {
    // Insertion operation
    public abstract InsertOneResult insertOne(MongoCollection<Document> collection, ClientSession session) throws Exception;

    // Find operations
    public abstract T findOne(MongoCollection<Document> collection, ClientSession session) throws Exception;
    public abstract MongoIterable<T> findMany(MongoCollection<Document> collection, ClientSession session) throws Exception;

    // Update operations
    public abstract <U extends Model<U>> UpdateResult updateOne(MongoCollection<Document> collection, ClientSession session, U filter) throws Exception;
    public abstract <U extends Model<U>> UpdateResult updateMany(MongoCollection<Document> collection, ClientSession session, U filter) throws Exception;

    // Delete operations
    public abstract DeleteResult deleteOne(MongoCollection<Document> collection, ClientSession session) throws Exception;
    public abstract DeleteResult deleteMany(MongoCollection<Document> collection, ClientSession session) throws Exception;

    // Parsing
    public abstract Document toDocument();
    public abstract T fromDocument(Document document);
}
