package cloud.anypoint.redis.api.stream;

import io.lettuce.core.XAddArgs;

public interface StreamEvictionOption {
    XAddArgs decorate(XAddArgs args);
}
