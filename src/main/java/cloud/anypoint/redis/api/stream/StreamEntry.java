package cloud.anypoint.redis.api.stream;

import java.util.Map;

public class StreamEntry {
    private String id;
    private Map<String, String> entry;

    public StreamEntry() {}
    public StreamEntry(String id, Map<String, String> entry) {
        this.id = id;
        this.entry = entry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getEntry() {
        return entry;
    }

    public void setEntry(Map<String, String> entry) {
        this.entry = entry;
    }
}
