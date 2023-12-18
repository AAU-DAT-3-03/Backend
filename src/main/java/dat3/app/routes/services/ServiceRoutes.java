package dat3.app.routes.services;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.Service;
import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;

public class ServiceRoutes {
    /**
     * GET /services
     * @param exchange The HttpExchange that is tied to the current client communication
     */
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

    /**
     * Parses the query string to a document which is then used as a filter for getting services.
     * @param exchange The HttpExchange object for the client-server communication 
     * @return Returns the document that the query can be parsed to. 
     */
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
