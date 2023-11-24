package dat3.app.routes.incidents;

import java.util.ArrayList;
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

import dat3.app.models.Incident;
import dat3.app.models.User;
import dat3.app.models.Incident.IncidentBuilder;
import dat3.app.models.User.UserBuilder;
import dat3.app.server.Auth;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;
import dat3.app.utility.MongoUtility;

public class IncidentRoutes {
    public static void getIncident(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> incidentCollection = MongoUtility.getCollection(client, "incidents");
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                Response response = new Response();

                Document docFilter = parseQueryString(exchange);
                try {
                    if (docFilter.getString("_id").equals("*")) docFilter.remove("_id");
                } catch (Exception e) {}
                Incident filter = new Incident().fromDocument(docFilter);
                if (filter == null) {
                    response.setMsg("Couldn't parse query string.");
                    response.setStatusCode(1);
                    response.sendResponse(exchange);
                    return;
                }

                List<Incident> incidents = MongoUtility.iterableToList(filter.findMany(incidentCollection, session));
                incidents = filterByPeriod(incidents, docFilter);
                List<IncidentPublic> toSend = new ArrayList<>();
                incidents.forEach((Incident incident) -> {
                    toSend.add(IncidentPublic.fromIncident(userCollection, session, incident));
                });

                response.setMsg(toSend);
                response.setStatusCode(0);
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

    public static void deleteIncident(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> incidentCollection = MongoUtility.getCollection(client, "incidents");
                Response response = new Response();
                IncidentBuilder builder = new IncidentBuilder();

                Incident filter = ExchangeUtility.parseJsonBody(exchange, 1000, Incident.class);
                if (filter != null && filter.getId() == null) filter = null;
                else if (filter != null && filter.getId() == "*") filter = null;
                filter = builder.setId(filter.getId()).getIncident(); 

                if (filter == null) {
                    response.setMsg("Couldn't parse query string.");
                    response.setStatusCode(1);
                    response.sendResponse(exchange);
                    return;
                }

                DeleteResult result = filter.deleteOne(incidentCollection, session);

                if (result.getDeletedCount() > 0) {
                    response.setMsg("Successfully deleted incident.");
                    response.setStatusCode(0);
                    response.sendResponse(exchange);
                } else {
                    response.setMsg("Deleted 0 incidents.");
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

    public static void putIncident(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> incidentCollection = MongoUtility.getCollection(client, "incidents");
                Response response = new Response();
                IncidentBuilder builder = new IncidentBuilder();

                Incident valuesToUpdate = ExchangeUtility.parseJsonBody(exchange, 1000, Incident.class);
                if (valuesToUpdate != null && valuesToUpdate.getId() == null) valuesToUpdate = null;
                else if (valuesToUpdate != null && valuesToUpdate.getId() == "*") valuesToUpdate = null;

                if (valuesToUpdate == null) {
                    response.setMsg("Couldn't parse query string.");
                    response.setStatusCode(1);
                    response.sendResponse(exchange);
                    return;
                }

                Incident filter = builder.setId(valuesToUpdate.getId()).getIncident();
                UpdateResult result = valuesToUpdate.updateOne(incidentCollection, session, filter);

                if (result.getModifiedCount() > 0) {
                    response.setMsg("Successfully modified incident.");
                    response.setStatusCode(0);
                    response.sendResponse(exchange);
                } else {
                    response.setMsg("Modified 0 incidents.");
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

    public static void postIncident(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> incidentCollection = MongoUtility.getCollection(client, "incidents");
                Response response = new Response();

                Incident incidentToInsert = ExchangeUtility.parseJsonBody(exchange, 1000, Incident.class);
                if (incidentToInsert != null && incidentToInsert.getId() != null) incidentToInsert = null;

                if (incidentToInsert == null) {
                    response.setMsg("Couldn't parse query string.");
                    response.setStatusCode(1);
                    response.sendResponse(exchange);
                    return;
                }

                InsertOneResult result = incidentToInsert.insertOne(incidentCollection, session);

                if (result.wasAcknowledged()) {
                    response.setMsg(result.getInsertedId().asObjectId().getValue().toHexString());
                    response.setStatusCode(0);
                    response.sendResponse(exchange);
                } else {
                    response.setMsg("Created 0 incidents.");
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

    private static List<Incident> filterByPeriod(List<Incident> incidents, Document docFilter) {
        List<Incident> parsedIncidents = new ArrayList<>();
        Long end = docFilter.getLong("end");
        Long start = docFilter.getLong("start");

        if (docFilter.containsKey("start") && !docFilter.containsKey("end")) {
            if (start == null) return incidents;
            
            for (Incident incident : incidents) {
                if (start < incident.getCreationDate()) {
                    parsedIncidents.add(incident);
                }
            }
            return parsedIncidents;
        } else if (!docFilter.containsKey("start") && docFilter.containsKey("end")) {
            if (end == null) return incidents;
            
            for (Incident incident : incidents) {
                if (incident.getCreationDate() < end) {
                    parsedIncidents.add(incident);
                }
            }
            return parsedIncidents;
        } else if (docFilter.containsKey("start") && docFilter.containsKey("end")) {
            if (start == null || end == null) return incidents;

            for (Incident incident : incidents) {
                if (start < incident.getCreationDate() && incident.getCreationDate() < end) {
                    parsedIncidents.add(incident);
                }
            }
            return parsedIncidents;
        }
        
        return incidents;
    }

    private static Document parseQueryString(HttpExchange exchange) {
        try {
            boolean isEmpty = true;
            Document document = new Document();
            String[] pairs = exchange.getRequestURI().getQuery().split("&");
            for (String string : pairs) {
                String[] pair = string.split("=");

                if (pair[0].equals("priority")) {
                    document.put("priority", Integer.parseInt(pair[1]));
                    isEmpty = false;
                    continue;
                }

                if (pair[0].equals("header")) {
                    document.put("header", pair[1]);
                    isEmpty = false;
                    continue;
                }

                if (pair[0].equals("acknowledgedBy")) {
                    document.put("acknowledgedBy", new ObjectId(pair[1]));
                    isEmpty = false;
                    continue;
                }

                if (pair[0].equals("creationDate")) {
                    document.put("creationDate", Long.parseLong(pair[1]));
                    isEmpty = false;
                    continue;
                }

                if (pair[0].equals("id")) {
                    if (pair[1].equals("*")) {
                        document.put("_id", "*");
                        isEmpty = false;
                        continue;
                    };
                    document.put("_id", new ObjectId(pair[1]));
                    isEmpty = false;
                    continue;
                }

                if (pair[0].equals("start")) {
                    document.put("start", Long.parseLong(pair[1]));
                    isEmpty = false;
                    continue;
                }

                if (pair[0].equals("end")) {
                    document.put("end", Long.parseLong(pair[1]));
                    isEmpty = false;
                    continue;
                }

                return null;
            }
            
            if (isEmpty) return null;
            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
class IncidentPublic {
    private Integer priority = null;
    private Boolean resolved = null;
    private String header = null;
    private User acknowledgedBy = null;
    private Long creationDate = null;
    private String id = null;
    private List<User> users = null;
    private List<User> calls = null;
    private String incidentNote = null;

    public String getIncidentNote() {
        return incidentNote;
    }

    public List<User> getCalls() {
        return calls;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getHeader() {
        return header;
    }

    public User getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public String getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public static IncidentPublic fromIncident(MongoCollection<Document> userCollection, ClientSession session, Incident incident) {
        try {
            UserBuilder builder = new UserBuilder();

            IncidentPublic incidentPublic = new IncidentPublic();
            incidentPublic.id = incident.getId();
            if (incident.getAcknowledgedBy() != null)  {
                incidentPublic.acknowledgedBy = builder.setId(incident.getAcknowledgedBy()).getUser().findOne(userCollection, session);
                incidentPublic.acknowledgedBy.setPassword(null);
            }
            incidentPublic.creationDate = incident.getCreationDate();
            incidentPublic.header = incident.getHeader();
            incidentPublic.priority = incident.getPriority();
            incidentPublic.resolved = incident.getResolved();
            incidentPublic.incidentNote = incident.getIncidentNote();

            incidentPublic.users = new ArrayList<>();
            for (String hex : incident.getUsers()) {
                User user = builder.setId(hex).getUser().findOne(userCollection, session);
                if (user == null) continue;
                user.setPassword(null);
                incidentPublic.users.add(user);
            }

            incidentPublic.calls = new ArrayList<>();
            for (String hex : incident.getCalls()) {
                User user = builder.setId(hex).getUser().findOne(userCollection, session);
                if (user == null) continue;
                user.setPassword(null);
                incidentPublic.calls.add(user);
            }

            return incidentPublic;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}