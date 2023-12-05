package dat3.app.testkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dat3.app.models.Alarm;
import dat3.app.models.Company;
import dat3.app.models.Incident;
import dat3.app.models.Service;
import dat3.app.models.User;
import dat3.app.models.Alarm.AlarmBuilder;
import dat3.app.models.Company.CompanyBuilder;
import dat3.app.models.Service.ServiceBuilder;
import dat3.app.models.User.UserBuilder;

public class TestData {
    private static Random random = new Random(System.currentTimeMillis());

    private static String[] companyNames = new String[] {
        "Quantum Innovations Co.",
        "Nebula Dynamics Solutions",
        "SynthoCraft Technologies",
        "ElectroPulse Innovations",
        "Zenith Nexus Enterprises",
        "Virtuoso Visionaries Ltd.",
        "LuminaSphere Innovations",
        "TechVortex Innovations",
        "NovaFusion Systems",
        "EchoSynergy Solutions",
        "Aetheric Nexus Technologies",
        "Pinnacle Pulse Innovations",
        "QuasarQuest Dynamics",
        "Ethereal Edge Solutions",
        "CipherSprint Innovations",
        "QuantumQuotient Ventures",
        "Solaris Spark Industries",
        "NebulaNova Innovations",
        "Vertex Visionary Systems",
        "Astral Axis Technologies",
    };

    private static String[] serviceNames = new String[] {
        "Uber",
        "Lyft",
        "Didi Chuxing",
        "DoorDash",
        "Grubhub",
        "Uber Eats",
        "Netflix",
        "Hulu",
        "Amazon Prime Video",
        "Peloton",
        "MyFitnessPal",
        "Headspace",
        "Upwork",
        "Fiverr",
        "Freelancer",
        "Coursera",
        "Udemy",
        "Khan Academy",
        "Facebook",
        "Instagram",
        "Twitter",
        "PayPal",
        "Venmo",
        "Cash App",
        "TaskRabbit",
        "Handy",
        "Thumbtack",
        "Airbnb",
        "Booking.com",
        "Expedia",
        "Birchbox",
        "Blue Apron",
        "Stitch Fix",
        "Google Translate",
        "Microsoft Translator",
        "Babbel",
        "Zoom",
        "Microsoft Teams",
        "Google Meet",
        "The New York Times",
        "The Washington Post",
        "Spotify",
        "Postmates",
        "Instacart",
        "Shipt",
    };

    private static String[] firstPartEmail = new String[] {
        "john.doe",
        "emma.smith",
        "alex.jones",
        "lisa.miller",
        "mark.wilson",
    };

    private static String[] secondPartEmail = new String[] {
        "acme_corp",
        "tech_solutions",
        "global_enterprises",
        "stellar_innovations",
        "alpha_industries"
    };

    private static String[] thirdPartEmail = new String[] {
        "com",
        "net",
        "org",
        "co",
        "io"
    };

    private static String[] firstPartName = new String[] {
        "Amelia",
        "Ethan",
        "Olivia",
        "Mason",
        "Ava"
    };

    private static String[] secondPartName = new String[] {
        "James",
        "Grace",
        "Alexander",
        "Rose",
        "Benjamin"
    };

    private static String[] thirdPartName = new String[] {
        "Smith",
        "Johnson",
        "Williams",
        "Jones",
        "Brown"
    };

    private static String[] firstPartNumber = new String[] {
        "12 3",
        "45 6",
        "78 9",
        "23 4",
        "56 7"
    };

    private static String[] secondPartNumber = new String[] {
        "1 23 ",
        "2 34 ",
        "3 45 ",
        "4 56 ",
        "5 67 "
    };

    private static String[] thirdPartNumber = new String[] {
        "34",
        "45",
        "56",
        "67",
        "78"
    };

    private static String[] firstPartIncident = new String[] {
        "Guldbæk",
        "Byriel",
        "Sandra",
        "Mikkel",
        "Oliver",
        "Rasmus",
        "Eva",
        "A computer",
    };

    private static String[] secondPartIncident = new String[] {
        "gracefully",
        "ominously",
        "elegantly",
        "swiftly",
        "vividly",
        "respectfully",
        "relentlessly",
        "violently",
        "mysteriously",
        "horrendously",
    };

    private static String[] thirdPartIncident = new String[] {
        "sharted",
        "fell off a cliff",
        "spun",
        "just died",
        "kissed a tree at 90mph",
        "chased",
        "crashed",
        "rescued",
        "twinkled",
        "settled",
    };

    private static String[] alarmHeaders = new String[] {
        "OOM-killed 3 times",
        "Lost 345 connections in 1 minute",
        "Core temp. above 90 degree rule",
        "Container restarted 6 times",
    };

    private static String[] teams = new String[] {
        "Service Desk",
        "Cloud Native Development", 
        "Cloud Native Maintenance",
        "Database",
        "Drift",
    };

    public static List<User> personalizedUsers() {
        String[] names = new String[] {
            "mads.byriel",
            "mads.guldbæk",
            "rasmus.pedersen",
            "sandra.rosenbeck",
            "mikkel.helsing",
            "oliver.nielsen",
        };
        List<User> users = new ArrayList<>(6);
        UserBuilder builder = new UserBuilder();
        for (String name : names) {
            users.add(builder
                .setName(name)
                .setEmail(name + "@gmail.com")
                .setPassword(name + "123")
                .setPhoneNumber("505-842-5662")
                .setOnCall(randomBoolean())
                .setOnDuty(randomBoolean())
                .getUser());
        }
        return users;
    }

    public static int randomIntExcl(int bound) {
        return random.nextInt(bound);
    }
    
    public static List<User> randomValidUsers() {
        return shuffleList(unshuffledValidUsers(), 10);
    }

    public static List<String> randomIncidentnames() {
        return shuffleList(unshuffledIncidentNames(), 10);
    }

    public static List<String> randomValidEmails() {
        return shuffleList(unshuffledValidEmails(), 10);
    }

    public static List<Alarm> randomAlarms() {
        return shuffleList(unshuffledAlarms(), 10);
    }
    
    public static List<String> randomValidNames() {
        return shuffleList(unshuffledValidNames(), 10);
    }

    public static boolean randomBoolean() {
        return random.nextInt(2) == 0;
    }

    public static List<Company> randomCompanies() {
        return shuffleList(unshuffledCompanies(), 10);
    }

    public static List<String> randomValidPhoneNumbers() {
        return shuffleList(unshuffledValidPhoneNumbers(), 10);
    }

    public static List<Service> randomServices() {
        return shuffleList(unshuffledServices(), 10);
    }
    
    public static Alarm getRandomAlarm() {
        AlarmBuilder builder = new AlarmBuilder();
        String alarmName = alarmHeaders[randomIntExcl(alarmHeaders.length)];
        return builder.setName(alarmName).getAlarm();
    }

    public static <T> List<T> shuffleList(List<T> list, int randomness) {
        for (int i = 0; i < randomness * list.size(); i++) {
            int randInt1 = random.nextInt(list.size());
            int randInt2 = random.nextInt(list.size());

            T temp = list.get(randInt1);
            list.set(randInt1, list.get(randInt2));
            list.set(randInt2, temp);
        }

        return list;
    }

    public static List<String> shuffledTeams() {
        return shuffleList(Arrays.asList(teams), 10);
    }
    
    public static List<Company> unshuffledCompanies() {
        List<Company> companies = new ArrayList<>();
        CompanyBuilder builder = new CompanyBuilder();
        for (String string : companyNames) {
            builder.setName(string);
            companies.add(builder.getCompany());
        }
        return companies;
    }

    public static List<String> unshuffledValidEmails() {
        List<String> validEmails = new ArrayList<>(firstPartEmail.length * secondPartEmail.length * thirdPartEmail.length);
        for (int i = 0; i < firstPartEmail.length; i++) {
            for (int j = 0; j < secondPartEmail.length; j++) {
                for (int k = 0; k < thirdPartEmail.length; k++) {
                    validEmails.add(firstPartEmail[i] + "@" + secondPartEmail[j] + "." + thirdPartEmail[k]);
                }
            }
        }

        return validEmails;
    }

    public static List<Alarm> unshuffledAlarms() {
        AlarmBuilder builder = new AlarmBuilder();
        List<Alarm> alarms = new ArrayList<>();
        for (String alarmName : alarmHeaders) {
            alarms.add(builder.setName(alarmName).getAlarm());
        }
        return alarms;
    }
    
    public static List<Service> unshuffledServices() {
        ServiceBuilder builder = new ServiceBuilder();
        List<Service> services = new ArrayList<>();
        for (String serviceName : serviceNames) {
            services.add(builder.setName(serviceName).getService());
        }
        return services;
    }

    public static List<String> unshuffledValidNames() {
        List<String> validNames = new ArrayList<>(firstPartName.length * secondPartName.length * thirdPartName.length);
        for (int i = 0; i < firstPartName.length; i++) {
            for (int j = 0; j < secondPartName.length; j++) {
                for (int k = 0; k < thirdPartName.length; k++) {
                    validNames.add(firstPartName[i] + " " + secondPartName[j] + " " + thirdPartName[k]);
                }
            }
        }

        return validNames;
    }

    public static List<User> unshuffledValidUsers() {
        UserBuilder builder = new UserBuilder();
        List<User> users = new ArrayList<>();
        Iterator<String> namesIterator = unshuffledValidNames().iterator();
        Iterator<String> phoneNumbers = unshuffledValidPhoneNumbers().iterator();

        while (namesIterator.hasNext() && phoneNumbers.hasNext()) {
            String name = namesIterator.next();
            String number = phoneNumbers.next();
            String email = createEmailFromName(name);
            users.add(builder
                .setEmail(email)
                .setName(name)
                .setPassword(email.split("@")[0] + "123")
                .setPhoneNumber(number)
                .setOnCall(randomBoolean())
                .setOnDuty(randomBoolean())
                .setTeam(teams[randomIntExcl(teams.length)])
                .getUser());
        }

        return users;
    }

    public static List<String> unshuffledValidPhoneNumbers() {
        List<String> validPhoneNumbers = new ArrayList<>(firstPartNumber.length * secondPartNumber.length * thirdPartNumber.length);
        for (int i = 0; i < firstPartNumber.length; i++) {
            for (int j = 0; j < secondPartNumber.length; j++) {
                for (int k = 0; k < thirdPartNumber.length; k++) {
                    validPhoneNumbers.add(firstPartNumber[i] + secondPartNumber[j] + thirdPartNumber[k]);
                }
            }
        }

        return validPhoneNumbers;
    }

    public static List<String> unshuffledIncidentNames() {
        List<String> incidentNames = new ArrayList<>(firstPartIncident.length * secondPartIncident.length * thirdPartIncident.length);
        for (int i = 0; i < firstPartIncident.length; i++) {
            for (int j = 0; j < secondPartIncident.length; j++) {
                for (int k = 0; k < thirdPartIncident.length; k++) {
                    incidentNames.add(firstPartIncident[i] + " " + secondPartIncident[j] + " " + thirdPartIncident[k]);
                }
            }
        }

        return incidentNames;
    }

    private static String createEmailFromName(String name) {
        String[] splitName = name.toLowerCase().split(" ");
        
        String finalFirstPart = "";
        for (int i = 0; i < splitName.length; i++) {
            if (i == 0) finalFirstPart += splitName[i];
            else finalFirstPart += "." + splitName[i];
        }
        finalFirstPart += "@";

        return finalFirstPart + randomSecondPartEmail() + "." + randomThirdPartEmail();
    }

    private static String randomSecondPartEmail() {
        return secondPartEmail[random.nextInt(secondPartEmail.length)];
    }

    private static String randomThirdPartEmail() {
        return thirdPartEmail[random.nextInt(thirdPartEmail.length)];
    }
}
