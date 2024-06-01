package cloud.anypoint.redis.api.stream;

import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

public class StreamWatermark {
    @Placement(order = 1)
    @Parameter
    private String key;
    @Placement(order = 2)
    @Parameter
    private String id;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
