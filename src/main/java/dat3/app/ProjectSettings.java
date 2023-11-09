package dat3.app;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class ProjectSettings {
    private String hostname = "10.92.0.231";
    private int port = 80;
    private String dbConnectionString = "mongodb://localhost:27017";
    private String dbName = "p3";

    private ProjectSettings() {}; // Disable the default public() constructor.

    public static ProjectSettings getProjectSettings() {
        Gson gson = new Gson();
        try {
            return gson.fromJson(new FileReader("projectsettings.json"), ProjectSettings.class);
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbConnectionString() {
        return dbConnectionString;
    }

    public void setDbConnectionString(String dbConnectionString) {
        this.dbConnectionString = dbConnectionString;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}