package dat3.app.routes.users;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.User;
import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;

public class UserRoutes {
    public static void get(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        Document documentFilter = parseQueryString(exchange);
        User filter = new User().fromDocument(documentFilter);
        if (filter == null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        List<User> result = ExchangeUtility.defaultGetOperation(filter, "users");
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

                if (pair[0].equals("email")) {
                    document.put("email", pair[1]);
                    continue;
                }

                if (pair[0].equals("name")) {
                    document.put("name", pair[1]);
                    continue;
                }

                if (pair[0].equals("phoneNumber")) {
                    document.put("phoneNumber", pair[1]);
                    continue;
                }

                if (pair[0].equals("onCall")) {
                    document.put("onCall", Boolean.parseBoolean(pair[1]));
                    continue;
                }

                if (pair[0].equals("onDuty")) {
                    document.put("onDuty", Boolean.parseBoolean(pair[1]));
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
