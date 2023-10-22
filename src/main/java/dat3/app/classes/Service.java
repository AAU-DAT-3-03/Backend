package dat3.app.classes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Service {
    private String name;
    private Map<String, Alarm> currentAlarms;
    private Map<String, Alarm> previousAlarms;
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
        Alarm newAlarm = new Alarm(ID);
        currentAlarms.put(ID, newAlarm);
    }

    public void RemoveAlarm(String alarmID) {
        Alarm alarm = currentAlarms.get(alarmID);
        currentAlarms.remove(alarmID);
        previousAlarms.put(alarmID, alarm);
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

    public Alarm findAlarm(String alarmID) {
        return currentAlarms.get(alarmID);
    }
}