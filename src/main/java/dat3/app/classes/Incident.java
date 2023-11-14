package dat3.app.classes;

import com.google.gson.Gson;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dat3.app.ProjectSettings;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

public class Incident {
    private int priority = 0;
    private boolean acknowledged = false;
    private ObjectId acknowledgedBy;
    private Date creationDate;
    private String _ID;
    private ObjectId[] users;

    private ObjectId[] alarms;

    public Incident(String incidentName) {
        this.creationDate = new Date();
        this._ID = incidentName;
    }

    public void AcknowledgeAlarm() {

    }

    public void UpdatePriority(int newPriority) {
        this.priority = newPriority;
    }


    public String getID() {
        return this._ID;
    }

    public Worker getAcknowledgedBy() {
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        MongoCollection<Document> workerCollection = db.getCollection("user");
        Document filter = new Document();
        filter.put("ObjectID", this.acknowledgedBy);
        Document workerDocument = workerCollection.find(filter).first();
        return new Gson().fromJson(workerDocument.toJson(), Worker.class);
    }

    public ArrayList<Worker> getWorkers() {
        ArrayList<Worker> workerList = new ArrayList<Worker>();
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        MongoCollection<Document> workerCollection = db.getCollection("user");

        for (ObjectId workerID: this.users) {
            Document filter = new Document();
            filter.put("ObjectID", workerID);
            Document workerDocument = workerCollection.find(filter).first();
            workerList.add(new Gson().fromJson(workerDocument.toJson(), Worker.class));
        }

        return workerList;
    }

    public ArrayList<Alarm> getAlarms() {
        ArrayList<Alarm> alarmList = new ArrayList<Alarm>();
        ProjectSettings settings = ProjectSettings.getProjectSettings();
        MongoDatabase db = MongoClients.create(settings.getDbConnectionString()).getDatabase(settings.getDbName());
        MongoCollection<Document> workerCollection = db.getCollection("Alarm");

        for (ObjectId alarmID: this.alarms) {
            Document filter = new Document();
            filter.put("ObjectID", alarmID);
            Document alarmDocument = workerCollection.find(filter).first();
            alarmList.add(new Gson().fromJson(alarmDocument.toJson(), Alarm.class));
        }

        return alarmList;
    }

    public Document toDocument(){
        Document document = new Document();
        document.append("Priority", this.priority);
        document.append("Acknowledged", this.acknowledged);
        document.append("AcknowledgedBy", this.getAcknowledgedBy());
        document.append("Date", this.creationDate);
        document.append("ID", this._ID);
        document.append("Users", this.getWorkers());
        document.append("Alarms", this.getAlarms());
        return document;
    }
}