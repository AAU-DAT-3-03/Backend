package dat3.app.mongo;

import java.io.FileInputStream;
import java.io.IOException;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import dat3.app.jsonutilty.JsonUtility;

public class MongoConnection {
    private String connectionString;
    private MongoClient client;

    private boolean hasTriedToConnect = false;

    public MongoConnection() {
        String jsonText = "";
        try (FileInputStream fs = new FileInputStream(System.getProperty("user.dir") + "/projectsettings.json")) {
            jsonText = new String(fs.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonUtility jsonUtility = new JsonUtility();
        ProjectInfo pInfo = jsonUtility.readJsonString(jsonText, ProjectInfo.class);

        connectionString = pInfo.getDbConnectionString();
    }

    public boolean connectToDb() {
        this.hasTriedToConnect = true;
        try {
            this.client = MongoClients.create(this.connectionString);
            return true;
        } catch (Exception e) {
            this.client = null;
            return false;
        }
    }
}

class ProjectInfo {
    private String dbConnectionString;

    public String getDbConnectionString() {
        return dbConnectionString;
    }
}
