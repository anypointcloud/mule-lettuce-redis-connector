package cloud.anypoint.redis.api;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public enum ConnectorError implements ErrorTypeDefinition<ConnectorError> {
    ARGUMENT,
    NIL,
    WRONGTYPE
}
