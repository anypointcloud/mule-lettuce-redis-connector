package cloud.anypoint.redis.api.stream;

import io.lettuce.core.XAddArgs;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

public class MinIdEvictionOption implements StreamEvictionOption {
    @DisplayName("MINID")
    @Parameter
    private String id;

    @DisplayName("Exact Trimming < = | ~ >")
    @Optional(defaultValue = "true")
    @Parameter
    private boolean exactTrimming;

    @DisplayName("LIMIT")
    @Optional
    @Parameter
    private Integer limit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isExactTrimming() {
        return exactTrimming;
    }

    public void setExactTrimming(boolean exactTrimming) {
        this.exactTrimming = exactTrimming;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public XAddArgs decorate(XAddArgs args) {
        XAddArgs modified = args.minId(id).exactTrimming(exactTrimming);
        if (null != limit) {
            modified = modified.limit(limit);
        }
        return modified;
    }
}
