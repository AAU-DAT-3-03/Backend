package dat3.app.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dat3.app.ProjectSettings;
import dat3.app.models.Alarm;
import dat3.app.models.Company;
import dat3.app.models.Misc;
import dat3.app.models.Model;
import dat3.app.models.Service;
import dat3.app.models.User;
import dat3.app.models.Alarm.AlarmBuilder;
import dat3.app.models.Company.CompanyBuilder;
import dat3.app.models.Incident.IncidentBuilder;
import dat3.app.models.Service.ServiceBuilder;
import dat3.app.models.StandardModel;
import dat3.app.models.User.UserBuilder;
import dat3.app.testkit.TestData;

public abstract class MongoUtility {
    private static ProjectSettings settings = ProjectSettings.getProjectSettings();

    public static void wipeDatabaseWithMock() throws Exception {
        MongoClient client = getClient();
        ClientSession session = client.startSession();
        MongoDatabase db = getDatabase(client);
        db.drop(session);
        UserBuilder userBuilder = new UserBuilder();
        MongoCollection<Document> miscColection = getCollection(client, "misc");
        MongoCollection<Document> userCollection = getCollection(client, "users");
        MongoCollection<Document> incidentCollection = getCollection(client, "incidents");
        MongoCollection<Document> companyCollection = getCollection(client, "companies");
        MongoCollection<Document> servicesCollection = getCollection(client, "services");
        MongoCollection<Document> alarmCollection = getCollection(client, "alarms");
        
        if (new Misc().findOne(miscColection, session) == null) {
            Misc misc = new Misc();
            misc.setCaseNumber(10l);
            misc.insertOne(miscColection, session);
        }
        
        for (Company company : TestData.randomCompanies()) {
            company.insertOne(companyCollection, session);
        }
        
        List<Company> companies = MongoUtility.iterableToList(new Company().findMany(companyCollection, session));
        for (Service service : TestData.randomServices()) {
            service.setCompanyId(companies.get(TestData.randomIntExcl(companies.size())).getId());
            service.insertOne(servicesCollection, session);
        }
        
        List<Service> services = MongoUtility.iterableToList(new Service().findMany(servicesCollection, session));
        for (Service service : services) {
            for (int i = 0; i < TestData.randomIntExcl(5); i++) {
                Alarm alarm = TestData.getRandomAlarm();
                alarm.setServiceId(service.getId());
                alarm.insertOne(alarmCollection, session);
            }
        }

        for (User user : TestData.personalizedUsers()) {
            user.insertOne(userCollection, session);
        }
        
        for (User user : TestData.randomValidUsers()) {
            user.insertOne(userCollection, session);
        }
        
        List<User> users = iterableToList(userBuilder.getUser().findMany(userCollection, session));
        Iterator<String> headers = TestData.randomIncidentnames().iterator();
        IncidentBuilder incidentBuilder = new IncidentBuilder();
        for (int i = 0; i < 150; i++) {
            List<String> userIds = new ArrayList<>();
            List<String> calls = new ArrayList<>();
            String acknowledgedBy = null;

            for (int j = 0; j < TestData.randomIntExcl(10); j++) {
                User userToAdd = users.get(TestData.randomIntExcl(users.size()));
                if (j == 0) acknowledgedBy = userToAdd.getId();
                userIds.add(userToAdd.getId());
                if (TestData.randomBoolean()) calls.add(userToAdd.getId());
            }
            
            
            AlarmBuilder alarmBuilder = new AlarmBuilder();
            List<Alarm> alarms = MongoUtility.iterableToList(alarmBuilder.setServiceId(services.get(TestData.randomIntExcl(services.size())).getId()).getAlarm().findMany(alarmCollection, session));
            if (alarms.size() == 0) continue;
            Company company = alarms.size() > 0 ? findCompanyFromAlarm(client, session, servicesCollection, companyCollection, alarms.get(0)) : null; 
            List<String> alarmIds = new ArrayList<>();

            Long caseNumber = Misc.getCaseNumberAndIncrement();
            
            alarms.forEach((Alarm alarm) -> {
                if (alarmIds.size() == 0) {
                    alarmIds.add(alarm.getId());
                }
                else if (TestData.randomBoolean()) alarmIds.add(alarm.getId());
            });

            incidentBuilder
                .setAcknowledgedBy(acknowledgedBy)
                .setAlarmIds(alarmIds)
                .setCallIds(calls)
                .setCompanyId(company != null ? company.getId() : null)
                .setCreationDate(System.currentTimeMillis())
                .setCaseNumber(caseNumber)
                .setHeader(headers.hasNext() ? headers.next() : null)
                .setIncidentNote(acknowledgedBy != null ? "Data" : null)
                .setPriority(TestData.randomIntExcl(4) + 1)
                .setResolved(acknowledgedBy != null ? TestData.randomIntExcl(3) == 0 : false)
                .setUserIds(userIds)
                .getIncident().insertOne(incidentCollection, session);
        }

        session.close();
        client.close();
    }

    private static Company findCompanyFromAlarm(MongoClient client, ClientSession session, MongoCollection<Document> serviceCollection, MongoCollection<Document> companyCollection, Alarm alarm) {
        ServiceBuilder serviceBuilder = new ServiceBuilder();
        CompanyBuilder companyBuilder = new CompanyBuilder();
        try {
            Service service = serviceBuilder.setId(alarm.getServiceId()).getService().findOne(serviceCollection, session);
            Company company = companyBuilder.setId(service.getCompanyId()).getCompany().findOne(companyCollection, session);
            return  company;
        } catch (Exception e) {
            return null;
        }
    }

    public static MongoClient getClient() {
        return MongoClients.create(settings.getDbConnectionString());
    }

    public static MongoCollection<Document> getCollection(MongoClient client, String collectionName) {
        return client.getDatabase(settings.getDbName()).getCollection(collectionName);
    }

    public static MongoDatabase getDatabase(MongoClient client) {
        return client.getDatabase(settings.getDbName());
    }

    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T item : iterable) {
            list.add(item);
        }
        return list;
    }

    public static <T extends Model<T>> List<Document> modelsToDocs(List<T> models) {
        List<Document> docs = new ArrayList<>(models.size());
        for (T model : models) {
            docs.add(model.toDocument());
        }
        return docs;
    }

    public static <T extends StandardModel<T>> List<Document> iterableToDocs(Iterable<T> iterable) {
        List<Document> list = new ArrayList<>();
        for (T t : iterable) {
            list.add(t.toDocument());
        }
        return list;
    }
}
