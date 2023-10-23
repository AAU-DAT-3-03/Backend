package dat3.app.mongo;

public class EqualityTuple {
    private String fieldName;
    private Object value;

    public EqualityTuple(String fieldName, Object value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getValue() {
        return value;
    }
} 