package dat3.app.models;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

/**
 * The StandardModel is a default implementation for communicating with the database. Only things the implementing class needs to define is fromDocument and toDocument for converting database objects to models and models to database objects. 
 */
public abstract class StandardModel<T> extends Model<T> {
    public abstract String getId();
    public abstract void setId(String id);

    /**
     * Inserts this object into the database.
     * @param collection The collection to store this object in.
     * @param session The session used.
     * @throws Exception Throws an exception if something goes wrong when executing the query.
     * @return Returns an InsertOneResult containing information of the insertion operation.
     */
    @Override
    public InsertOneResult insertOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        return collection.insertOne(session, this.toDocument());
    }

    /**
     * Finds this object in the database.
     * @param collection The collection to search.
     * @param session The session used.
     * @throws Exception Throws an exception if something goes wrong when executing the query.
     * @return Returns an the object found, which very well may be null.
     */
    @Override
    public T findOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        Document result = collection.find(session, this.toDocument()).first();
        if (result == null) return null;
        return this.fromDocument(result);
    }

    /**
     * Finds all object in the database matching this object.
     * @param collection The collection to search.
     * @param session The session used.
     * @throws Exception Throws an exception if something goes wrong when executing the query.
     * @return Returns an the objects found, which very well may be an empty list, and the list may also contain null values.
     */
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

    /**
     * Updates the object on the database matching the filter to the values in this.
     * @param collection The collection to search.
     * @param session The session used.
     * @param filter The filter to use.
     * @throws Exception Throws an exception if something goes wrong when executing the query.
     * @return Returns an UpdateResult containing information on the query.
     */
    @Override
    public <U extends Model<U>> UpdateResult updateOne(MongoCollection<Document> collection, ClientSession session, U filter) throws Exception {
        if (filter == null || filter.toDocument().isEmpty()) {
            return collection.updateOne(session, new Document(), new Document("$set", this.toDocument()));
        }
        return collection.updateOne(session, filter.toDocument(), new Document("$set", this.toDocument()));
    }

    /**
     * Deletes the first object matching this object in the database. 
     * @param collection The collection to search.
     * @param session The session used.
     * @throws Exception Throws an exception if something goes wrong when executing the query.
     * @return Returns an DeleteResult result containing information on the query.
     */
    @Override
    public DeleteResult deleteOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        return collection.deleteOne(session, this.toDocument());
    }
}
