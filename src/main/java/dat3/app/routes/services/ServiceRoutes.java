package dat3.app.routes.services;

import java.io.IOException;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.Service;
import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;

public class ServiceRoutes {
    public static void get(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        Document documentFilter = parseQueryString(exchange);
        Service filter = new Service().fromDocument(documentFilter);
        if (filter == null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        List<Service> result = ExchangeUtility.defaultGetOperation(filter, "services");
        if (result == null) {
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }

        Response response = new Response();
        response.setMsg(result);
        response.setStatusCode(0);
        try {
            response.sendResponse(exchange);
        } catch (Exception e) {}
    }

    public static void delete(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        Service filter = parseBody(exchange);
        if (filter == null || filter.getId() == null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        DeleteResult result = ExchangeUtility.defaultDeleteOperation(filter, "services");
        if (result == null) {
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }

        Response response = new Response();
        if (result.getDeletedCount() == 0) {
            response.setMsg("Did not delete any objects.");
            response.setStatusCode(1);
            return;
        } else {
            response.setMsg("Deleted object successfully.");
            response.setStatusCode(0);
        }

        try {
            response.sendResponse(exchange);
        } catch (IOException e) {}
    }

    public static void put(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        Service toUpdate = parseBody(exchange);
        if (toUpdate == null || toUpdate.getId() == null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        Service filter = new Service();
        filter.setId(toUpdate.getId());
        UpdateResult result = ExchangeUtility.defaultPutOperation(filter, toUpdate, "services");
        if (result == null) {
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }

        Response response = new Response();
        if (result.getModifiedCount() == 0) {
            response.setMsg("Did not modify any objects.");
            response.setStatusCode(1);
            return;
        } else {
            response.setMsg("Modified object successfully.");
            response.setStatusCode(0);
        }

        try {
            response.sendResponse(exchange);
        } catch (IOException e) {}
    }
    
    public static void post(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        Service filter = parseBody(exchange);
        if (filter == null || filter.getId() != null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        InsertOneResult result = ExchangeUtility.defaultPostOperation(filter, "services");
        if (result == null) {
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }

        Response response = new Response();
        if (!result.wasAcknowledged()) {
            response.setMsg("Did not insert any objects.");
            response.setStatusCode(1);
            return;
        } else {
            response.setMsg("Inserted object successfully.");
            response.setStatusCode(0);
        }

        try {
            response.sendResponse(exchange);
        } catch (IOException e) {}
    }

    private static Service parseBody(HttpExchange exchange) {
        try {
            return ExchangeUtility.parseJsonBody(exchange, 1000, Service.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static Document parseQueryString(HttpExchange exchange) {
        try {
            Document document = new Document();
            String[] tuples = exchange.getRequestURI().getQuery().split("&");
            for (String tuple : tuples) {
                String[] pair = tuple.split("=");

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
