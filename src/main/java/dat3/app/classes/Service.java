package dat3.app.classes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Service {
    private String name;
    private Map<String, Incident> currentAlarms;
    private Map<String, Incident> previousAlarms;
    private Map<String, Case> currentCases;
    private Map<String, Case> previousCases;
    // alt muligt data?

    public Service(String serviceName) {
        this.name = serviceName;
        this.currentAlarms = new HashMap<>();
        this.previousAlarms = new HashMap<>();
        this.currentCases = new HashMap<>();
        this.previousCases = new HashMap<>();
    }

    public void CreateAlarm() {
        String ID = UUID.randomUUID().toString();
        Incident newIncident = new Incident(ID);
        currentAlarms.put(ID, newIncident);
    }

    public void RemoveAlarm(String alarmID) {
        Incident incident = currentAlarms.get(alarmID);
        currentAlarms.remove(alarmID);
        previousAlarms.put(alarmID, incident);
    }

    public void CreateCase() {
        String ID = UUID.randomUUID().toString();
        Case newCase = new Case(ID);
        this.currentCases.put(ID, newCase);
    }

    public void RemoveCase(String caseID) {
        Case removedCase = currentCases.get(caseID);
        this.currentCases.remove(caseID);
        this.previousCases.put(caseID, removedCase);
    }

    public void UpdateService() {

    }

    public Incident findAlarm(String alarmID) {
        return currentAlarms.get(alarmID);
    }
}