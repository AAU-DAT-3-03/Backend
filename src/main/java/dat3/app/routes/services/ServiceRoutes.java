package dat3.app.routes.services;

import java.io.IOException;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.Service;
import dat3.app.models.Service.ServiceBuilder;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;
import dat3.app.utility.MongoUtility;

public abstract class ServiceRoutes {
    public static void getService(HttpExchange exchange) {

        Document document = parseQueryString(exchange);
        if (document == null) {
            Response response = new Response();
            response.setMsg("Invalid query string.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e) {}
            return;
        }
        Service service = new Service().fromDocument(document);
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> serviceCollection = MongoUtility.getCollection(client, "services");
                List<Service> services = MongoUtility.iterableToList(service.findMany(serviceCollection, session));
                
                Response response = new Response();
                response.setMsg(services);
                response.setStatusCode(0);
                response.sendResponse(exchange);
            }
        } catch (Exception e) {
            Response response = new Response();
            response.setMsg("Something went wrong.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }
    }

    public static void deleteService(HttpExchange exchange) {
        Service service;
        try {
            service = ExchangeUtility.parseJsonBody(exchange, 1000, Service.class);
        } catch (Exception e) {
            Response response = new Response();
            response.setMsg("Couldn't parse json body.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> serviceCollection = MongoUtility.getCollection(client, "services");
                if (service.getId() == null) {
                    Response response = new Response();
                    response.setMsg("Invalid body data. Body needs an id.");
                    response.setStatusCode(1);
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }

                DeleteResult result = service.deleteOne(serviceCollection, session);

                if (result.getDeletedCount() > 0) {
                    Response response = new Response();
                    response.setMsg("Deleted service.");
                    response.setStatusCode(0);
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                } else {
                    Response response = new Response();
                    response.setMsg("Deleted 0 services.");
                    response.setStatusCode(1);
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }
            }
        } catch (Exception e) {
            Response response = new Response();
            response.setMsg("Something went wrong.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }
    }

    public static void putService(HttpExchange exchange) {
        Service service;
        try {
            service = ExchangeUtility.parseJsonBody(exchange, 1000, Service.class);
        } catch (Exception e) {
            Response response = new Response();
            response.setMsg("Couldn't parse json body.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> serviceCollection = MongoUtility.getCollection(client, "services");
                if (service.getId() == null) {
                    Response response = new Response();
                    response.setMsg("Invalid body data, an id must be specified.");
                    response.setStatusCode(1);
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }

                ServiceBuilder builder = new ServiceBuilder();
                UpdateResult result = service.updateOne(serviceCollection, session, builder.setId(service.getId()).getService());

                if (result.getModifiedCount() > 0) {
                    Response response = new Response();
                    response.setMsg("Modified service.");
                    response.setStatusCode(0);
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                } else {
                    Response response = new Response();
                    response.setMsg("Modified 0 services.");
                    response.setStatusCode(1);
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }
            }
        } catch (Exception e) {
            Response response = new Response();
            response.setMsg("Something went wrong.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }
    }

    public static void postService(HttpExchange exchange) {
        Service service;
        try {
            service = ExchangeUtility.parseJsonBody(exchange, 1000, Service.class);
        } catch (Exception e) {
            Response response = new Response();
            response.setMsg("Couldn't parse json body.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> serviceCollection = MongoUtility.getCollection(client, "services");
                if (service.getId() != null) {
                    Response response = new Response();
                    response.setMsg("Invalid body data, an id cannot be specified at creation.");
                    response.setStatusCode(1);
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }

                InsertOneResult result = service.insertOne(serviceCollection, session);

                if (result.wasAcknowledged()) {
                    Response response = new Response();
                    response.setMsg("Created service.");
                    response.setStatusCode(0);
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                } else {
                    Response response = new Response();
                    response.setMsg("Created 0 services.");
                    response.setStatusCode(1);
                    try {
                        response.sendResponse(exchange);
                    } catch (IOException e1) {}
                    return;
                }
            }
        } catch (Exception e) {
            Response response = new Response();
            response.setMsg("Something went wrong.");
            response.setStatusCode(1);
            try {
                response.sendResponse(exchange);
            } catch (IOException e1) {}
            return;
        }
    }

    private static Document parseQueryString(HttpExchange exchange) {
        try {
            Document document = new Document();
            String[] pairs = exchange.getRequestURI().getQuery().split("&");
            for (String string : pairs) {
                String[] pair = string.split("=");

                if (pair[0].equals("id")) {
                    if (pair[1].equals("*")) return new Document();
                    document.put("_id", new ObjectId(pair[1]));
                    continue;
                }

                if (pair[0].equals("name")) {
                    document.put("name", pair[1]);
                    continue;
                }

                if (pair[0].equals("companyId")) {
                    document.put("companyId", new ObjectId(pair[1]));
                    continue;
                }

                return null;
            }

            return document;
        } catch (Exception e) {
            return null;
        }
    }
}
