package cloud.anypoint.redis.internal;

import cloud.anypoint.redis.api.ConnectorError;
import org.mule.runtime.extension.api.exception.ModuleException;

public class WrongTypeException extends ModuleException {
    public WrongTypeException(String command, String key) {
        super("Key " + key + " contains wrong type for command " + command, ConnectorError.WRONGTYPE);
    }

    public WrongTypeException(String command, String key, Throwable cause) {
        super("Key " + key + " contains wrong type for command " + command, ConnectorError.WRONGTYPE, cause);
    }
}
