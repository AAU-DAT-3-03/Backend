package dat3.server;

import java.util.Date;

public class Alarm {
    private int priority = 0;
    private boolean acknowledged = false;
    private Worker acknowledgedBy;
    private Worker[] currentWorkers;
    private Date creationDate;
    private String alarmID;

    public Alarm(String alarmName) {
        this.creationDate = new Date();
        this.alarmID = alarmName;
    }

    public void AcknowledgeAlarm(Worker worker) {
        this.acknowledged = true;
        this.acknowledgedBy = worker;
    }

    public void UpdatePriority(int newPriority) {
        this.priority = newPriority;
    }

    public void AddWorker() {
    }

    public void RemoveWorker() {

    }
}
