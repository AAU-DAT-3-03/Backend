package dat3.app.classes;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private Date date;
    private Worker user;
    private String message;

    //Updating priority
    public Event(Worker user, int newPriority) {
        this.date = new Date();
        this.user = user;
    }
    public void UpdatePriority(int newPriority) {
        this.message = this.user.GetName() + " changed the priority to " + newPriority;
    }

    //Adding users
    public void AddUsers(ArrayList<Worker> addedUsers) {
        StringBuilder message = new StringBuilder(this.user.GetName() + " added the following users:");
        for (Worker addedUser : addedUsers) {
            message.append(" ").append(addedUser.GetName());
        }
        message.append(".");
        this.message = message.toString();
    }

    //Removing users
    public void RemoveUsers(ArrayList<Worker> removedUsers) {
        StringBuilder message = new StringBuilder(this.user.GetName() + " removed the following users:");
        for (Worker removedUser : removedUsers) {
            message.append(" ").append(removedUser.GetName());
        }
        message.append(".");
        this.message = message.toString();
    }

    //Resolving incidents
    public void ResolveIncident() {
        this.message = this.user.GetName() + " has marked this incident as resolved.";
    }
}
