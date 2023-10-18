package dat3.app.classes;

public class Worker {
    private String name;
    private Boolean onCall = false;
    private Boolean onDuty = false;

    public Worker(String name) {
        this.name = name;
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
}