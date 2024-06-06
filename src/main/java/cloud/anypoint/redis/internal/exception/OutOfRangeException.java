package cloud.anypoint.redis.internal.exception;

import cloud.anypoint.redis.api.error.ConnectorError;
import org.mule.runtime.extension.api.exception.ModuleException;

public class OutOfRangeException extends ModuleException {
    public OutOfRangeException(Throwable cause) {
        super(ConnectorError.OUT_OF_RANGE, cause);
    }
}
