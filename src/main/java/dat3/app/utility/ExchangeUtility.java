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
    /**
     * Parses a JSON data from the body of a request using GSON
     * @param <T> The expected return type. 
     * @param exchange The HttpExchange that is tied to the current client-server communication
     * @param maxSize The maximum size of the body data. If data size exceeds this, it throws. 
     * @param type The class to which the JSON will be parsed. 
     * @return Returns a new instance of T, with fields containing the body data.
     * @throws Exception Throws an exception if body data can't be read, or something goes wrong in parsing the data.
     */
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

    /**
     * Sends a default unautherized response. 
     * @param exchange The HttpExchange that is tied to the current client-server communication
     */
    public static void sendUnauthorizedResponse(HttpExchange exchange) {
        Response response = new Response();
        response.setMsg("Not authorized");
        response.setStatusCode(-1);
        try {
            response.sendResponse(exchange);
        } catch (IOException e) {}
    }

    /**
     * Starts a very short-lived session and performs a findMany query to the database, with the given filter. 
     * @param <T> The expected return type. 
     * @param filter The filter (must match return type, eg. User can only be a filter for other User)
     * @param collectionName The name of the collection where the queried data is stored.
     * @return Returns a list of all objects that matched the query.
     */
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

    /**
     * Starts a very short-lived session and performs a deleteOne query to the database, with the given filter. 
     * @param <T> The type of the filter, must extend StandardModel.
     * @param filter The filter to use. 
     * @param collectionName The name of the collection to query.
     * @return Returns a DeleteResult, containing information of how many Documents were deleted (max 1)
     */
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

    /**
     * Starts a very short-lived session and performs a updateOne query to the database, with the given filter and values to update.
     * @param <T> The type of the filter and object encapsulating values to update.  
     * @param filter The type of the filter 
     * @param toUpdate The object containing the values to update. 
     * @param collectionName The name of the collection to query.
     * @return Returns an UpdateResult, containing information of how many Documents were updated. 
     */
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

    /**
     * Starts a very short-lived session and performs a insertOne query to the database, with the given filter and values to update.
     * @param <T> The type of the object to insert. 
     * @param toInsert The object to insert.
     * @param collectionName The collection to query.
     * @return Returns an InsertOneResult, containing information from the insert operation.
     */
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

    /**
     * Sends a default query execution error response (something went wrong when querying the database). 
     * @param exchange The HttpExchange that is tied to the current client-server communication
     */
    public static void queryExecutionErrorResponse(HttpExchange exchange) {
        Response response = new Response();
        response.setMsg("Something went wrong when executing query.");
        response.setStatusCode(1);
        try {
            response.sendResponse(exchange);
        } catch (IOException e) {}
    }

    /**
     * Sends a default invalid query response (most often invalid body data eg. invalid query or the query string was bad). 
     * @param exchange The HttpExchange that is tied to the current client-server communication
     */
    public static void invalidQueryResponse(HttpExchange exchange) {
        Response response = new Response();
        response.setMsg("Invalid query.");
        response.setStatusCode(1);
        try {
            response.sendResponse(exchange);
        } catch (IOException e) {}
    }
}
