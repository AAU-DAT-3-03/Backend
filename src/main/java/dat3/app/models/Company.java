package dat3.app.models;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

public class Company extends Model<Company> {

    @Override
    public InsertOneResult insertOne(MongoCollection<Document> collection) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertOne'");
    }

    @Override
    public InsertOneResult insertOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertOne'");
    }

    @Override
    public Company findOne(MongoCollection<Document> collection) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findOne'");
    }

    @Override
    public Company findOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findOne'");
    }

    @Override
    public MongoIterable<Company> findMany(MongoCollection<Document> collection) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findMany'");
    }

    @Override
    public MongoIterable<Company> findMany(MongoCollection<Document> collection, ClientSession session)
            throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findMany'");
    }

    @Override
    public UpdateResult updateOne(MongoCollection<Document> collection, Company filter) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateOne'");
    }

    @Override
    public UpdateResult updateOne(MongoCollection<Document> collection, ClientSession session, Company filter)
            throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateOne'");
    }

    @Override
    public UpdateResult updateMany(MongoCollection<Document> collection, Company filter) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMany'");
    }

    @Override
    public UpdateResult updateMany(MongoCollection<Document> collection, ClientSession session, Company filter)
            throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMany'");
    }

    @Override
    public DeleteResult deleteOne(MongoCollection<Document> collection) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteOne'");
    }

    @Override
    public DeleteResult deleteOne(MongoCollection<Document> collection, ClientSession session) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteOne'");
    }

    @Override
    public DeleteResult deleteMany(MongoCollection<Document> collection) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteMany'");
    }

    @Override
    public DeleteResult deleteMany(MongoCollection<Document> collection, ClientSession session) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteMany'");
    }

    @Override
    public Document toDocument() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toDocument'");
    }

    @Override
    public Company fromDocument(Document document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fromDocument'");
    }
}
