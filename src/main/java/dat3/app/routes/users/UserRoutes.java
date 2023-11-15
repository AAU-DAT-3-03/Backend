package dat3.app.routes.users;

import java.io.IOException;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.User;
import dat3.app.models.User.UserBuilder;
import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.utility.MongoUtility;

public abstract class UserRoutes {
    public static void getUser(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            Response response = new Response();
            response.setMsg("Not authorized");
            response.setStatusCode(-1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e) {}
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                
                User filter = parseQueryString(exchange);
                if (filter == null) {
                    Response response = new Response();
                    response.setMsg("Empty query.");
                    response.setStatusCode(1);
                    response.sendResponse(exchange);
                    return;
                }

                List<Document> users = MongoUtility.iterableToDocs(filter.findMany(userCollection, session));
                Response response = new Response();
                response.setMsg(new Document("users", users).toJson());
                response.sendResponse(exchange);
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setMsg("Something went wrong.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (Exception e1) {}
        }
    }

    public static void deleteUser(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            Response response = new Response();
            response.setMsg("Not authorized");
            response.setStatusCode(-1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e) {}
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                
                User filter = parseQueryString(exchange);
                if (filter == null || filter.getId() == null) {
                    Response response = new Response();
                    response.setMsg("Must delete users by id.");
                    response.setStatusCode(1);
                    response.sendResponse(exchange);
                    return;
                }

                DeleteResult result = filter.deleteOne(userCollection, session);

                Response response = new Response();
                if (result.getDeletedCount() > 0) {
                    response.setMsg("Successfully deleted user.");
                    response.setStatusCode(0);
                    response.sendResponse(exchange);
                } else {
                    response.setMsg("Deleted 0 users.");
                    response.setStatusCode(1);
                    response.sendResponse(exchange);
                }
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setMsg("Something went wrong.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (Exception e1) {}
        }
    }

    public static void updateUser(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            Response response = new Response();
            response.setMsg("Not authorized");
            response.setStatusCode(-1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e) {}
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                
                User updateValues = parseQueryString(exchange);
                if (updateValues == null || updateValues.getId() == null) {
                    Response response = new Response();
                    response.setMsg("Must update users by id.");
                    response.setStatusCode(1);
                    response.sendResponse(exchange);
                    return;
                }

                UserBuilder builder = new UserBuilder();
                User filter = builder.setId(updateValues.getId()).getUser();
                UpdateResult result = updateValues.updateOne(userCollection, session, filter);

                Response response = new Response();
                if (result.getModifiedCount() > 0) {
                    response.setMsg("Successfully updated user.");
                    response.setStatusCode(0);
                    response.sendResponse(exchange);
                } else {
                    response.setMsg("Updated 0 users.");
                    response.setStatusCode(1);
                    response.sendResponse(exchange);
                }
            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setMsg("Something went wrong.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (Exception e1) {}
        }
    }

    private static User parseQueryString(HttpExchange exchange) {
        try {
            Document document = new Document();
            String[] pairs = exchange.getRequestURI().getQuery().split("&");
            for (String string : pairs) {
                String[] pair = string.split("=");

                if (pair[0].equals("id")) {
                    if (pair[1].equals("*")) return new User();
                    document.put("_id", new ObjectId(pair[1]));
                    continue;
                }

                document.put(pair[0], pair[1]);
            }
            return new User().fromDocument(document);
        } catch (Exception e) {
            return null;
        }
    }
}
