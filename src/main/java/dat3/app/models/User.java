package dat3.app.models;

import org.bson.Document;
import org.bson.types.ObjectId;

public class User extends StandardModel<User> {
    private String id = null;
    private String email = null;
    private String password = null;
    private String name = null;
    private String phoneNumber = null;
    private String team = null;
    private Boolean onCall = null;
    private Boolean onDuty = null;

    // ---------- Getters & Setters ---------- //
    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
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

        public UserBuilder setId(String _id) {
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

        public UserBuilder setPhoneNumber(String phoneNumber) {
            user.setPhoneNumber(phoneNumber);
            return this;
        }

        public UserBuilder setTeam(String team) {
            user.setTeam(team);
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
        if (this.id != null)
            document.put("_id", new ObjectId(this.id));
        if (this.email != null)
            document.put("email", this.email);
        if (this.password != null)
            document.put("password", this.password);
        if (this.name != null)
            document.put("name", this.name);
        if (this.phoneNumber != null)
            document.put("phoneNumber", this.phoneNumber);
        if (this.team != null)
            document.put("team", this.team);
        if (this.onCall != null)
            document.put("onCall", this.onCall);
        if (this.onDuty != null)
            document.put("onDuty", this.onDuty);
        return document;
    }

    @Override
    public User fromDocument(Document document) {
        User user = new User();
        if (document.containsKey("_id"))
            user.id = document.getObjectId("_id").toHexString();
        if (document.containsKey("email"))
            user.email = document.getString("email");
        if (document.containsKey("password"))
            user.password = document.getString("password");
        if (document.containsKey("name"))
            user.name = document.getString("name");
        if (document.containsKey("phoneNumber"))
            user.phoneNumber = document.getString("phoneNumber");
        if (document.containsKey("team"))
            user.team = document.getString("team");
        if (document.containsKey("onCall"))
            user.onCall = document.getBoolean("onCall");
        if (document.containsKey("onDuty"))
            user.onDuty = document.getBoolean("onDuty");
        return user;
    }

    // ---------- Static Methods ---------- //
    public static boolean UserEquals(User user1, User user2) {
        try {
            return user1.getEmail().equals(user2.getEmail()) && user1.getName().equals(user2.getName())
                    && user1.getOnCall().equals(user2.getOnCall()) && user1.getOnDuty().equals(user2.getOnDuty())
                    && user1.getPassword().equals(user2.getPassword())
                    && user1.getPhoneNumber().equals(user2.getPhoneNumber());
        } catch (Exception e) {
            return false;
        }
    }
}
