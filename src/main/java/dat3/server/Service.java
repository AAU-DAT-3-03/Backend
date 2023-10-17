package dat3.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Service {
    private String name;
    private Map<String, Alarm> currentAlarms;
    private Map<String, Alarm> previousAlarms;
    // alt muligt data?

    public Service(String serviceName) {
        this.name = serviceName;
        this.currentAlarms = new HashMap<>();
        this.previousAlarms = new HashMap<>();
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

    public void UpdateService() {

    }

    public Alarm findAlarm(String alarmID) {
        return currentAlarms.get(alarmID);
    }
}
