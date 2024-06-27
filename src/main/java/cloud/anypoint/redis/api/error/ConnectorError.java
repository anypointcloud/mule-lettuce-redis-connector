package cloud.anypoint.redis.api.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.Optional;

public enum ConnectorError implements ErrorTypeDefinition<ConnectorError> {
    SYNTAX,
    COMMAND,
    ARGUMENT(SYNTAX),
    NIL,
    WRONG_TYPE(COMMAND),
    OUT_OF_RANGE(COMMAND),
    TIMEOUT;

    private ErrorTypeDefinition<? extends Enum<?>> parent;

    ConnectorError() {}
    ConnectorError(ErrorTypeDefinition<? extends Enum<?>> parent) {
        this.parent = parent;
    }

    @Override
    public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
        return Optional.ofNullable(parent);
    }
}
