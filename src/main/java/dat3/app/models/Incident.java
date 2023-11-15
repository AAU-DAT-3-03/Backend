package dat3.app.models;

import com.google.gson.Gson;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dat3.app.ProjectSettings;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Incident extends StandardModel<Incident> {
    private Integer priority = null;
    private String header = null;
    private ObjectId acknowledgedBy = null;
    private Long creationDate = null;
    private ObjectId _id = null;
    private List<ObjectId> users = null;
    private List<ObjectId> alarms = null;
    private List<ObjectId> eventLog = null;

    // ---------- Getters & Setters ---------- //
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public ObjectId getAcknowledgedBy() {
        return acknowledgedBy;
    }
    public void setAcknowledgedBy(ObjectId _id) {
        this.acknowledgedBy = _id;
    }
    public Long getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }
    public ObjectId getId() {
        return _id;
    }
    public void setId(ObjectId _id) {
        this._id = _id;
    }
    public List<ObjectId> getUsers() {
        return users;
    }
    public void setUsers(List<ObjectId> users) {
        this.users = users;
    }
    public List<ObjectId> getAlarms() {
        return alarms;
    }
    public void setAlarms(List<ObjectId> alarms) {
        this.alarms = alarms;
    }
    public List<ObjectId> getEventLog() {
        return eventLog;
    }
    public void setEventLog(List<ObjectId> eventLog) {
        this.eventLog = eventLog;
    }
    // ---------- Builder subclass ---------- //
    public static class IncidentBuilder {
        private Incident incident = new Incident();
        public IncidentBuilder setPriority(int priority) {
            incident.setPriority(priority);
            return this;
        }
        public IncidentBuilder setHeader(String header) {
            incident.setHeader(header);
            return this;
        }
        public IncidentBuilder setAcknowledgedBy(ObjectId acknowledgedBy) {
            incident.setAcknowledgedBy(acknowledgedBy);
            return this;
        }
        public IncidentBuilder setCreationDate(Long creationDate) {
            incident.setCreationDate(creationDate);
            return this;
        }
        public IncidentBuilder setId(ObjectId _id) {
            incident.setId(_id);
            return this;
        }
        public IncidentBuilder setUsers(List<ObjectId> users) {
            incident.setUsers(users);
            return this;
        }
        public IncidentBuilder setAlarms(List<ObjectId> alarms) {
            incident.setAlarms(alarms);
            return this;
        }
        public IncidentBuilder setEventLog(List<ObjectId> eventLog) {
            incident.setEventLog(eventLog);
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
        if (this.header != null) document.append("header", this.header);
        if (this.acknowledgedBy != null) document.append("acknowledgedBy", this.acknowledgedBy);
        if (this.creationDate != null) document.append("creationDate", this.creationDate);
        if (this._id != null) document.append("_id", this._id);
        if (this.users != null) document.append("users", this.users);
        if (this.alarms != null) document.append("alarms", this.alarms);
        if (this.eventLog!= null) document.append("eventlog", this.eventLog);
        return document;
    }
    @Override
    public Incident fromDocument(Document document) {
        Incident incident = new Incident();
        if (document.containsKey("priority")) incident.priority = document.getInteger("priority");
        if (document.containsKey("header")) incident.header = document.getString("header");
        if (document.containsKey("acknowledgedBy")) incident.acknowledgedBy = document.getObjectId("acknowledgedBy");
        if (document.containsKey("creationDate")) incident.creationDate = document.getLong("creationDate");
        if (document.containsKey("_id")) incident._id = document.getObjectId("_id");
        if (document.containsKey("users")) incident.users = document.getList("users", ObjectId.class);
        if (document.containsKey("alarms")) incident.alarms = document.getList("alarms", ObjectId.class);
        if (document.containsKey("eventLog")) incident.eventLog = document.getList("eventLog", ObjectId.class);
        return incident;
    }
    // ---------- Object Methods ---------- //
    public Document toDocumentFormatted() {
        Document document = new Document();
        if (this.priority != null) document.append("Priority", this.priority);
        if (this.acknowledgedBy != null) document.append("AcknowledgedBy", this.GetAcknowledgedBy());
        if (this.creationDate != null) document.append("Date", this.creationDate);
        if (this._id != null) document.append("ID", this._id);
        if (this.users != null) document.append("Users", this.GetUsers());
        if (this.alarms != null) document.append("Alarms", this.GetAlarms());
        if (this.eventLog!= null) document.append("Eventlog", this.GetEventLog());
        return document;
    }
    private User GetAcknowledgedBy() {
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        MongoCollection<Document> userCollection = db.getCollection("user");
        Document filter = new Document();
        filter.put("ObjectID", this.acknowledgedBy);
        Document userDocument = userCollection.find(filter).first();
        return new Gson().fromJson(userDocument.toJson(), User.class);
    }
    private ArrayList<User> GetUsers() {
        ArrayList<User> userList = new ArrayList<>();
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        MongoCollection<Document> userCollection = db.getCollection("user");

        for (ObjectId userID: this.users) {
            Document filter = new Document();
            filter.put("ObjectID", userID);
            Document userDocument = userCollection.find(filter).first();
            userList.add(new Gson().fromJson(userDocument.toJson(), User.class));
        }

        return userList;
    }
    private ArrayList<Alarm> GetAlarms() {
        ArrayList<Alarm> alarmList = new ArrayList<>();
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        MongoCollection<Document> alarmCollection = db.getCollection("Alarm");

        for (ObjectId alarmID: this.alarms) {
            Document filter = new Document();
            filter.put("ObjectID", alarmID);
            Document alarmDocument = alarmCollection.find(filter).first();
            alarmList.add(new Gson().fromJson(alarmDocument.toJson(), Alarm.class));
        }

        return alarmList;
    }
    private ArrayList<Event> GetEventLog() {
        ArrayList<Event> eventList = new ArrayList<>();
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        MongoCollection<Document> eventCollection = db.getCollection("event");

        for (ObjectId eventID: this.eventLog) {
            Document filter = new Document();
            filter.put("ObjectID", eventID);
            Document eventDocument = eventCollection.find(filter).first();
            eventList.add(new Gson().fromJson(eventDocument.toJson(), Event.class));
        }

        return eventList;
    }
}