package cloud.anypoint.redis.api.stream;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

public class PendingDetailsArgs {
    @Parameter
    private String start;
    @Parameter
    private String end;
    @Parameter
    private Long count;

    @DisplayName("IDLE")
    @Optional
    @Parameter
    private Long minIdleTime;

    @Optional
    @Parameter
    private String consumer;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getMinIdleTime() {
        return minIdleTime;
    }

    public void setMinIdleTime(Long minIdleTime) {
        this.minIdleTime = minIdleTime;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }
}
