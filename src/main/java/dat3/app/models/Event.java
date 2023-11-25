package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Event extends StandardModel<Event>{
    private String _id = null;
    private Long date = null;
    private String userId = null;
    private String message = null;
    private String affectedObjectId = null;
    @Override
    public String getId() {
        return this._id;
    }
    @Override
    public void setId(String id) {
        this._id = id;
    }
    public Long getDate() {
        return this.date;
    }
    public void setDate(Long date) {
        this.date = date;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getAffectedObjectId() {
        return this.affectedObjectId;
    }
    public void setAffectedObjectId(String affectedObjectId) {
        this.affectedObjectId = affectedObjectId;
    }
    public static class EventBuilder{
        private Event event = new Event();
        public EventBuilder setDate(Long date) {
            this.event.setDate(date);
            return this;
        }
        public EventBuilder setUserId(String userId) {
            this.event.setUserId(userId);
            return this;
        }
        public EventBuilder setMessage(String message) {
            this.event.setMessage(message);
            return this;
        }
        public EventBuilder setAffectedObjectId(String affectedObjectId) {
            this.event.affectedObjectId = affectedObjectId;
            return this;
        }
        public Event getEvent() {
            Event temp = this.event;
            this.event = new Event();
            return temp;
        }

    }
    @Override
    public Document toDocument() {
        System.out.println("redas");
        Document document = new Document();
        if (this.date != null) document.append("date", this.date);
        System.out.println("dsa");
        if (this.userId != null) document.append("userId", new ObjectId(this.userId));
        System.out.println("da");
        if (this.message != null) document.append("message", this.message);
        System.out.println("s");
        if (this.affectedObjectId != null) document.append("affectedObjectId", new ObjectId(this.affectedObjectId));
        System.out.println("21");
        return document;
    }
    @Override
    public Event fromDocument(Document document) {
        Event event = new Event();
        if (document.containsKey("date")) event.date = document.getLong("date");
        if (document.containsKey("userId")) event.userId = document.getObjectId("userId").toHexString();
        if (document.containsKey("message")) event.message = document.getString("message");
        if (document.containsKey("affectedObjectId")) event.affectedObjectId = document.getObjectId("affectedObjectId").toHexString();
        return event;
    }
}
