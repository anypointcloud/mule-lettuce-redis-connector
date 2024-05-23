package cloud.anypoint.redis.internal.util;

import cloud.anypoint.redis.internal.exception.SyntaxErrorException;
import cloud.anypoint.redis.internal.exception.TimeoutException;
import cloud.anypoint.redis.internal.exception.WrongTypeException;
import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.RedisCommandTimeoutException;
import reactor.core.publisher.Mono;

public class ErrorDecorator {

    public static <T> Mono<T> mapErrors(Mono<T> cmd, String commandText) {
        return cmd
                .onErrorMap(RedisCommandTimeoutException.class, TimeoutException::new)
                .onErrorMap(RedisCommandExecutionException.class, t -> {
                    if (t.getMessage().startsWith("WRONGTYPE")) {
                        return new WrongTypeException(commandText, t);
                    }
                    if (t.getMessage().equals("ERR syntax error")) {
                        return new SyntaxErrorException(commandText, t);
                    }
                    return t;
                });
    }
    public static <T> Mono<T> mapErrors(Mono<T> cmd, String commandText, String key) {
        return cmd
            .onErrorMap(RedisCommandTimeoutException.class, TimeoutException::new)
            .onErrorMap(RedisCommandExecutionException.class, t -> {
                if (t.getMessage().startsWith("WRONGTYPE")) {
                    return new WrongTypeException(commandText, key, t);
                }
                if (t.getMessage().equals("ERR syntax error")) {
                    return new SyntaxErrorException(commandText, t);
                }
                return t;
            });
    }
}
