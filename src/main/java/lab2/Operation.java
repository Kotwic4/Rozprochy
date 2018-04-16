package lab2;

import java.io.Serializable;

public class Operation implements Serializable {

    static final long serialVersionUID = 42L;
    private Type type;
    private String key;
    private String value;

    public Operation(Type type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public enum Type {
        PUT, DELETE
    }
}
