package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class User extends StandardModel<User> {
    private ObjectId _id = null;
    private String email = null;
    private String password = null;
    private String name = null;
    private Boolean onCall = null;
    private Boolean onDuty = null;

    // ---------- Getters & Setters ---------- //
    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOnCall() {
        return onCall;
    }

    public void setOnCall(Boolean onCall) {
        this.onCall = onCall;
    }

    public Boolean getOnDuty() {
        return onDuty;
    }

    public void setOnDuty(Boolean onDuty) {
        this.onDuty = onDuty;
    }

    // ---------- Builder subclass ---------- //
    public static class UserBuilder {
        private User user = new User();

        public UserBuilder setId(ObjectId _id) {
            user.setId(_id);
            return this;
        }

        public UserBuilder setEmail(String email) {
            user.setEmail(email);
            return this;
        }

        public UserBuilder setPassword(String password) {
            user.setPassword(password);
            return this;
        }

        public UserBuilder setName(String name) {
            user.setName(name);
            return this;
        }

        public UserBuilder setOnCall(Boolean onCall) {
            user.setOnCall(onCall);
            return this;
        }

        public UserBuilder setOnDuty(Boolean onDuty) {
            user.setOnDuty(onDuty);
            return this;
        }

        public User getUser() {
            User temp = this.user;
            this.user = new User();
            return temp;            
        }
    }

    // ---------- Abstract method overrides ---------- //
    @Override
    public Document toDocument() {
        Document document = new Document();
        if (this._id != null) document.put("_id", this._id);
        if (this.email != null) document.put("email", this.email);
        if (this.password != null) document.put("password", this.password);
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
        if (document.containsKey("password")) user.password = document.getString("password");
        if (document.containsKey("name")) user.name = document.getString("name");
        if (document.containsKey("onCall")) user.onCall = document.getBoolean("onCall");
        if (document.containsKey("onDuty")) user.onDuty = document.getBoolean("onDuty");
        return user;
    }
}
