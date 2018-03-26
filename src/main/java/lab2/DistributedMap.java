package lab2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedMap implements SimpleStringMap {
    private final Map<String, String> map;

    DistributedMap() {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public String put(String key, String value) {
        return map.put(key, value);
    }

    @Override
    public String remove(String key) {
        return map.remove(key);
    }
}
