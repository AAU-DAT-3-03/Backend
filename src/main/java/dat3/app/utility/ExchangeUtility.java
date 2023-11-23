package dat3.app.utility;

import java.io.IOException;
import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.StandardModel;
import dat3.app.server.Response;

public abstract class ExchangeUtility {
    public static <T> T parseJsonBody(HttpExchange exchange, int maxSize, Class<T> type) throws Exception {
        int contentLength = Integer.parseInt(exchange.getRequestHeaders().get("Content-Length").get(0));
        if (maxSize < contentLength) throw new Exception("Content length is bigger than allowed size.");

        byte[] buffer = new byte[contentLength];
        int read;
        int totalRead = 0;
        while (totalRead < contentLength) {
            read = exchange.getRequestBody().read(buffer, totalRead, contentLength - totalRead);
            totalRead += read;
        }

        String bodyJson = new String(buffer);
        return new Gson().fromJson(bodyJson, type);
    }

    public static void sendUnauthorizedResponse(HttpExchange exchange) {
        Response response = new Response();
        response.setMsg("Not authorized");
        response.setStatusCode(-1);
        try {
            response.sendResponse(exchange);
        } catch (IOException e) {}
    }

    public static <T extends StandardModel<T>> List<T> defaultGetOperation(T filter, String collectionName) {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> collection = MongoUtility.getCollection(client, collectionName);
                return MongoUtility.iterableToList(filter.findMany(collection, session));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends StandardModel<T>> DeleteResult defaultDeleteOperation(T filter, String collectionName) {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> collection = MongoUtility.getCollection(client, collectionName);
                return filter.deleteOne(collection, session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends StandardModel<T>> UpdateResult defaultPutOperation(T filter, T toUpdate, String collectionName) {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> collection = MongoUtility.getCollection(client, collectionName);
                return toUpdate.updateOne(collection, session, filter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends StandardModel<T>> InsertOneResult defaultPostOperation(T toInsert, String collectionName) {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> collection = MongoUtility.getCollection(client, collectionName);
                return toInsert.insertOne(collection, session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
