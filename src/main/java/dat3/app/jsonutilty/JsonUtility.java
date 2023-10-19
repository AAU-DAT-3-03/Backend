package dat3.app.jsonutilty;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonUtility {
    private Gson gson;

    public JsonUtility() {
        this.gson = new Gson();
    }

    public <T> T readJsonString(String jsonString, Class<? extends T> clazz) {
        try {
            return gson.fromJson(jsonString, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void Example1() {
        JsonUtility jsonUtility = new JsonUtility();
        String jsonString = "{\"name\": \"mads hvid byriel\", \"age\": 22}";
        TestPerson tp = jsonUtility.readJsonString(jsonString, TestPerson.class);
        System.out.println(tp);
    }

    public static void Example2() {
        JsonUtility jsonUtility = new JsonUtility();

        // Write class with reference to itself into JSON format:
        TestStructure ts = new TestStructure("mads");
        TestStructure ts2 = new TestStructure("byriel", ts);
        String jsonText = jsonUtility.gson.toJson(ts2);

        // Read from a JSON format to a class:
        TestStructure readStructure = jsonUtility.readJsonString(jsonText, TestStructure.class); 
        // readStructure is not null.
    }
}

class TestPerson {
    private String name = "null";
    private int age = 0;

    @Override
    public String toString() {
        String s = "{";
        s += "\"name\":\"" + name + "\"";
        s += ",\"age\":" + Integer.toString(age) + "";
        s += "}";
        return s;
    }
}

class TestStructure {
    private TestStructure reference = null;
    private String name;

    public TestStructure() {}

    public TestStructure(String name) {
        this();
        this.name = name;
    }

    public TestStructure(String name, TestStructure reference) {
        this(name);
        this.reference = reference;
    }

    @Override
    public String toString() {
        String s = "{\"name\":";
        if (name == null) {
            s += "null";
        } else {
            s += "\"" + name + "\"";
        }

        s += ",\"reference\":";
        if (reference == null) {
            s += "null";
        } else {
            s += reference.toString();
        }
        s += "}";
        return s;
    }
}
