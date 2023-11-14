package dat3.app.models;

import com.google.gson.Gson;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dat3.app.ProjectSettings;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

public class Incident extends StandardModel<Incident> {
    private int priority = 0;
    private boolean acknowledged = false;
    private ObjectId acknowledgedBy;
    private Date creationDate;
    private String _id;
    private ObjectId[] users;

    private ObjectId[] alarms;
    private ObjectId[] eventLog;

    public Incident(String incidentName) {
        this.creationDate = new Date();
        this._id = incidentName;
    }

    public void AcknowledgeAlarm() {

    }

    public void UpdatePriority(int newPriority) {
        this.priority = newPriority;
    }


    public String GetID() {
        return this._id;
    }

    public User GetAcknowledgedBy() {
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        MongoCollection<Document> userCollection = db.getCollection("user");
        Document filter = new Document();
        filter.put("ObjectID", this.acknowledgedBy);
        Document userDocument = userCollection.find(filter).first();
        return new Gson().fromJson(userDocument.toJson(), User.class);
    }

    public ArrayList<User> GetUsers() {
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

    public ArrayList<Alarm> GetAlarms() {
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

    public ArrayList<Event> GetEventLog() {
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
    @Override
    public Document toDocument(){
        Document document = new Document();
        document.append("Priority", this.priority);
        document.append("Acknowledged", this.acknowledged);
        if (this.acknowledgedBy != null) document.append("AcknowledgedBy", this.acknowledged);
        if (this.creationDate != null) document.append("Date", this.creationDate);
        if (this._id != null) document.append("ID", this._id);
        if (this.users != null) document.append("Users", this.users);
        if (this.alarms != null) document.append("Alarms", this.alarms);
        if (this.eventLog!= null) document.append("Eventlog", this.eventLog);
        return document;
    }
    @Override
    public Incident fromDocument(Document document) {
        return new Incident("dsa");
    }

    public Document toDocumentFormatted() {
        Document document = new Document();
        document.append("Priority", this.priority);
        document.append("Acknowledged", this.acknowledged);
        if (this.acknowledgedBy != null) document.append("AcknowledgedBy", this.GetAcknowledgedBy());
        if (this.creationDate != null) document.append("Date", this.creationDate);
        if (this._id != null) document.append("ID", this._id);
        if (this.users != null) document.append("Users", this.GetUsers());
        if (this.alarms != null) document.append("Alarms", this.GetAlarms());
        if (this.eventLog!= null) document.append("Eventlog", this.GetEventLog());
        return document;
    }
}