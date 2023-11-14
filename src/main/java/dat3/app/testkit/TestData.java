package dat3.app.testkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dat3.app.models.User;
import dat3.app.models.User.UserBuilder;

public class TestData {
    private static Random random = new Random(System.currentTimeMillis());

    private static String[] firstPartEmail = new String[] {
        "john.doe",
        "emma.smith",
        "alex.jones",
        "lisa.miller",
        "mark.wilson",
        "sarah.jones",
        "mike.smith",
        "emily.wilson",
        "chris.martin",
        "laura.brown",
        "daniel.carter",
        "olivia.jenkins",
        "brian.roberts",
        "mia.anderson",
        "jacob.evans",
        "sophia.perez",
        "liam.thompson",
        "abigail.morris",
        "ethan.russell",
        "ava.hall",
    };

    private static String[] secondPartEmail = new String[] {
        "acme_corp",
        "tech_solutions",
        "global_enterprises",
        "stellar_innovations",
        "alpha_industries",
        "digital_solutions",
        "quantum_technologies",
        "united_services",
        "synergy_systems",
        "global_innovators",
        "dynamic_enterprises",
        "fusion_solutions",
        "stellar_consulting",
        "prime_group",
        "agile_innovations",
        "apex_corp",
        "matrix_technologies",
        "nexus_solutions",
        "pioneer_systems",
        "horizon_innovations",
    };

    private static String[] thirdPartEmail = new String[] {
        "com",
        "net",
        "org",
        "co",
        "io",
        "biz",
        "info",
        "us",
        "co.uk",
        "tech",
        "edu",
        "gov",
        "mobi",
        "pro",
        "name",
        "tv",
        "travel",
        "blog",
        "app",
        "shop",
    };

    private static String[] firstPartName = new String[] {
        "Amelia",
        "Ethan",
        "Olivia",
        "Mason",
        "Ava",
        "Logan",
        "Sophia",
        "Jackson",
        "Isabella",
        "Liam",
    };

    private static String[] secondPartName = new String[] {
        "James",
        "Grace",
        "Alexander",
        "Rose",
        "Benjamin",
        "Emma",
        "Carter",
        "Ava",
        "Henry",
        "Lily",
    };

    private static String[] thirdPartName = new String[] {
        "Smith",
        "Johnson",
        "Williams",
        "Jones",
        "Brown",
        "Davis",
        "Miller",
        "Wilson",
        "Moore",
        "Taylor",
    };
    
    public static List<User> randomValidUsers() {
        return shuffleList(unshuffledValidUsers(), 10);
    }

    public static List<String> randomValidEmails() {
        return shuffleList(unshuffledValidEmails(), 10);
    }

    public static List<String> randomValidNames() {
        return shuffleList(unshuffledValidNames(), 10);
    }

    public static boolean randomBoolean() {
        return random.nextInt(4) == 0;
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

        Iterator<String> namesIterator = randomValidNames().iterator();

        while (namesIterator.hasNext()) {
            String name = namesIterator.next();
            String email = createEmailFromName(name);
            users.add(builder
                .setEmail(email)
                .setName(name)
                .setOnCall(randomBoolean())
                .setOnDuty(randomBoolean())
                .getUser());
        }

        return users;
    }

    private static String createEmailFromName(String name) {
        String[] splitName = name.toLowerCase().split(" ");
        
        String finalFirstPart = "";
        for (int i = 0; i < splitName.length; i++) {
            if (i == 0) finalFirstPart += splitName[i];
            else finalFirstPart += "." + splitName[i];
        }
        finalFirstPart += "@";

        return finalFirstPart + randomSecondPartEmail() + randomThirdPartEmail();
    }

    private static String randomSecondPartEmail() {
        return secondPartEmail[random.nextInt(secondPartEmail.length)];
    }

    private static String randomThirdPartEmail() {
        return thirdPartEmail[random.nextInt(thirdPartEmail.length)];
    }
}
