package dat3.app.classes;

import java.util.UUID;

public class Worker {
    private String name;
    private String employeeID;
    private Boolean onCall = false;
    private Boolean onDuty = false;

    public Worker(String name) {
        this.name = name;
        this.employeeID = UUID.randomUUID().toString();
    }

    public String GetName() {
        return name;
    }

    public void GoOnDuty() {
        this.onDuty = true;
    }

    public void GoOffDuty() {
        this.onDuty = false;
    }

    public void GoOnCall() {
        this.onCall = true;
    }

    public void GoOffCall() {
        this.onCall = false;
    }

    public String getID() {
        return this.employeeID;
    }
}