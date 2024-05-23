package cloud.anypoint.redis.internal.exception;

import io.lettuce.core.RedisCommandExecutionException;
import org.mule.runtime.extension.api.exception.ModuleException;

import static cloud.anypoint.redis.api.error.ConnectorError.SYNTAX;


public class SyntaxErrorException extends ModuleException {
    public SyntaxErrorException(String command, RedisCommandExecutionException cause) {
        super("Syntax error for " + command, SYNTAX, cause);
    }
}
