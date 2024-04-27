package cloud.anypoint.redis.internal.util;

import cloud.anypoint.redis.internal.WrongTypeException;
import io.lettuce.core.RedisCommandExecutionException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ErrorDecorator {
    public static <T> Mono<T> mapWrongTypeError(Mono<T> cmd, String commandText, String key) {
        return cmd.onErrorMap(RedisCommandExecutionException.class, t -> {
                if (t.getMessage().startsWith("WRONGTYPE")) {
                    return new WrongTypeException(commandText, key, t);
                }
                return t;
            });
    }
}
