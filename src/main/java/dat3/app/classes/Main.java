package dat3.app.classes;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // TODO lav et hashmap eller noget over services og workers
        Server server = new Server("localhost", 31415);
        server.addGetRoute("/", exchange -> {
            String response = "Hello from Index!";
            try {
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            server.startServer();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void AcknowledgeAlarm(String serviceName, String alarmID, String workerName) {
        Service service = null; // TODO Find i hashmap/database eller whatever når det bliver implementeret
        Alarm alarm = service.findAlarm(alarmID);
        Worker worker = null; // TODO Find i hashmap/database eller whatever når det bliver implementeret
        alarm.AcknowledgeAlarm(worker);
    }

    public void ResolveAlarm(String serviceName, String alarmID) {
        Service service = null; // TODO Find i hashmap/database eller whatever når det bliver implementeret
        service.RemoveAlarm(alarmID);
    }

    public void ChangeAlarmPriority(String serviceName, String alarmID, int newPriority) {
        Service service = null; // TODO Find i hashmap/database eller whatever når det bliver implementeret
        Alarm alarm = service.findAlarm(alarmID);
        alarm.UpdatePriority(newPriority);
    }
}