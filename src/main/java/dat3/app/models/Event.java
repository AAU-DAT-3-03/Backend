package dat3.app.models;

import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Date;

public class Event extends StandardModel<Event>{
    private Date date;
    private User user;
    private String message;

    //Updating priority
    public Event(User user) {
        this.date = new Date();
        this.user = user;
    }
    public void UpdatePriority(int newPriority) {
        this.message = this.user.getName() + " changed the priority to " + newPriority;
    }

    //Adding users
    public void AddUsers(ArrayList<User> addedUsers) {
        StringBuilder message = new StringBuilder(this.user.getName() + " added the following users:");
        for (User addedUser : addedUsers) {
            message.append(" ").append(addedUser.getName());
        }
        message.append(".");
        this.message = message.toString();
    }

    //Removing users
    public void RemoveUsers(ArrayList<User> removedUsers) {
        StringBuilder message = new StringBuilder(this.user.getName() + " removed the following users:");
        for (User removedUser : removedUsers) {
            message.append(" ").append(removedUser.getName());
        }
        message.append(".");
        this.message = message.toString();
    }

    //Resolving incidents
    public void ResolveIncident() {
        this.message = this.user.getName() + " has marked this incident as resolved.";
    }

    @Override
    public Document toDocument() {
        Document document = new Document();
        if (this.date != null) document.append("date", this.date);
        if (this.user != null) document.append("user", this.user);
        if (this.message != null) document.append("message", this.message);
        return document;
    }
    @Override
    public Event fromDocument(Document document) {
        return new Event(new User());
    }
}
