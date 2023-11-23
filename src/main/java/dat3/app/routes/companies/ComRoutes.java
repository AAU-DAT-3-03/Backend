package dat3.app.routes.companies;

import java.io.IOException;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.Company;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;

public abstract class ComRoutes {
    public static void getCompanies(HttpExchange exchange) {
        Company filter = parseQueryString(exchange);
        if (filter == null) {
            invalidQueryResponse(exchange);
            return;
        }

        List<Company> result = ExchangeUtility.defaultGetOperation(filter, "companies");
        if (result == null) {
            queryErrorResponse(exchange);
            return;
        }

        Response response = new Response();
        response.setMsg(result);
        response.setStatusCode(0);
    }

    public static void deleteCompanies(HttpExchange exchange) {
        Company filter = parseQueryString(exchange);
        if (filter == null) {
            invalidQueryResponse(exchange);
            return;
        }

        DeleteResult result = ExchangeUtility.defaultDeleteOperation(filter, "companies");
        if (result == null) {
            queryErrorResponse(exchange);
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

    public static void putCompanies(HttpExchange exchange) {
        Company toUpdate = parseQueryString(exchange);
        if (toUpdate == null) {
            invalidQueryResponse(exchange);
            return;
        }

        Company filter = new Company();
        filter.setId(toUpdate.getId());
        UpdateResult result = ExchangeUtility.defaultPutOperation(filter, toUpdate, "companies");
        if (result == null) {
            queryErrorResponse(exchange);
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

    public static void postCompanies(HttpExchange exchange) {
        Company filter = parseQueryString(exchange);
        if (filter == null) {
            invalidQueryResponse(exchange);
            return;
        }

        InsertOneResult result = ExchangeUtility.defaultPostOperation(filter, "companies");
        if (result == null) {
            queryErrorResponse(exchange);
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

    private static void queryErrorResponse(HttpExchange exchange) {
        Response response = new Response();
        response.setMsg("Something went wrong when executing query.");
        response.setStatusCode(1);
        try {
            response.sendResponse(exchange);
        } catch (IOException e) {}
    }

    private static void invalidQueryResponse(HttpExchange exchange) {
        Response response = new Response();
        response.setMsg("Invalid query.");
        response.setStatusCode(1);
        try {
            response.sendResponse(exchange);
        } catch (IOException e) {}
    }

    private static Company parseQueryString(HttpExchange exchange) {
        try {
            Document document = new Document();
            String[] tuples = exchange.getRequestURI().getQuery().split("&");
            for (String tuple : tuples) {
                String[] pair = tuple.split("=");

                if (pair[0].equals("id")) {
                    if (pair[1].equals("*")) return new Company();

                    document.put("_id", new ObjectId(pair[1]));
                    continue;
                }

                if (pair[0].equals("name")) {
                    document.put("name", pair[1]);
                    continue;
                }

                return null;
            }

            return new Company().fromDocument(document);
        } catch (Exception e) {
            return null;
        }
    }
}
