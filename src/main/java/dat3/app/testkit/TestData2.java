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
            new UserInfo("Allan Højgaard Jensen", "ahj@netic.dk", "123", "30203376"),
            new UserInfo("Claus", "claus@netic.dk", "123", "54875621"),
            new UserInfo("Orla Pedersen", "orlapedersen@cs.aau.dk", "orlapedersen123", "90351250"),
            new UserInfo("Lars Larsen", "larslarsen@gmail.com", "larslarsen123", "69306820"),
            new UserInfo("Andreas Hummelmose", "andreashummelmose@gmail.com", "andreashummelmose123", "96849736"),
            new UserInfo("Christian Bonde", "christianbonde@gmail.com", "christianbonde123", "58739689"),
            new UserInfo("Alexander Redder", "alexanderredder@gmail.com", "alexanderredder123", "85746397"),
            new UserInfo("Josva Kleist", "kleist@cs.aau.dk", "josvakleist123", "86744289"),
            new UserInfo("Nicklas Andersen", "nia@netcompany.com", "nicklasandersen123", "58526798"),
            new UserInfo("Anton Evgrafov", "anev@math.aau.dk", "antonevgrafov123", "76739279"),
            new UserInfo("Giovanni Bacci", "giovbacci@cs.aau.dk", "giovannibacci123", "74986493"),
            new UserInfo("Peter Gjøl Jensen", "pgj@cs.aau.dk", "petergjøljensen", "87598294"),
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
            "Flytning af infrastruktur",
            "E-handel hosting",
            "Drift og vedligeholdelse",
            "Website hosting",
            "DB",
            "WMS",
            "POS",
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

    public static List<Company> generateCompanies() {
        List<Company> companies = new ArrayList<>();

        for (int i = 0; i < companyNames.length; i++) {
            Company company = new Company();
            company.setName(companyNames[i]);
            companies.add(company);
        }

        return companies;
    }

    public static List<Service> generateServices(int size, String companyId) {
        List<Service> services = new ArrayList<>();

        for (int i = 0; i < size; i++) {
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

        List<User> users;
        List<Alarm> alarms;
        List<Company> companies;
        List<Service> services;
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

                users = MongoUtility.iterableToList(userBuilder.getUser().findMany(userCollection, session));
                if (users == null)
                    users = new ArrayList<>();

                alarms = MongoUtility.iterableToList(alarmBuilder.getAlarm().findMany(alarmCollection, session));
                if (alarms == null)
                    alarms = new ArrayList<>();

                services = MongoUtility
                        .iterableToList(serviceBuilder.getService().findMany(serviceCollection, session));
                if (services == null)
                    services = new ArrayList<>();
                    
                companies = MongoUtility
                        .iterableToList(companyBuilder.getCompany().findMany(companyCollection, session));
                if (companies == null)
                    companies = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return incidents;
        }

        for (int i = 0; i < size; i++) {
            List<String> alarmIds = new ArrayList<>();

            String companyId = companies.get(random.nextInt(companies.size())).getId();
            List<Alarm> alarmsFromCompany = filterAlarmsByCompany(companyId, services, alarms);
            for (Alarm alarm : alarmsFromCompany) {
                if (random.nextInt(3) == 0)
                    alarmIds.add(alarm.getId());
            }

            if (alarmIds.size() == 0)
                continue;

            List<String> userIds = new ArrayList<>();
            List<String> callIds = new ArrayList<>();

            String acknowledgedBy = null;
            for (int j = 0; j < random.nextInt(5); j++) {
                User userToAdd = users.get(random.nextInt(users.size()));
                if (userIds.contains(userToAdd.getId()))
                    continue;
                if (j == 0)
                    acknowledgedBy = userToAdd.getId();
                userIds.add(userToAdd.getId());
                if (random.nextInt(3) != 0)
                    callIds.add(userToAdd.getId());
            }

            Incident incident = new Incident();
            incident.setAlarmIds(alarmIds);
            incident.setCallIds(callIds);
            incident.setUserIds(userIds);

            incident.setAcknowledgedBy(acknowledgedBy);
            incident.setCaseNumber(Misc.getCaseNumberAndIncrement());
            incident.setCompanyId(companyId);
            incident.setCreationDate(System.currentTimeMillis() - random.nextInt(86400000 * 365));
            incident.setHeader("Header");
            incident.setIncidentNote("");
            incident.setPriority(random.nextInt(4) + 1);
            incident.setResolved(random.nextInt(3) == 0);

            incidents.add(incident);
        }

        return incidents;
    }

    private static List<Alarm> filterAlarmsByCompany(String companyId, List<Service> services, List<Alarm> alarms) {
        List<Alarm> filteredAlarms = new ArrayList<>();
        List<Service> filteredServices = new ArrayList<>();

        for (Service service : services) {
            if (service.getCompanyId().equals(companyId))
                filteredServices.add(service);
        }

        for (Service service : filteredServices) {
            for (Alarm alarm : alarms) {
                if (alarm.getServiceId().equals(service.getId()))
                    filteredAlarms.add(alarm);
            }
        }

        return filteredAlarms;
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

                for (Company company : generateCompanies()) {
                    company.insertOne(companyCollection, session);
                }
                for (Company companyWithId : companyBuilder.getCompany().findMany(companyCollection, session)) {
                    for (Service service : generateServices(2, companyWithId.getId())) {
                        service.insertOne(serviceCollection, session);
                    }
                    for (Service serviceWithId : serviceBuilder.setCompanyId(companyWithId.getId()).getService()
                            .findMany(serviceCollection, session)) {
                        for (Alarm alarm : generateAlarms(3, serviceWithId.getId())) {
                            alarm.insertOne(alarmCollection, session);
                        }
                    }
                }
                for (User user : generateUsers()) {
                    user.insertOne(userCollection, session);
                }
                for (Incident incident : generateIncidents(15)) {
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

    public CompanyInfo(String name, String... serviceNames) {
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