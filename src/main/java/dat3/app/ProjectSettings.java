package dat3.app;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Project settings class. Used to read the projectsettings.json file. 
 */
public class ProjectSettings {
    private String hostname = "10.92.0.231";
    private int port = 80;
    private String dbConnectionString = "mongodb://localhost:27017";
    private String dbName = "p3";
    private String certificationPath = "/home/ubuntu/firebasecert.json";

    private ProjectSettings() {}; // Disable the default public() constructor.

    /**
     * Tries to find a projectsettings.json file and read it. All values not found in the file will be equal to the default values specified in the class.
     * @return Returns the projectsettings.json represented by an object. 
     */
    public static ProjectSettings getProjectSettings() {
        Gson gson = new Gson();
        try {
            return gson.fromJson(new FileReader("projectsettings.json"), ProjectSettings.class);
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            return new ProjectSettings();
        }
    }

    public String getCertificationPath() {
        return certificationPath;
    }

    public void setCertificationPath(String certificationPath) {
        this.certificationPath = certificationPath; 
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