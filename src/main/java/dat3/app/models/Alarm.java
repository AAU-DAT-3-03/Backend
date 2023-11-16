package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Alarm extends StandardModel<Alarm> {
    private String _id = null;
    private String name = null;
    private String serviceId = null;

    // ---------- Getters & Setters ---------- //
    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    // ---------- Builder subclass ---------- //
    public static class AlarmBuilder  {
        private Alarm alarm = new Alarm();

        public AlarmBuilder setId(String id) {
            alarm.setId(id);
            return this;
        }

        public AlarmBuilder setName(String name) {
            alarm.setName(name);
            return this;
        }

        public AlarmBuilder setServiceId(String serviceId) {
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
        if (this._id != null) document.put("_id", new ObjectId(this._id));
        if (this.name != null) document.put("name", this.name);
        if (this.serviceId != null) document.put("serviceId", new ObjectId(this.serviceId));
        return document;
    }

    @Override
    public Alarm fromDocument(Document document) {
        Alarm alarm = new Alarm();
        if (document.containsKey("_id")) alarm._id = document.getObjectId("_id").toHexString();
        if (document.containsKey("name")) alarm.name = document.getString("name");
        if (document.containsKey("serviceId")) alarm.serviceId = document.getObjectId("serviceId").toHexString();
        return alarm;
    }

    // ---------- Static Methods ---------- //
    
    // ---------- Object Methods ---------- //
}