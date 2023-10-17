package dat3.server;

import java.util.HashMap;
import java.util.Map;

public class Service {
    private String name;
    private Map<String, Alarm> currentAlarms;
    // alt muligt data?
    private Map<String, Alarm> previousAlarms;

    public Service(String serviceName) {
        this.name = serviceName;
        this.currentAlarms = new HashMap<>();
        this.previousAlarms = new HashMap<>();
    }

    public void CreateAlarm() {

    }

    public void RemoveAlarm(String alarmID) {
        Alarm alarm = currentAlarms.get(alarmID);
        currentAlarms.remove(alarmID);
        previousAlarms.put(alarmID, alarm);
    }

    public void UpdateService() {

    }

    public Alarm findAlarms(String alarmID) {
        return currentAlarms.get(alarmID);
    }
}
