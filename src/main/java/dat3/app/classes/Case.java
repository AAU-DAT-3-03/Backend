package dat3.app.classes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Case {
    private String caseID;
    private Map<String, Alarm> alarms;
    private Map<String, Worker> currentWorkers;
    private Date creationDate;

    public Case(String caseID) {
        this.creationDate = new Date();
        this.caseID = caseID;
        this.alarms = new HashMap<>();
        this.currentWorkers = new HashMap<>();
    }

    public void AddAlarm(Alarm newAlarm) {
        this.alarms.put(newAlarm.getID(), newAlarm);
    }

    public void removeAlarm(Alarm removedAlarm) {
        this.alarms.remove(removedAlarm.getID());
    }

    public void AddWorker(Worker worker) {
        this.currentWorkers.put(worker.getID(), worker);
    }

    public void RemoveWorker(Worker worker) {
        this.currentWorkers.remove(worker.getID());
    }
}
