package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Incident extends StandardModel<Incident> {
    private Integer priority = null;
    private Boolean resolved = null;
    private String header = null;
    private String acknowledgedBy = null;
    private Long creationDate = null;
    private String id = null;
    private List<String> users = null;
    private List<String> alarms = null;
    private List<String> calls = null;
    private String incidentNote = null;

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
    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<String> getUsers() {
        return users;
    }
    public void setUsers(List<String> users) {
        this.users = users;
    }
    public List<String> getAlarms() {
        return alarms;
    }
    public void setAlarms(List<String> alarms) {
        this.alarms = alarms;
    }
    public List<String> getCalls() {
        return calls;
    }
    public void setCalls(List<String> calls) {
        this.calls = calls;
    }
    public String getIncidentNote() {
        return incidentNote;
    }
    public void setIncidentNote(String incidentNote) {
        this.incidentNote = incidentNote;
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
        public IncidentBuilder setId(String id) {
            incident.setId(id);
            return this;
        }
        public IncidentBuilder setUsers(List<String> users) {
            incident.setUsers(users);
            return this;
        }
        public IncidentBuilder setAlarms(List<String> alarms) {
            incident.setAlarms(alarms);
            return this;
        }
        public IncidentBuilder setCalls(List<String> calls) {
            incident.setCalls(calls);
            return this;
        }
        public IncidentBuilder setIncidentNote(String incidentNote) {
            incident.setIncidentNote(incidentNote);
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
    public Document toDocument(){
        Document document = new Document();
        if (this.priority != null) document.append("priority", this.priority);
        if (this.resolved != null) document.append("resolved", this.resolved);
        if (this.header != null) document.append("header", this.header);
        if (this.acknowledgedBy != null) document.append("acknowledgedBy", new ObjectId(this.acknowledgedBy));
        if (this.creationDate != null) document.append("creationDate", this.creationDate);
        if (this.id != null) document.append("_id", new ObjectId(this.id));
        if (this.incidentNote != null) document.append("incidentNote", this.incidentNote);
        if (this.users != null) {
            List<ObjectId> ids = new ArrayList<>();
            this.users.forEach((String hexString) -> {
                ids.add(new ObjectId(hexString));
            });
            document.append("users", ids);
        }
        if (this.alarms != null) {
            List<ObjectId> ids = new ArrayList<>();
            this.alarms.forEach((String hexString) -> {
                ids.add(new ObjectId(hexString));
            });
            document.append("alarms", ids);
        }
        if (this.calls != null) {
            List<ObjectId> ids = new ArrayList<>();
            this.calls.forEach((String hexString) -> {
                ids.add(new ObjectId(hexString));
            });
            document.append("calls", ids);
        }

        return document;
    }
    @Override
    public Incident fromDocument(Document document) {
        Incident incident = new Incident();
        if (document.containsKey("priority")) incident.priority = document.getInteger("priority");
        if (document.containsKey("resolved")) incident.resolved = document.getBoolean("resolved");
        if (document.containsKey("header")) incident.header = document.getString("header");
        if (document.containsKey("acknowledgedBy")) incident.acknowledgedBy = document.getObjectId("acknowledgedBy").toHexString();
        if (document.containsKey("creationDate")) incident.creationDate = document.getLong("creationDate");
        if (document.containsKey("_id")) incident.id = document.getObjectId("_id").toHexString();
        if (document.containsKey("incidentNote")) incident.incidentNote = document.getString("incidentNote");
        
        if (document.containsKey("users")) {
            incident.users = new ArrayList<>();
            document.getList("users", ObjectId.class).forEach((ObjectId id) -> {
                incident.users.add(id.toHexString());
            });
        }
        if (document.containsKey("alarms")) {
            incident.alarms = new ArrayList<>();
            document.getList("alarms", ObjectId.class).forEach((ObjectId id) -> {
                incident.alarms.add(id.toHexString());
            });;
        }
        if (document.containsKey("calls")) {
            incident.calls = new ArrayList<>();
            document.getList("calls", ObjectId.class).forEach((ObjectId id) -> {
                incident.calls.add(id.toHexString());
            });
        }
        
        return incident;
    }
    // ---------- Object Methods ---------- //
    // public Document toDocumentFormatted() {
    //     Document document = new Document();
    //     if (this.priority != null) document.append("Priority", this.priority);
    //     if (this.acknowledgedBy != null) document.append("AcknowledgedBy", this.GetAcknowledgedBy());
    //     if (this.creationDate != null) document.append("Date", this.creationDate);
    //     if (this._id != null) document.append("ID", this._id);
    //     if (this.users != null) document.append("Users", this.GetUsers());
    //     if (this.alarms != null) document.append("Alarms", this.GetAlarms());
    //     if (this.eventLog!= null) document.append("Eventlog", this.GetEventLog());
    //     return document;
    // }
    // private User GetAcknowledgedBy() {
    //     ProjectSettings settings = ProjectSettings.getProjectSettings();
    //     MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
    //     MongoCollection<Document> userCollection = db.getCollection("user");
    //     Document filter = new Document();
    //     filter.put("ObjectID", this.acknowledgedBy);
    //     Document userDocument = userCollection.find(filter).first();
    //     return new Gson().fromJson(userDocument.toJson(), User.class);
    // }
    // // private ArrayList<User> GetUsers() {
    //     ArrayList<User> userList = new ArrayList<>();
    //     ProjectSettings settings = ProjectSettings.getProjectSettings();
    //     MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
    //     MongoCollection<Document> userCollection = db.getCollection("user");

    //     for (ObjectId userID: this.users) {
    //         Document filter = new Document();
    //         filter.put("ObjectID", userID);
    //         Document userDocument = userCollection.find(filter).first();
    //         userList.add(new Gson().fromJson(userDocument.toJson(), User.class));
    //     }

    //     return userList;
    // }

    // private ArrayList<Alarm> GetAlarms() {
    //     ArrayList<Alarm> alarmList = new ArrayList<>();
    //     ProjectSettings settings = ProjectSettings.getProjectSettings();
    //     MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
    //     MongoCollection<Document> alarmCollection = db.getCollection("Alarm");

    //     for (ObjectId alarmID: this.alarms) {
    //         Document filter = new Document();
    //         filter.put("ObjectID", alarmID);
    //         Document alarmDocument = alarmCollection.find(filter).first();
    //         alarmList.add(new Gson().fromJson(alarmDocument.toJson(), Alarm.class));
    //     }

    //     return alarmList;
    // }
    // private ArrayList<Event> GetEventLog() {
    //     ArrayList<Event> eventList = new ArrayList<>();
    //     ProjectSettings settings = ProjectSettings.getProjectSettings();
    //     MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
    //     MongoCollection<Document> eventCollection = db.getCollection("event");

    //     for (ObjectId eventID: this.eventLog) {
    //         Document filter = new Document();
    //         filter.put("ObjectID", eventID);
    //         Document eventDocument = eventCollection.find(filter).first();
    //         eventList.add(new Gson().fromJson(eventDocument.toJson(), Event.class));
    //     }

    //     return eventList;
    // }
}