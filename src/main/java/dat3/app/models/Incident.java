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
    private Long caseNumber = null;
    private String id = null;
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
    public Document toDocument(){
        Document document = new Document();
        if (this.priority != null) document.append("priority", this.priority);
        if (this.resolved != null) document.append("resolved", this.resolved);
        if (this.header != null) document.append("header", this.header);
        if (this.acknowledgedBy != null) document.append("acknowledgedBy", new ObjectId(this.acknowledgedBy));
        if (this.creationDate != null) document.append("creationDate", this.creationDate);
        if (this.caseNumber != null) document.append("caseNumber", this.caseNumber);
        if (this.id != null) document.append("_id", new ObjectId(this.id));
        if (this.incidentNote != null) document.append("incidentNote", this.incidentNote);
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
        if (document.containsKey("priority")) incident.priority = document.getInteger("priority");
        if (document.containsKey("resolved")) incident.resolved = document.getBoolean("resolved");
        if (document.containsKey("header")) incident.header = document.getString("header");
        if (document.containsKey("acknowledgedBy")) incident.acknowledgedBy = document.getObjectId("acknowledgedBy").toHexString();
        if (document.containsKey("creationDate")) incident.creationDate = document.getLong("creationDate");
        if (document.containsKey("caseNumber")) incident.caseNumber = document.getLong("caseNumber");
        if (document.containsKey("_id")) incident.id = document.getObjectId("_id").toHexString();
        if (document.containsKey("incidentNote")) incident.incidentNote = document.getString("incidentNote");
        
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
            });;
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
}