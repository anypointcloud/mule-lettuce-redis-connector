package cloud.anypoint.redis.internal.exception;

import cloud.anypoint.redis.api.error.ConnectorError;
import org.mule.runtime.extension.api.exception.ModuleException;

public class WrongTypeException extends ModuleException {
    public WrongTypeException(String command, Throwable cause) {
        super("Wrong type for command " + command, ConnectorError.WRONG_TYPE, cause);
    }
    public WrongTypeException(String command, String key) {
        super("Key " + key + " contains wrong type for command " + command, ConnectorError.WRONG_TYPE);
    }

    public WrongTypeException(String command, String key, Throwable cause) {
        super("Key " + key + " contains wrong type for command " + command, ConnectorError.WRONG_TYPE, cause);
    }
}
