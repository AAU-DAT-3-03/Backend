package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Alarm extends StandardModel<Alarm> {
    private ObjectId _id = null;
    private String name = null;
    private ObjectId serviceId = null;

    // ---------- Getters & Setters ---------- //
    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getServiceId() {
        return serviceId;
    }

    public void setServiceId(ObjectId serviceId) {
        this.serviceId = serviceId;
    }

    // ---------- Builder subclass ---------- //
    public static class AlarmBuilder  {
        private Alarm alarm = new Alarm();

        public AlarmBuilder setId(ObjectId id) {
            alarm.setId(id);
            return this;
        }

        public AlarmBuilder setName(String name) {
            alarm.setName(name);
            return this;
        }

        public AlarmBuilder setServiceId(ObjectId serviceId) {
            alarm.setServiceId(serviceId);
            return this;
        }

        public Alarm getAlarm() {
            Alarm temp = this.alarm;
            this.alarm = new Alarm();
            return temp;
        }
    }

    // ---------- Abstract method overrides ---------- //
    @Override
    public Document toDocument() {
        Document document = new Document();
        if (this._id != null) document.put("_id", this._id);
        if (this.name != null) document.put("name", this.name);
        if (this.serviceId != null) document.put("serviceId", this.serviceId);
        return document;
    }

    @Override
    public Alarm fromDocument(Document document) {
        Alarm alarm = new Alarm();
        if (document.containsKey("_id")) alarm._id = document.getObjectId("_id");
        if (document.containsKey("name")) alarm.name = document.getString("name");
        if (document.containsKey("serviceId")) alarm.serviceId = document.getObjectId("serviceId");
        return alarm;
    }

    // ---------- Static Methods ---------- //
    
    // ---------- Object Methods ---------- //
}