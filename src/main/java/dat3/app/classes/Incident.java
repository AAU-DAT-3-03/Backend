package dat3.app.classes;

import java.util.Date;

public class Incident {
    private int priority = 0;
    private boolean acknowledged = false;
    private Worker acknowledgedBy;
    private Date creationDate;
    private String incidentID;

    public Incident(String incidentName) {
        this.creationDate = new Date();
        this.incidentID = incidentName;
    }

    public void AcknowledgeAlarm(Worker worker) {
        this.acknowledged = true;
        this.acknowledgedBy = worker;
    }

    public void UpdatePriority(int newPriority) {
        this.priority = newPriority;
    }


    public String getID() {
        return this.incidentID;
    }
}