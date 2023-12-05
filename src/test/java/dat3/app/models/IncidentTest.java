package dat3.app.models;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Test;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import dat3.app.utility.MongoUtility;

public class IncidentTest {
    @Test
    void TestIncidentInsertion() {
        Incident incident = new Incident();

        List<String> userIds = Arrays.asList(new String[] {
                "656dd24dd86c1a107a567ed4",
                "656dd24cd86c1a107a567ecd",
                "656dd24ad86c1a107a567e47",
                "656dd24ad86c1a107a567e48",
                "656dd24bd86c1a107a567e5e",
                "656dd24bd86c1a107a567e95",
                "656dd24cd86c1a107a567ec0",
                "656dd24cd86c1a107a567ecd",
        });

        List<String> callIds = Arrays.asList(new String[] {
                "656dd24dd86c1a107a567ed4",
                "656dd24cd86c1a107a567ecd",
                "656dd24ad86c1a107a567e47",
                "656dd24ad86c1a107a567e48",
                "656dd24bd86c1a107a567e5e",
                "656dd24bd86c1a107a567e95",
                "656dd24cd86c1a107a567ec0",
                "656dd24cd86c1a107a567ecd",
        });

        List<String> alarmIds = Arrays.asList(new String[] {
                "656dd24dd86c1a107a567ed4",
                "656dd24cd86c1a107a567ecd",
                "656dd24ad86c1a107a567e47",
                "656dd24ad86c1a107a567e48",
                "656dd24bd86c1a107a567e5e",
                "656dd24bd86c1a107a567e95",
                "656dd24cd86c1a107a567ec0",
                "656dd24cd86c1a107a567ecd",
        });

        incident.setAcknowledgedBy("656dd24dd86c1a107a567ed4");
        incident.setAlarmIds(alarmIds);
        incident.setCallIds(callIds);
        incident.setCaseNumber(10l);
        incident.setCompanyId("656dd24dd86c1a107a567ed4");
        incident.setCreationDate(1701696077276l);
        incident.setHeader("A computer respectfully spun");
        incident.setId("656dd24dd86c1a107a567ed4");
        incident.setIncidentNote("God dag til dig.");
        incident.setPriority(2);
        incident.setResolved(false);
        incident.setUserIds(userIds);

        Incident recIncident = null;
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession clientSession = client.startSession()) {
                MongoCollection<Document> incidentCollection = MongoUtility.getCollection(client, "incidents");
                incident.insertOne(incidentCollection, clientSession);

                Incident filter = new Incident();
                filter.setId("656dd24dd86c1a107a567ed4");

                recIncident = filter.findOne(incidentCollection, clientSession);

                incidentCollection.drop();
            }
        } catch (Exception e) {
        }

        assertTrue(Incident.IncidentEquals(incident, recIncident));
    }

    @Test
    void TestMerging() {
        Incident incident1 = new Incident();
        Incident incident2 = new Incident();
        Incident expected = new Incident();

        List<String> userIds1 = Arrays.asList(new String[] {
                "656dd24dd86c1a107a567ed4",
                "656dd24cd86c1a107a567ecd",
                "656dd24ad86c1a107a567e47",
                "656dd24ad86c1a107a567e48",
                "656dd24bd86c1a107a567e5e",
                "656dd24bd86c1a107a567e95",
                "656dd24cd86c1a107a567ec0",
                "656dd24cd86c1a107a567ecd",
        });
        List<String> userIds2 = Arrays.asList(new String[] {
                "656dd48bd86c1a107a568365",
                "656dd48ad86c1a107a56835a",
                "656dd25ed86c1a107a567fd6",
                "656dd25ed86c1a107a567fd4",
                "656dd25dd86c1a107a567fd2",
        });
        List<String> expectedUserIds = Arrays.asList(new String[] {
                "656dd24dd86c1a107a567ed4",
                "656dd24cd86c1a107a567ecd",
                "656dd24ad86c1a107a567e47",
                "656dd24ad86c1a107a567e48",
                "656dd24bd86c1a107a567e5e",
                "656dd24bd86c1a107a567e95",
                "656dd24cd86c1a107a567ec0",
                "656dd24cd86c1a107a567ecd",
                
                "656dd48bd86c1a107a568365",
                "656dd48ad86c1a107a56835a",
                "656dd25ed86c1a107a567fd6",
                "656dd25ed86c1a107a567fd4",
                "656dd25dd86c1a107a567fd2",
        });


        List<String> callIds1 = Arrays.asList(new String[] {
                "656dd24dd86c1a107a567ed4",
                "656dd24cd86c1a107a567ecd",
                "656dd24ad86c1a107a567e47",
                "656dd24ad86c1a107a567e48",
                "656dd24bd86c1a107a567e5e",
                "656dd24bd86c1a107a567e95",
                "656dd24cd86c1a107a567ec0",
                "656dd24cd86c1a107a567ecd",
        });
        List<String> callIds2 = Arrays.asList(new String[] {
                "656dd48bd86c1a107a568365",
                "656dd48ad86c1a107a56835a",
                "656dd25ed86c1a107a567fd6",
                "656dd25ed86c1a107a567fd4",
                "656dd25dd86c1a107a567fd2",
        });
        List<String> expectedCallIds = Arrays.asList(new String[] {
                "656dd24dd86c1a107a567ed4",
                "656dd24cd86c1a107a567ecd",
                "656dd24ad86c1a107a567e47",
                "656dd24ad86c1a107a567e48",
                "656dd24bd86c1a107a567e5e",
                "656dd24bd86c1a107a567e95",
                "656dd24cd86c1a107a567ec0",
                "656dd24cd86c1a107a567ecd",
                
                "656dd48bd86c1a107a568365",
                "656dd48ad86c1a107a56835a",
                "656dd25ed86c1a107a567fd6",
                "656dd25ed86c1a107a567fd4",
                "656dd25dd86c1a107a567fd2",
        });

        List<String> alarmIds1 = Arrays.asList(new String[] {
                "656dd24dd86c1a107a567ed4",
                "656dd24cd86c1a107a567ecd",
                "656dd24ad86c1a107a567e47",
                "656dd24ad86c1a107a567e48",
                "656dd24bd86c1a107a567e5e",
                "656dd24bd86c1a107a567e95",
                "656dd24cd86c1a107a567ec0",
                "656dd24cd86c1a107a567ecd",
        });
        List<String> alarmIds2 = Arrays.asList(new String[] {
                "656dd48bd86c1a107a568365",
                "656dd48ad86c1a107a56835a",
                "656dd25ed86c1a107a567fd6",
                "656dd25ed86c1a107a567fd4",
                "656dd25dd86c1a107a567fd2",
        });
        List<String> expectedAlarmIds = Arrays.asList(new String[] {
                "656dd24dd86c1a107a567ed4",
                "656dd24cd86c1a107a567ecd",
                "656dd24ad86c1a107a567e47",
                "656dd24ad86c1a107a567e48",
                "656dd24bd86c1a107a567e5e",
                "656dd24bd86c1a107a567e95",
                "656dd24cd86c1a107a567ec0",
                "656dd24cd86c1a107a567ecd",
                
                "656dd48bd86c1a107a568365",
                "656dd48ad86c1a107a56835a",
                "656dd25ed86c1a107a567fd6",
                "656dd25ed86c1a107a567fd4",
                "656dd25dd86c1a107a567fd2",
        });

        incident1.setAcknowledgedBy("656dd24dd86c1a107a567ed4"); // Should prio this!
        incident2.setAcknowledgedBy("656dd24cd86c1a107a567ecd");
        expected.setAcknowledgedBy("656dd24dd86c1a107a567ed4");

        incident1.setAlarmIds(alarmIds1); // Lists a naively merged. 
        incident2.setAlarmIds(alarmIds2);
        expected.setAlarmIds(expectedAlarmIds);

        incident1.setCallIds(callIds1); // Lists a naively merged.
        incident2.setCallIds(callIds2);
        expected.setCallIds(expectedCallIds);

        incident1.setCaseNumber(10l); // Expects a brand new case number. 
        incident2.setCaseNumber(1l);
        expected.setCaseNumber(10000000000000000l);

        incident1.setCompanyId("656dd24dd86c1a107a567ed4"); // Should prio this!
        incident2.setCompanyId("656dd48ad86c1a107a56835a");
        expected.setCompanyId("656dd24dd86c1a107a567ed4");

        incident1.setCreationDate(1701696077276l); // Should prio this!
        incident2.setCreationDate(1701696077l);
        expected.setCreationDate(1701696077276l);

        incident1.setHeader("A computer respectfully spun"); // Should prio this!
        incident2.setHeader("Eva mysteriously crashed");
        expected.setHeader("A computer respectfully spun");

        incident1.setId("656dd24dd86c1a107a567ed4"); // Should be neither of these.  
        incident2.setId("656dd48ad86c1a107a56835a");
        expected.setId("!!!!!!!!!!!!!!");   

        incident1.setIncidentNote("God dag til dig."); // Should merge these with a newline in between. 
        incident2.setIncidentNote("Endnu bedre dag til dig!");
        expected.setIncidentNote("God dag til dig.\nEndnu bedre dag til dig!");  

        incident1.setPriority(2); // Should prio the smallest. 
        incident2.setPriority(2);
        expected.setPriority(2);

        incident1.setResolved(false); // Should prio this!
        incident2.setResolved(true);
        expected.setResolved(false);

        incident1.setUserIds(userIds1); // Lists a naively merged.
        incident2.setUserIds(userIds2);
        expected.setUserIds(expectedUserIds);

        Incident merged = incident1.mergeIncident(incident2);

        boolean isAsExpected = 
            merged.getAcknowledgedBy().equals(expected.getAcknowledgedBy()) &&
            (merged.getCaseNumber() != incident1.getCaseNumber() && merged.getCaseNumber() != incident2.getCaseNumber()) &&
            merged.getCompanyId().equals(expected.getCompanyId()) &&
            merged.getCreationDate().equals(expected.getCreationDate()) &&
            merged.getHeader().equals(expected.getHeader()) &&
            (merged.getId() != incident1.getId() && merged.getId() != incident2.getId()) &&
            merged.getIncidentNote().equals(expected.getIncidentNote()) &&
            merged.getPriority().equals(expected.getPriority()) &&
            merged.getResolved().equals(expected.getResolved());
            
        if (isAsExpected) {
            for (String id : merged.getAlarmIds()) {
                if (!expected.getAlarmIds().contains(id)) {
                    isAsExpected = false;
                    break;
                }
            }
        }
        
        if (isAsExpected) {
            for (String id : merged.getCallIds()) {
                if (!expected.getCallIds().contains(id)) {
                    isAsExpected = false;
                    break;
                }
            }
        }
        
        if (isAsExpected) {
            for (String id : merged.getUserIds()) {
                if (!expected.getUserIds().contains(id)) {
                    isAsExpected = false;
                    break;
                }
            }
        }

        assertTrue(isAsExpected);
    }
}

