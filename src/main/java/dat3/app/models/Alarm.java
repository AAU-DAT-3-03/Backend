package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Alarm extends StandardModel<Alarm> {
    private String id = null;
    private String name = null;
    private String serviceId = null;
    private String alarmNote = null;

    // ---------- Getters & Setters ---------- //
    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
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
    
    public String getAlarmNote() {
        return alarmNote;
    }
    
    public void setAlarmNote(String alarmNote) {
        this.alarmNote = alarmNote;
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

        public AlarmBuilder setAlarmNote(String alarmNote) {
            alarm.setAlarmNote(alarmNote);
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
        if (this.id != null) document.put("_id", new ObjectId(this.id));
        if (this.name != null) document.put("name", this.name);
        if (this.serviceId != null) document.put("serviceId", new ObjectId(this.serviceId));
        if (this.alarmNote != null) document.put("alarmNote", new ObjectId(this.alarmNote));
        return document;
    }

    @Override
    public Alarm fromDocument(Document document) {
        Alarm alarm = new Alarm();
        if (document.containsKey("_id")) alarm.id = document.getObjectId("_id").toHexString();
        if (document.containsKey("name")) alarm.name = document.getString("name");
        if (document.containsKey("serviceId")) alarm.serviceId = document.getObjectId("serviceId").toHexString();
        if (document.containsKey("alarmNote")) alarm.alarmNote = document.getObjectId("alarmNote").toHexString();
        return alarm;
    }

    // ---------- Static Methods ---------- //
    
    // ---------- Object Methods ---------- //
}