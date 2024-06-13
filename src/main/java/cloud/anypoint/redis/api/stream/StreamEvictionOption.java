package cloud.anypoint.redis.api.stream;

import io.lettuce.core.XAddArgs;
import io.lettuce.core.XTrimArgs;

public interface StreamEvictionOption {
    XAddArgs decorate(XAddArgs args);
    XTrimArgs decorate(XTrimArgs args);
}
