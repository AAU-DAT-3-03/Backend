package dat3.app.routes.users;

import java.util.Date;
import java.util.List;

import dat3.app.models.Event;
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
import dat3.app.utility.ExchangeUtility;
import dat3.app.utility.MongoUtility;

public abstract class UserRoutes {
    public static void getUser(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
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

                List<User> users = MongoUtility.iterableToList(filter.findMany(userCollection, session));
                users.forEach((User user) -> {
                    user.setPassword(null);
                });
                Response response = new Response();
                response.setStatusCode(0);
                response.setMsg(users);
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
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                
                User filter = ExchangeUtility.parseJsonBody(exchange, 1000, User.class);
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
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                
                User updateValues = ExchangeUtility.parseJsonBody(exchange, 1000, User.class);
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
    private static List<Event> fetchEvents(String userId) {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> eventCollection = MongoUtility.getCollection(client, "events");
                Event.EventBuilder eventbuilder = new Event.EventBuilder();
                eventbuilder.setAffectedObjectId(userId);
                Event filter = eventbuilder.getEvent();
                List<Event> events =  MongoUtility.iterableToList(filter.findMany(eventCollection, session));
                session.close();
                return events;
            } catch(Exception e) {
            }
        } catch(Exception e) {
        }
        return null;
    }
    private static void createEvent(String userId, String message, String affectedUserId) {
        Event.EventBuilder eventbuilder = new Event.EventBuilder();
        eventbuilder.setUserId(userId);
        eventbuilder.setMessage(message);
        eventbuilder.setAffectedObjectId(affectedUserId);
        eventbuilder.setDate(new Date().getTime());
        Event event = eventbuilder.getEvent();
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> eventCollection = MongoUtility.getCollection(client, "events");
                // Indsæt event i DB
            } catch(Exception e) {
            }
        } catch(Exception e) {
        }
    }
}
