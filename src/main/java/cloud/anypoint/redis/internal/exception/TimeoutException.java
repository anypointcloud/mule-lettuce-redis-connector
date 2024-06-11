package cloud.anypoint.redis.internal.exception;

import io.lettuce.core.RedisCommandTimeoutException;
import org.mule.runtime.extension.api.exception.ModuleException;

import static cloud.anypoint.redis.api.error.ConnectorError.TIMEOUT;

public class TimeoutException extends ModuleException {
    public TimeoutException(String command, double timeout) {
        super(command + " exceeded timeout " + timeout, TIMEOUT);
    }
    public TimeoutException(RedisCommandTimeoutException e) {
        super(TIMEOUT, e);
    }
}
