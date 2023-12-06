package dat3.app.testkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.Document;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import dat3.app.models.Alarm;
import dat3.app.models.Company;
import dat3.app.models.Incident;
import dat3.app.models.Misc;
import dat3.app.models.Service;
import dat3.app.models.User;
import dat3.app.models.Alarm.AlarmBuilder;
import dat3.app.models.Company.CompanyBuilder;
import dat3.app.models.Service.ServiceBuilder;
import dat3.app.models.User.UserBuilder;
import dat3.app.utility.MongoUtility;

public abstract class TestData2 {
    private static Random random = new Random(System.currentTimeMillis());
    
    private static UserInfo[] personalizedUsers = new UserInfo[] {
        new UserInfo("Mads Byriel", "madshbyriel@gmail.com", "madsbyriel123", "30745907"),
        new UserInfo("Mikkel Helsing", "mikkelhelsing@gmail.com", "mikkelhelsing123", "40342010"),
        new UserInfo("Oliver Nielsen", "olivernielsen@gmail.com", "olivernielsen123", "23404130"), 
        new UserInfo("Rasmus Pedersen", "rasmuspedersen@gmail.com", "rasmuspedersen123", "40592821"),
        new UserInfo("Sandra Rosenbeck", "sandrarosenbeck@gmail.com", "sandrarosenbeck123", "30103243"),
        new UserInfo("Mads Guldbæk", "madsguldbæk@gmail.com", "madsguldbæk123", "30746012"),
        new UserInfo("Joakim Byg", "jby@netic.dk", "joakimbyg123", "30883321"),
        new UserInfo("Allan Højgaard Jensen", "ahj@netic.dk", "allanhøjgaardjensen123", "30203376"),
        new UserInfo("Claus Efternavn", "clausefternavn", "clausefternavn123", "54875621"),
    };

    private static String[] companyNames = new String[] {
        "Intel Corporation",
        "Amazon",
        "Netcompany",
        "Microsoft",
        "Jysk",
        "Wolt",
        "Min Læge",
        "TrendHim",
        "Norli",
    };
    
    private static String[] serviceNames = new String[] {
        "Flyt af systemer og data",
        "E-commerce hosting",
        "Hosting", 
        "Maintenance",
        "Website hosting", 
        "E-handel", 
        "Microsoft 365", 
        "Xbox Danish Webshop",
        "The Danish Road Directorate", 
        "Topdanmark", 
        "Mit.dk",
        "Outsourced Cloud Computing", 
        "Amazon Danish Webshop",
        "Intel Homepage", 
        "Analytics Hosting",
    };
    
    private static String[] alarmNames = new String[] {
        "Lost 321 connections in 2 minutes",
        "OOM-killed 3 times in 40 minutes",
        "ClassNotFoundException",
        "NullReferenceException",
        "Pod restarted 5 times within in a day",
        "Pod has idled for 10 minutes",
    };

    private static String[] teams = new String[] {
        "Cloud Hosting",
        "Cloud Maintenance",
        "Database",
        "Service Desk",
    };
    
    public static List<Company> generateCompanies(int size) {
        List<Company> companies = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Company company = new Company();
            company.setName(companyNames[random.nextInt(companyNames.length)]);
            companies.add(company);
        }

        return companies;
    }

    public static List<Service> generateServices(int size, String companyId) {
        List<Service> services = new ArrayList<>();

        for (int i = 0;  i < size; i++) {
            Service service = new Service();
            service.setCompanyId(companyId);
            service.setName(serviceNames[random.nextInt(serviceNames.length)]);
            services.add(service);
        }
        
        return services;
    }

    public static List<Alarm> generateAlarms(int size, String serviceId) {
        List<Alarm> alarms = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            Alarm alarm = new Alarm();
            alarm.setAlarmNote("");
            alarm.setName(alarmNames[random.nextInt(alarmNames.length)]);
            alarm.setServiceId(serviceId);
            alarms.add(alarm);
        }
        
        return alarms;
    }

    public static List<Incident> generateIncidents(int size) {
        List<Incident> incidents = new ArrayList<>();
        
        List<String> alarmIds = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        List<String> callIds = new ArrayList<>();
        String acknowledgedBy = null;
        String companyId = null;
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                MongoCollection<Document> companyCollection = MongoUtility.getCollection(client, "companies");
                MongoCollection<Document> serviceCollection = MongoUtility.getCollection(client, "services");
                MongoCollection<Document> alarmCollection = MongoUtility.getCollection(client, "alarms");
                UserBuilder userBuilder = new UserBuilder();
                AlarmBuilder alarmBuilder = new AlarmBuilder();
                ServiceBuilder serviceBuilder = new ServiceBuilder();
                CompanyBuilder companyBuilder = new CompanyBuilder();

                int totalUsers;
                if ((totalUsers = random.nextInt(6)) > 0) {
                    List<User> users = MongoUtility.iterableToList(userBuilder.getUser().findMany(userCollection, session));

                    for (int i = 0; i < totalUsers; i++) {
                        User userToAdd = users.get(random.nextInt(users.size()));
                        if (userIds.contains(userToAdd.getId())) continue;
                        if (i == 0) acknowledgedBy = userToAdd.getId();
                        userIds.add(userToAdd.getId());
                        if (random.nextInt(2) == 0) callIds.add(userToAdd.getId());
                    }
                }

                Alarm alarm;
                {
                    List<Alarm> alarms = MongoUtility
                            .iterableToList(alarmBuilder.getAlarm().findMany(alarmCollection, session));
                    alarm = alarms.get(random.nextInt(alarms.size()));
                }
                alarmIds.add(alarm.getId());
                companyId = companyBuilder
                        .setId(serviceBuilder.setId(alarm.getServiceId()).getService()
                                .findOne(serviceCollection, session).getCompanyId())
                        .getCompany().findOne(companyCollection, session).getId();
                List<Alarm> alarmsWithSameService = MongoUtility.iterableToList(alarmBuilder.setServiceId(alarm.getServiceId()).getAlarm().findMany(alarmCollection, session));
                for (Alarm additionalAlarm : alarmsWithSameService) {
                    if (random.nextInt(2) == 0) alarmIds.add(additionalAlarm.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return incidents;
        }

        for (int i = 0; i < size; i++) {
            Incident incident = new Incident();

            incident.setAlarmIds(alarmIds);
            incident.setCallIds(callIds);
            incident.setUserIds(userIds);
            
            incident.setAcknowledgedBy(acknowledgedBy);
            incident.setCaseNumber(Misc.getCaseNumberAndIncrement());
            incident.setCompanyId(companyId);
            incident.setHeader("Header");
            incident.setIncidentNote("");
            incident.setPriority(random.nextInt(5));
            incident.setResolved(random.nextInt(3) == 0);

            incidents.add(incident);
        }

        return incidents;
    }

    public static List<User> generateUsers() {
        List<User> users = new ArrayList<>();
        
        for (int i = 0; i < personalizedUsers.length; i++) {
            UserInfo info = personalizedUsers[i];
            User user = new User();
            user.setEmail(info.getEmail());
            user.setName(info.getName());
            user.setOnCall(random.nextInt(2) == 0);
            user.setOnDuty(random.nextInt(2) == 0);
            user.setPassword(info.getPassword());
            user.setPhoneNumber(info.getPhoneNumber());
            user.setTeam(teams[random.nextInt(teams.length)]);
            users.add(user);
        }
        
        return users;
    }

    public static void SetupDatabase() throws Exception {
        try (MongoClient client = MongoUtility.getClient()) {
            try (ClientSession session = client.startSession()) {
                MongoUtility.getDatabase(client).drop();
                MongoCollection<Document> userCollection = MongoUtility.getCollection(client, "users");
                MongoCollection<Document> companyCollection = MongoUtility.getCollection(client, "companies");
                MongoCollection<Document> incidentCollection = MongoUtility.getCollection(client, "incidents");
                MongoCollection<Document> serviceCollection = MongoUtility.getCollection(client, "services");
                MongoCollection<Document> alarmCollection = MongoUtility.getCollection(client, "alarms");
                CompanyBuilder companyBuilder = new CompanyBuilder();
                ServiceBuilder serviceBuilder = new ServiceBuilder();

                for (Company company : generateCompanies(10)) {
                    company.insertOne(companyCollection, session);
                }
                for (Company companyWithId : companyBuilder.getCompany().findMany(companyCollection, session)) {
                    for (Service service : generateServices(2, companyWithId.getId()) ) {
                        service.insertOne(serviceCollection, session);
                    }
                    for (Service serviceWithId : serviceBuilder.setCompanyId(companyWithId.getId()).getService().findMany(serviceCollection, session)) {
                        for (Alarm alarm : generateAlarms(3, serviceWithId.getId())) {
                            alarm.insertOne(alarmCollection, session);
                        }
                    }
                }
                for (User user : generateUsers()) {
                    user.insertOne(userCollection, session);
                }
                for (Incident incident : generateIncidents(10)) {
                    incident.insertOne(incidentCollection, session);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class UserInfo {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    
    public UserInfo(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

class CompanyInfo {
    private String name;
    private String[] serviceNames;

    public CompanyInfo(String name, String ... serviceNames) {
        this.name = name;
        this.serviceNames = serviceNames;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String[] getServiceNames() {
        return serviceNames;
    }
    
    public void setServiceNames(String[] serviceNames) {
        this.serviceNames = serviceNames;
    }
}