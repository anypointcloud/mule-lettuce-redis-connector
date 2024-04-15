package cloud.anypoint.redis.internal;

import cloud.anypoint.redis.api.ConnectorError;
import org.mule.runtime.extension.api.exception.ModuleException;

public class NilValueException extends ModuleException {
    public NilValueException(String command) {
        super(command + " reply is nil", ConnectorError.NIL);
    }

    public NilValueException(String command, String key) {
        super(command + " reply is nil for key " + key, ConnectorError.NIL);
    }
}
