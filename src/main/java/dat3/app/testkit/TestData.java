package dat3.app.testkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestData {
    public static Iterable<String> randomValidEmails() {
        String[] firstPart = new String[] {
            "john.doe@",
            "emma.smith@",
            "alex.jones@",
            "lisa.miller@",
            "mark.wilson@",
            "sarah.jones@",
            "mike.smith@",
            "emily.wilson@",
            "chris.martin@",
            "laura.brown@",
            "daniel.carter@",
            "olivia.jenkins@",
            "brian.roberts@",
            "mia.anderson@",
            "jacob.evans@",
            "sophia.perez@",
            "liam.thompson@",
            "abigail.morris@",
            "ethan.russell@",
            "ava.hall@",
        };

        String[] secondPart = new String[] {
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

        String[] thirdPart = new String[] {
            ".com",
            ".net",
            ".org",
            ".co",
            ".io",
            ".biz",
            ".info",
            ".us",
            ".co.uk",
            ".tech",
            ".edu",
            ".gov",
            ".mobi",
            ".pro",
            ".name",
            ".tv",
            ".travel",
            ".blog",
            ".app",
            ".shop",
        };

        List<String> validEmails = new ArrayList<>(firstPart.length * secondPart.length * thirdPart.length);

        for (int i = 0; i < firstPart.length; i++) {
            for (int j = 0; j < secondPart.length; j++) {
                for (int k = 0; k < thirdPart.length; k++) {
                    validEmails.add(firstPart[i] + secondPart[j] + thirdPart[k]);
                }
            }
        }

        return shuffleList(validEmails, 10);
    }

    public static Iterable<String> randomNames() {
        String[] firstPart = new String[] {
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

        String[] secondPart = new String[] {
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

        String[] thirdPart = new String[] {
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

        List<String> validNames = new ArrayList<>(firstPart.length * secondPart.length * thirdPart.length);
        for (int i = 0; i < firstPart.length; i++) {
            for (int j = 0; j < secondPart.length; j++) {
                for (int k = 0; k < thirdPart.length; k++) {
                    validNames.add(firstPart[i] + " " + secondPart[j] + " " + thirdPart[k]);
                }
            }
        }

        return shuffleList(validNames, 10);
    }

    public static <T> List<T> shuffleList(List<T> list, int randomness) {
        // SHUFFLES IN-PLACE.
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        for (int i = 0; i < randomness * list.size(); i++) {
            int randInt1 = random.nextInt(list.size());
            int randInt2 = random.nextInt(list.size());

            T temp = list.get(randInt1);
            list.set(randInt1, list.get(randInt2));
            list.set(randInt2, temp);
        }

        return list;
    }
}
