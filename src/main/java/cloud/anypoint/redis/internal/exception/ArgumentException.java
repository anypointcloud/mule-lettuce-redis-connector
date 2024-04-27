package cloud.anypoint.redis.internal.exception;

import cloud.anypoint.redis.api.ConnectorError;
import org.mule.runtime.extension.api.exception.ModuleException;

public class ArgumentException extends ModuleException {
    public ArgumentException(String command, IllegalArgumentException cause) {
        super("(" + command + ") " + cause.getMessage(), ConnectorError.ARGUMENT, cause);
    }
}
