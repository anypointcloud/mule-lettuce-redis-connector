package cloud.anypoint.redis.internal.exception;

import cloud.anypoint.redis.api.error.ConnectorError;
import io.lettuce.core.RedisCommandExecutionException;
import org.mule.runtime.extension.api.exception.ModuleException;

public class CommandException extends ModuleException {

    public CommandException(RedisCommandExecutionException cause) {
        super(cause.getMessage(), ConnectorError.COMMAND, cause);
    }
}
