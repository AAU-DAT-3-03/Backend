package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class User extends StandardModel<User> {
    private ObjectId _id = null;
    private String email = null;
    private String name = null;
    private Boolean onCall = null;
    private Boolean onDuty = null;

    @Override
    public Document toDocument() {
        Document document = new Document();
        if (this._id != null) document.put("_id", this._id);
        if (this.email != null) document.put("email", this.email);
        if (this.name != null) document.put("name", this.name);
        if (this.onCall != null) document.put("onCall", this.onCall);
        if (this.onDuty != null) document.put("onDuty", this.onDuty);
        return document;
    }

    @Override
    public User fromDocument(Document document) {
        User user = new User();
        if (document.containsKey("_id")) user._id = document.getObjectId("_id");
        if (document.containsKey("email")) user.email = document.getString("email");
        if (document.containsKey("name")) user.name = document.getString("name");
        if (document.containsKey("onCall")) user.onCall = document.getBoolean("onCall");
        if (document.containsKey("onDuty")) user.onDuty = document.getBoolean("onDuty");
        return user;
    }
}
