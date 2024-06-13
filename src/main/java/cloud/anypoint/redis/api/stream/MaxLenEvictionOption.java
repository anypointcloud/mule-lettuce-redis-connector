package cloud.anypoint.redis.api.stream;

import io.lettuce.core.XAddArgs;
import io.lettuce.core.XTrimArgs;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

public class MaxLenEvictionOption implements StreamEvictionOption {

    @DisplayName("MAXLEN")
    @Parameter
    private long maxLength;

    @DisplayName("Exact Trimming < = | ~ >")
    @Optional(defaultValue = "true")
    @Parameter
    private boolean exactTrimming;

    @DisplayName("LIMIT")
    @Optional
    @Parameter
    private Integer limit;

    public boolean isExactTrimming() {
        return exactTrimming;
    }

    public void setExactTrimming(boolean exactTrimming) {
        this.exactTrimming = exactTrimming;
    }

    public long getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(long maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public XAddArgs decorate(XAddArgs args) {
        XAddArgs modified = args.maxlen(maxLength).exactTrimming(exactTrimming);
        if (null != limit) {
            modified = modified.limit(limit);
        }
        return modified;
    }

    @Override
    public XTrimArgs decorate(XTrimArgs args) {
        XTrimArgs modified = args.maxlen(maxLength).exactTrimming(exactTrimming);
        if (null != limit) {
            modified = modified.limit(limit);
        }
        return modified;
    }
}
