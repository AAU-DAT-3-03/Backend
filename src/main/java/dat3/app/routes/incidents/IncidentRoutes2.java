package dat3.app.routes.incidents;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import dat3.app.models.Event;
import dat3.app.models.Event.EventBuilder;
import dat3.app.server.Auth;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.Incident;
import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;

public abstract class IncidentRoutes2 {
    public static void get(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        Document documentFilter = parseQueryString(exchange);
        Incident filter = new Incident().fromDocument(documentFilter);
        if (filter == null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        List<Incident> result = ExchangeUtility.defaultGetOperation(filter, "incidents");
        if (result == null) {
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }
        for (Incident incident : result) {
            Event eventFilter = new EventBuilder().setAffectedObjectId(incident.getId()).getEvent();
            List<Event> eventLog = ExchangeUtility.defaultGetOperation(eventFilter, "events");
            incident.setEventLog(eventLog);
        }


        // add start and end filtering.

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

        Incident filter = parseBody(exchange);
        if (filter == null || filter.getId() == null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        DeleteResult result = ExchangeUtility.defaultDeleteOperation(filter, "incidents");
        if (result == null) {
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }
        Event eventFilter = new EventBuilder().setAffectedObjectId(filter.getId()).getEvent();
        DeleteResult eventLogResult = ExchangeUtility.defaultDeleteOperation(eventFilter, "events");

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

        Incident toUpdate = parseBody(exchange);
        if (toUpdate == null || toUpdate.getId() == null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        Incident filter = new Incident();
        filter.setId(toUpdate.getId());
        UpdateResult result = ExchangeUtility.defaultPutOperation(filter, toUpdate, "incidents");
        if (result == null) {
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }
        try {
            Event event = new EventBuilder().setAffectedObjectId(toUpdate.getId()).setMessage("temp message").setDate(new Date().getTime()).setUserId(Auth.auth(exchange).getId()).getEvent();
            ExchangeUtility.defaultPostOperation(event, "events");
        } catch(Exception e) {
            System.out.println("Couldnt log an event for incident" + toUpdate.getId());
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
        
        Incident filter = parseBody(exchange);
        if (filter == null || filter.getId() != null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        InsertOneResult result = ExchangeUtility.defaultPostOperation(filter, "incidents");
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

    private static Incident parseBody(HttpExchange exchange) {
        try {
            return ExchangeUtility.parseJsonBody(exchange, 1000, Incident.class);
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

                if (pair[0].equals("priority")) {
                    document.put("priority", Integer.parseInt(pair[1]));
                    continue;
                }

                if (pair[0].equals("resolved")) {
                    document.put("resolved", Boolean.parseBoolean(pair[1]));
                    continue;
                }

                if (pair[0].equals("header")) {
                    document.put("header", pair[1]);
                    continue;
                }

                if (pair[0].equals("acknowledgedBy")) {
                    document.put("acknowledgedBy", pair[1]);
                    continue;
                }

                if (pair[0].equals("creationDate")) {
                    document.put("creationDate", Long.parseLong(pair[1]));
                    continue;
                }

                if (pair[0].equals("id")) {
                    if (pair[1].equals("*")) return new Document();

                    document.put("_id", new ObjectId(pair[1]));
                    continue;
                }

                if (pair[0].equals("start")) {
                    document.put("start", Long.parseLong(pair[1]));
                    continue;
                }

                if (pair[0].equals("end")) {
                    document.put("end", Long.parseLong(pair[1]));
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
