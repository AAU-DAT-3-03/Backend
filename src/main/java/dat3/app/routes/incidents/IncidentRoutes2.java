package dat3.app.routes.incidents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dat3.app.models.Event;
import dat3.app.models.Event.EventBuilder;
import dat3.app.models.Incident.IncidentBuilder;
import dat3.app.models.Incident.IncidentPublic;
import dat3.app.models.Incident.PutBody;
import dat3.app.models.Incident.MergeBody;
import dat3.app.models.User;
import dat3.app.models.User.UserBuilder;
import dat3.app.server.Auth;

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
import dat3.app.models.Misc;
import dat3.app.models.Alarm.AlarmBuilder;
import dat3.app.models.Company.CompanyBuilder;
import dat3.app.server.Response;
import dat3.app.utility.ExchangeUtility;
import dat3.app.utility.MongoUtility;

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

        Long end = documentFilter.getLong("end");
        Long start = documentFilter.getLong("start");
        result = Incident.filterByPeriod(result, start, end);

        List<IncidentPublic> resultPublic = new ArrayList<>();
        result.forEach((Incident incident) -> {
            resultPublic.add(incident.toPublic());
        });

        Response response = new Response();
        response.setMsg(resultPublic);
        response.setStatusCode(0);
        try {
            response.sendResponse(exchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        Incident filter = Incident.parseBody(exchange);
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
        } catch (IOException e) {
        }
    }

    public static void put(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        PutBody toUpdate = Incident.parseBodyPut(exchange);
        if (toUpdate == null || toUpdate.getId() == null || toUpdate.getUserIds() != null
                || toUpdate.getCaseNumber() != null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }
        Document change = toUpdate.toDocument();

        Incident filter = new Incident();
        filter.setId(toUpdate.getId());

        // fetches incident before it changes for the eventlog
        Incident incidentBeforeChange = null;
        try {
            incidentBeforeChange = ExchangeUtility.defaultGetOperation(filter, "incidents").get(0);
        } catch(Exception e) {
            System.out.println("Couldn't find incident in DB for eventlog");
        }
        String eventLogMessage = "Error";
        if(change.containsKey("priority") && incidentBeforeChange != null) eventLogMessage = "Priority changed from: " + incidentBeforeChange.getPriority().toString() + " to " + toUpdate.getPriority().toString();
        if(change.containsKey("resolved") && incidentBeforeChange != null) eventLogMessage = "Incident marked as resolved";
        if(change.containsKey("header") && incidentBeforeChange != null) eventLogMessage = "Header changed from: " + incidentBeforeChange.getHeader() + " to: " + toUpdate.getHeader();
        if(change.containsKey("acknowledgedBy") && incidentBeforeChange != null) eventLogMessage = "Incident marked as acknowledged";
        if(change.containsKey("incidentNote") && incidentBeforeChange != null) eventLogMessage = "Note changed from: " + incidentBeforeChange.getIncidentNote() + " to: " + toUpdate.getIncidentNote();
        if(toUpdate.getAddUsers() != null && incidentBeforeChange != null) {
            eventLogMessage = "Added users: ";
            for (String addUserId: toUpdate.getAddUsers()) {
                User userFilter = new UserBuilder().setId(addUserId).getUser();
                eventLogMessage += ExchangeUtility.defaultGetOperation(userFilter, "users").get(0).getName();
                eventLogMessage += ", ";
            }
            eventLogMessage = eventLogMessage.substring(0, eventLogMessage.length()-2);
            eventLogMessage += ".";
        }
        if(toUpdate.getRemoveUsers() != null && incidentBeforeChange != null) {
            eventLogMessage = "Removed users: ";
            for (String removeUserId: toUpdate.getRemoveUsers()) {
                User userFilter = new UserBuilder().setId(removeUserId).getUser();
                eventLogMessage += ExchangeUtility.defaultGetOperation(userFilter, "users").get(0).getName();
                eventLogMessage += ", ";
            }
            eventLogMessage = eventLogMessage.substring(0, eventLogMessage.length()-2);
            eventLogMessage += ".";
        }
        if(toUpdate.getAddCalls() != null && incidentBeforeChange != null) {
            eventLogMessage = "Called users: ";
            for (String addUserId: toUpdate.getAddCalls()) {
                User userFilter = new UserBuilder().setId(addUserId).getUser();
                eventLogMessage += ExchangeUtility.defaultGetOperation(userFilter, "users").get(0).getName();
                eventLogMessage += ", ";
            }
            eventLogMessage = eventLogMessage.substring(0, eventLogMessage.length()-2);
            eventLogMessage += ".";
        }
        if(toUpdate.getRemoveCalls() != null && incidentBeforeChange != null) {
            eventLogMessage = "Removed called users: ";
            for (String removeUserId: toUpdate.getRemoveCalls()) {
                User userFilter = new UserBuilder().setId(removeUserId).getUser();
                eventLogMessage += ExchangeUtility.defaultGetOperation(userFilter, "users").get(0).getName();
                eventLogMessage += ", ";
            }
            eventLogMessage = eventLogMessage.substring(0, eventLogMessage.length()-2);
            eventLogMessage += ".";
        }

        toUpdate.setId(null);

        // Change the toUpdate such that it contains the users that are specified by
        // addUsers, removeUsers, addCalls, removeCalls.
        Incident.updateCallsAndUsers(filter, toUpdate);

        UpdateResult result = ExchangeUtility.defaultPutOperation(filter, toUpdate, "incidents");
        if (result == null) {
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }
        try {
            User user = Auth.auth(exchange);
            Event event = new EventBuilder().setAffectedObjectId(filter.getId()).setMessage(eventLogMessage)
                    .setDate(new Date().getTime()).setUserId(user.getId()).setUserName(user.getName()).getEvent();
            ExchangeUtility.defaultPostOperation(event, "events");
        } catch (Exception e) {
            System.out.println("Couldnt log an event for incident" + toUpdate.getId());
        }

        Response response = new Response();
        if (result.getModifiedCount() == 0) {
            response.setMsg("Did not modify any objects.");
            response.setStatusCode(1);
        } else {
            response.setMsg("Modified object successfully.");
            response.setStatusCode(0);
        }

        try {
            response.sendResponse(exchange);
        } catch (IOException e) {
        }
    }

    public static void post(HttpExchange exchange) {
        if (Auth.auth(exchange) == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        Incident filter = Incident.parseBody(exchange);
        if (filter == null || filter.getId() != null || filter.getCaseNumber() != null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }
        Long caseNumber = Misc.getCaseNumberAndIncrement();
        if (caseNumber == null) {
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }

        filter.setCaseNumber(caseNumber);

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
        } catch (IOException e) {
        }
    }

    public static void merge(HttpExchange exchange) {
        User user = Auth.auth(exchange);
        if (user == null) {
            ExchangeUtility.sendUnauthorizedResponse(exchange);
            return;
        }

        MergeBody mergeBody = Incident.parseMergeBody(exchange);
        if (mergeBody.getFirst() == null || mergeBody.getSecond() == null) {
            ExchangeUtility.invalidQueryResponse(exchange);
            return;
        }

        Incident mergedIncident;
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> incidentCollection = MongoUtility.getCollection(client, "incidents");
                MongoCollection<Document> eventCollection = MongoUtility.getCollection(client, "events");
                MongoCollection<Document> companyCollection = MongoUtility.getCollection(client, "companies");

                IncidentBuilder incidentBuilder = new IncidentBuilder();
                Incident first = incidentBuilder.setId(mergeBody.getFirst()).getIncident().findOne(incidentCollection,
                        session);
                Incident second = incidentBuilder.setId(mergeBody.getSecond()).getIncident().findOne(incidentCollection,
                        session);

                Incident merged = first.mergeIncident(second);

                first.setResolved(true);
                second.setResolved(true);
                first.updateOne(incidentCollection, session, incidentBuilder.setId(first.getId()).getIncident());
                second.updateOne(incidentCollection, session, incidentBuilder.setId(second.getId()).getIncident());

                merged.insertOne(incidentCollection, session);
                mergedIncident = merged.findOne(incidentCollection, session);
                if (mergedIncident == null)
                    throw new Exception("New incident wasn't inserted correctly.");
                // Get all events from first incident and copy over to merged incident
                try {
                    Event eventFilter = new EventBuilder().setAffectedObjectId(first.getId()).getEvent();
                    List<Event> eventLog = ExchangeUtility.defaultGetOperation(eventFilter, "events");
                    for (Event event : eventLog) {
                        event.setId(null);
                        event.setAffectedObjectId(mergedIncident.getId());
                        event.insertOne(eventCollection, session);
                    }
                } catch (Exception ignored) {
                }
                // Get all events from second incident and copy over to merged incident
                try {
                    Event eventFilter = new EventBuilder().setAffectedObjectId(second.getId()).getEvent();
                    List<Event> eventLog = ExchangeUtility.defaultGetOperation(eventFilter, "events");
                    for (Event event : eventLog) {
                        event.setId(null);
                        event.setAffectedObjectId(mergedIncident.getId());
                        event.insertOne(eventCollection, session);
                    }
                } catch (Exception e) {
                }
                String eventCompanyId = mergedIncident.getCompanyId();
                String eventCompanyName = new CompanyBuilder().setId(eventCompanyId).getCompany().findOne(companyCollection, session).getName();
                String firstCaseNumber = first.getCaseNumber().toString();
                String secondCaseNumber = second.getCaseNumber().toString();
                String firstCaseMessage = "Incident merged with " + eventCompanyName + " #" + firstCaseNumber;
                String secondCaseMessage = "Incident merged with " + eventCompanyName + " #" + secondCaseNumber;
                Long time = new Date().getTime();
                Event newEventFirst = new EventBuilder().setAffectedObjectId(first.getId()).setUserId(user.getId()).setMessage(firstCaseMessage).setUserName(user.getName()).setDate(time).getEvent();
                Event newEventSecond = new EventBuilder().setAffectedObjectId(second.getId()).setUserId(user.getId()).setMessage(secondCaseMessage).setUserName(user.getName()).setDate(time).getEvent();
                newEventFirst.insertOne(eventCollection, session);
                newEventSecond.insertOne(eventCollection, session);
                String mergedMessage = "Incident created by merging " + firstCaseMessage + " and " + secondCaseMessage;
                Event newEventMerged = new EventBuilder().setAffectedObjectId(mergedIncident.getId()).setUserId(user.getId()).setUserName(user.getName()).setMessage(mergedMessage).setDate(time).getEvent();
                newEventMerged.insertOne(eventCollection, session);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExchangeUtility.queryExecutionErrorResponse(exchange);
            return;
        }

        IncidentPublic mergedIncidentPublic = toPublic(mergedIncident);

        Response response = new Response();
        response.setMsg(mergedIncidentPublic);
        response.setStatusCode(0);
        try {
            response.sendResponse(exchange);
        } catch (IOException e) {
        }
    }

    private static IncidentPublic toPublic(Incident incident) {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                MongoCollection<Document> companyCollection = MongoUtility.getCollection(client, "companies");
                MongoCollection<Document> alarmCollection = MongoUtility.getCollection(client, "alarms");
                UserBuilder userBuilder = new UserBuilder();
                CompanyBuilder companyBuilder = new CompanyBuilder();
                AlarmBuilder alarmBuilder = new AlarmBuilder();

                IncidentPublic pub = new IncidentPublic();

                pub.setAcknowledgedByPublic(
                        userBuilder.setId(incident.getAcknowledgedBy()).getUser().findOne(userCollection, session));
                pub.setAlarmsPublic(new ArrayList<>());
                incident.getAlarmIds().forEach((String id) -> {
                    try {
                        pub.getAlarmsPublic().add(alarmBuilder.setId(id).getAlarm().findOne(alarmCollection, session));
                    } catch (Exception e) {
                    }
                });
                pub.setCallsPublic(new ArrayList<>());
                incident.getCallIds().forEach((String id) -> {
                    try {
                        pub.getCallsPublic().add(userBuilder.setId(id).getUser().findOne(userCollection, session));
                    } catch (Exception e) {
                    }
                });
                pub.setCaseNumber(incident.getCaseNumber());
                pub.setCompanyPublic(
                        companyBuilder.setId(incident.getCompanyId()).getCompany().findOne(companyCollection, session));
                pub.setCreationDate(incident.getCreationDate());
                pub.setHeader(incident.getHeader());
                pub.setId(incident.getId());
                pub.setIncidentNote(incident.getIncidentNote());
                pub.setPriority(incident.getPriority());
                pub.setResolved(incident.getResolved());
                pub.setUsersPublic(new ArrayList<>());
                incident.getUserIds().forEach((String id) -> {
                    try {
                        pub.getUsersPublic().add(userBuilder.setId(id).getUser().findOne(userCollection, session));
                    } catch (Exception e) {
                    }
                });

                return pub;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Document parseQueryString(HttpExchange exchange) {
        try {
            Document document = new Document();
            String[] tuples = exchange.getRequestURI().getQuery().split("&");
            for (String tuple : tuples) {
                String[] pair = tuple.split("=");

                if (pair[0].equals("end")) {
                    document.put("end", Long.parseLong(pair[1]));
                    continue;
                }

                if (pair[0].equals("start")) {
                    document.put("start", Long.parseLong(pair[1]));
                    continue;
                }

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

                if (pair[0].equals("caseNumber")) {
                    document.put("caseNumber", Long.parseLong(pair[1]));
                    continue;
                }

                if (pair[0].equals("id")) {
                    if (pair[1].equals("*"))
                        return new Document();

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
