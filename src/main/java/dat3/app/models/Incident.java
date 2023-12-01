package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpExchange;

import dat3.app.models.Alarm.AlarmBuilder;
import dat3.app.models.Company.CompanyBuilder;
import dat3.app.models.User.UserBuilder;
import dat3.app.utility.ExchangeUtility;
import dat3.app.utility.MongoUtility;

import java.util.ArrayList;
import java.util.List;

public class Incident extends StandardModel<Incident> {
    private Integer priority = null;
    private Boolean resolved = null;
    private String header = null;
    private String acknowledgedBy = null;
    private Long creationDate = null;
    private Long caseNumber = null;
    private String id = null;
    private String companyId = null;
    private List<String> userIds = null;
    private List<String> alarmIds = null;
    private List<String> callIds = null;
    private String incidentNote = null;
    private List<Event> eventLog = null;

    // ---------- Getters & Setters ---------- //
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public void setAcknowledgedBy(String _id) {
        this.acknowledgedBy = _id;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(Long caseNumber) {
        this.caseNumber = caseNumber;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> users) {
        this.userIds = users;
    }

    public List<String> getAlarmIds() {
        return alarmIds;
    }

    public void setAlarmIds(List<String> alarms) {
        this.alarmIds = alarms;
    }

    public List<String> getCallIds() {
        return callIds;
    }

    public void setCallIds(List<String> calls) {
        this.callIds = calls;
    }

    public String getIncidentNote() {
        return incidentNote;
    }

    public void setIncidentNote(String incidentNote) {
        this.incidentNote = incidentNote;
    }

    public List<Event> getEventLog() {
        return this.eventLog;
    }

    public void setEventLog(List<Event> eventLog) {
        this.eventLog = eventLog;
    }

    // ---------- Builder subclass ---------- //
    public static class IncidentBuilder {
        private Incident incident = new Incident();

        public IncidentBuilder setPriority(Integer priority) {
            incident.setPriority(priority);
            return this;
        }

        public IncidentBuilder setResolved(Boolean resolved) {
            incident.setResolved(resolved);
            return this;
        }

        public IncidentBuilder setHeader(String header) {
            incident.setHeader(header);
            return this;
        }

        public IncidentBuilder setAcknowledgedBy(String acknowledgedBy) {
            incident.setAcknowledgedBy(acknowledgedBy);
            return this;
        }

        public IncidentBuilder setCreationDate(Long creationDate) {
            incident.setCreationDate(creationDate);
            return this;
        }

        public IncidentBuilder setCaseNumber(Long caseNumber) {
            incident.setCaseNumber(caseNumber);
            return this;
        }

        public IncidentBuilder setId(String id) {
            incident.setId(id);
            return this;
        }

        public IncidentBuilder setCompanyId(String id) {
            incident.setCompanyId(id);
            return this;
        }

        public IncidentBuilder setUserIds(List<String> users) {
            incident.setUserIds(users);
            return this;
        }

        public IncidentBuilder setAlarmIds(List<String> alarms) {
            incident.setAlarmIds(alarms);
            return this;
        }

        public IncidentBuilder setCallIds(List<String> calls) {
            incident.setCallIds(calls);
            return this;
        }

        public IncidentBuilder setIncidentNote(String incidentNote) {
            incident.setIncidentNote(incidentNote);
            return this;
        }

        public IncidentBuilder setEventLog(List<Event> eventLog) {
            this.incident.setEventLog(eventLog);
            return this;
        }

        public Incident getIncident() {
            Incident temp = this.incident;
            this.incident = new Incident();
            return temp;
        }
    }

    // ---------- Abstract method overrides ---------- //
    @Override
    public Document toDocument() {
        Document document = new Document();
        if (this.priority != null)
            document.append("priority", this.priority);
        if (this.resolved != null)
            document.append("resolved", this.resolved);
        if (this.header != null)
            document.append("header", this.header);
        if (this.acknowledgedBy != null)
            document.append("acknowledgedBy", new ObjectId(this.acknowledgedBy));
        if (this.creationDate != null)
            document.append("creationDate", this.creationDate);
        if (this.caseNumber != null)
            document.append("caseNumber", this.caseNumber);
        if (this.id != null)
            document.append("_id", new ObjectId(this.id));
        if (this.companyId != null)
            document.append("companyId", new ObjectId(this.companyId));
        if (this.incidentNote != null)
            document.append("incidentNote", this.incidentNote);
        if (this.userIds != null) {
            List<ObjectId> ids = new ArrayList<>();
            this.userIds.forEach((String hexString) -> {
                ids.add(new ObjectId(hexString));
            });
            document.append("users", ids);
        }
        if (this.alarmIds != null) {
            List<ObjectId> ids = new ArrayList<>();
            this.alarmIds.forEach((String hexString) -> {
                ids.add(new ObjectId(hexString));
            });
            document.append("alarms", ids);
        }
        if (this.callIds != null) {
            List<ObjectId> ids = new ArrayList<>();
            this.callIds.forEach((String hexString) -> {
                ids.add(new ObjectId(hexString));
            });
            document.append("calls", ids);
        }
        if (this.eventLog != null) {
            List<Document> eventLogList = new ArrayList<>();
            for (Event event : this.eventLog) {
                eventLogList.add(event.toDocument());
            }
            document.append("eventLog", eventLogList);
        }
        return document;
    }

    @Override
    public Incident fromDocument(Document document) {
        Incident incident = new Incident();
        if (document.containsKey("priority"))
            incident.priority = document.getInteger("priority");
        if (document.containsKey("resolved"))
            incident.resolved = document.getBoolean("resolved");
        if (document.containsKey("header"))
            incident.header = document.getString("header");
        if (document.containsKey("acknowledgedBy"))
            incident.acknowledgedBy = document.getObjectId("acknowledgedBy").toHexString();
        if (document.containsKey("creationDate"))
            incident.creationDate = document.getLong("creationDate");
        if (document.containsKey("caseNumber"))
            incident.caseNumber = document.getLong("caseNumber");
        if (document.containsKey("_id"))
            incident.id = document.getObjectId("_id").toHexString();
        if (document.containsKey("companyId"))
            incident.companyId = document.getObjectId("companyId").toHexString();
        if (document.containsKey("incidentNote"))
            incident.incidentNote = document.getString("incidentNote");

        if (document.containsKey("users")) {
            incident.userIds = new ArrayList<>();
            document.getList("users", ObjectId.class).forEach((ObjectId id) -> {
                incident.userIds.add(id.toHexString());
            });
        }
        if (document.containsKey("alarms")) {
            incident.alarmIds = new ArrayList<>();
            document.getList("alarms", ObjectId.class).forEach((ObjectId id) -> {
                incident.alarmIds.add(id.toHexString());
            });
            ;
        }
        if (document.containsKey("calls")) {
            incident.callIds = new ArrayList<>();
            document.getList("calls", ObjectId.class).forEach((ObjectId id) -> {
                incident.callIds.add(id.toHexString());
            });
        }
        if (document.containsKey("eventLog")) {
            incident.eventLog = new ArrayList<>();
            document.getList("eventLog", Event.class).forEach((Event event) -> {
                incident.eventLog.add(event);
            });
        }

        return incident;
    }

    // ---------- Object Methods ---------- //
    @Override
    public Incident clone() {
        IncidentBuilder builder = new IncidentBuilder();

        Long caseNumber = Misc.getCaseNumberAndIncrement();
        if (caseNumber == null)
            return null;

        List<String> newAlarmIds = new ArrayList<>();
        alarmIds.forEach((String id) -> {
            newAlarmIds.add(id);
        });

        List<String> newCallIds = new ArrayList<>();
        callIds.forEach((String id) -> {
            newCallIds.add(id);
        });

        List<String> newUserIds = new ArrayList<>();
        userIds.forEach((String id) -> {
            newUserIds.add(id);
        });

        builder.setAcknowledgedBy(acknowledgedBy)
                .setAlarmIds(newAlarmIds)
                .setCallIds(newCallIds)
                .setCompanyId(companyId)
                .setCreationDate(creationDate)
                .setHeader(header)
                .setIncidentNote(incidentNote)
                .setPriority(priority)
                .setResolved(resolved)
                .setUserIds(newUserIds);
        return builder.getIncident();
    }

    public IncidentPublic toPublic() {
        return Incident.toPublic(this);
    }

    public Incident mergeIncident(Incident subIncident) {
        return Incident.mergeIncidents(this, subIncident);
    }

    // ---------- Static Methods ---------- //
    public static MergeBody parseMergeBody(HttpExchange exchange) {
        try {
            return ExchangeUtility.parseJsonBody(exchange, 1000, MergeBody.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static PutBody parseBodyPut(HttpExchange exchange) {
        try {
            return ExchangeUtility.parseJsonBody(exchange, 1000, PutBody.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static Incident parseBody(HttpExchange exchange) {
        try {
            return ExchangeUtility.parseJsonBody(exchange, 1000, Incident.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Incident> filterByPeriod(List<Incident> incidents, Long start, Long end) {
        List<Incident> result = new ArrayList<>();
        incidents.forEach((Incident incident) -> {
            if (start != null) {
                try {
                    if (incident.getCreationDate() < start) {
                        return;
                    }
                } catch (Exception exception) {
                    return;
                }
            }
            if (end != null) {
                try {
                    if (incident.getCreationDate() > end) {
                        return;
                    }
                } catch (Exception exception) {
                    return;
                }
            }

            result.add(incident);
        });
        return result;
    }

    private static Incident mergeIncidents(Incident first, Incident second) {
        Incident merged = first.clone();
        if (merged == null)
            return null;

        if (merged.getAcknowledgedBy() == null)
            merged.setAcknowledgedBy(second.getAcknowledgedBy());

        Long caseNumber = Misc.getCaseNumberAndIncrement();
        if (caseNumber == null)
            return null;
        merged.setCaseNumber(caseNumber);

        second.getAlarmIds().forEach((String id) -> {
            if (!merged.getAlarmIds().contains(id))
                merged.getAlarmIds().add(id);
        });
        second.getCallIds().forEach((String id) -> {
            if (!merged.getCallIds().contains(id))
                merged.getCallIds().add(id);
        });
        second.getUserIds().forEach((String id) -> {
            if (!merged.getUserIds().contains(id))
                merged.getUserIds().add(id);
        });

        merged.setIncidentNote(merged.getIncidentNote() != null ? merged.getIncidentNote()
                : "" + "\n" + second.getIncidentNote() != null ? second.getIncidentNote() : "");

        return merged;
    }

    public static void updateCallsAndUsers(Incident filter, PutBody toUpdate) {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> incidentCollection = MongoUtility.getCollection(client, "incidents");
                Incident incident = filter.findOne(incidentCollection, session);

                // Doesn't check if the user id is ACTUALLY a user...
                if (toUpdate.getAddCalls() != null) {
                    List<String> calls = incident.getCallIds();
                    if (calls == null)
                        calls = new ArrayList<>();
                    for (String userId : toUpdate.getAddCalls()) {
                        if (!calls.contains(userId))
                            calls.add(userId);
                    }
                    incident.setCallIds(calls);
                }

                if (toUpdate.getRemoveCalls() != null) {
                    List<String> calls = incident.getCallIds();
                    if (calls == null)
                        calls = new ArrayList<>();
                    for (String userId : toUpdate.getRemoveCalls()) {
                        calls.remove(userId);
                    }
                    incident.setCallIds(calls);
                }

                if (toUpdate.getAddUsers() != null) {
                    List<String> users = incident.getUserIds();
                    if (users == null)
                        users = new ArrayList<>();
                    for (String userId : toUpdate.getAddUsers()) {
                        if (!users.contains(userId))
                            users.add(userId);
                    }
                    incident.setUserIds(users);
                }

                if (toUpdate.getRemoveUsers() != null) {
                    List<String> users = incident.getUserIds();
                    if (users == null)
                        users = new ArrayList<>();
                    for (String userId : toUpdate.getRemoveUsers()) {
                        users.remove(userId);
                    }
                    incident.setUserIds(users);
                }

                toUpdate.setCallIds(incident.getCallIds());
                toUpdate.setUserIds(incident.getUserIds());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
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

    // ---------- Static Classes ---------- //
    public static class PutBody extends Incident {
        private List<String> addUsers;
        private List<String> removeUsers;
        private List<String> addCalls;
        private List<String> removeCalls;

        public List<String> getAddUsers() {
            return addUsers;
        }

        public void setAddUsers(List<String> addUsers) {
            this.addUsers = addUsers;
        }

        public List<String> getRemoveUsers() {
            return removeUsers;
        }

        public void setRemoveUsers(List<String> removeUsers) {
            this.removeUsers = removeUsers;
        }

        public List<String> getAddCalls() {
            return addCalls;
        }

        public void setAddCalls(List<String> addCalls) {
            this.addCalls = addCalls;
        }

        public List<String> getRemoveCalls() {
            return removeCalls;
        }

        public void setRemoveCalls(List<String> removeCalls) {
            this.removeCalls = removeCalls;
        }
    }

    public static class MergeBody {
        private String first;
        private String second;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }
    }

    public static class IncidentPublic extends Incident {
        private List<User> calls;
        private List<Alarm> alarms;
        private List<User> users;
        private Company companyPublic;
        private User acknowledgedByPublic;

        public Company getCompanyPublic() {
            return companyPublic;
        }

        public void setCompanyPublic(Company companyPublic) {
            this.companyPublic = companyPublic;
        }

        public User getAcknowledgedByPublic() {
            return acknowledgedByPublic;
        }

        public void setAcknowledgedByPublic(User acknowledgedByPublic) {
            this.acknowledgedByPublic = acknowledgedByPublic;
        }

        public List<User> getCallsPublic() {
            return calls;
        }

        public void setCallsPublic(List<User> calls) {
            this.calls = calls;
        }

        public List<Alarm> getAlarmsPublic() {
            return alarms;
        }

        public void setAlarmsPublic(List<Alarm> alarms) {
            this.alarms = alarms;
        }

        public List<User> getUsersPublic() {
            return users;
        }

        public void setUsersPublic(List<User> users) {
            this.users = users;
        }
    }
}