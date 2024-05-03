package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapWrongTypeError;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.exception.ArgumentException;
import cloud.anypoint.redis.internal.exception.NilValueException;
import cloud.anypoint.redis.internal.metadata.ArgumentErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.NilErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import io.lettuce.core.KeyValue;
import io.lettuce.core.Value;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Map;

public class HashCommandOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(HashCommandOperations.class);

    @DisplayName("HGETALL")
    @Throws(WrongTypeErrorTypeProvider.class)
    public void hgetall(@Connection LettuceRedisConnection connection,
                        String key,
                        CompletionCallback<Map<String, String>, Void> callback) {
        LOGGER.debug("HGETALL {}", key);
        Mono<Map<String, String>> cmd = connection.commands().hgetall(key)
                .collectMap(KeyValue::getKey, Value::getValue);
        mapWrongTypeError(cmd, "HGETALL", key)
            .subscribe(
                result -> callback.success(Result.<Map<String, String>, Void>builder()
                    .output(result)
                    .build()),
                callback::error);
    }

    @DisplayName("HGET")
    @MediaType(value = "text/plain", strict = false)
    @Throws({WrongTypeErrorTypeProvider.class, NilErrorTypeProvider.class})
    public void hget(@Connection LettuceRedisConnection connection,
                     String key,
                     String field,
                     CompletionCallback<String, Void> callback) {
        LOGGER.debug("HGET {}", key);
        mapWrongTypeError(connection.commands().hget(key, field), "HGET", key)
                // TODO: Add validator parameter to make this optional
                .switchIfEmpty(Mono.error(new NilValueException("HGET", key)))
                .subscribe(
                    result ->
                        callback.success(Result.<String, Void>builder()
                            .output(result)
                            .build()),
                    callback::error);
    }

    @DisplayName("HLEN")
    @Throws(WrongTypeErrorTypeProvider.class)
    public void hlen(@Connection LettuceRedisConnection connection,
                     String key,
                     CompletionCallback<Long, Void> callback) {
        LOGGER.debug("HLEN {}", key);
        mapWrongTypeError(connection.commands().hlen(key), "HLEN", key)
            .subscribe(
                result -> callback.success(Result.<Long, Void>builder()
                    .output(result)
                    .build()),
                callback::error);
    }

    @DisplayName("HSET")
    @Throws({WrongTypeErrorTypeProvider.class, ArgumentErrorTypeProvider.class})
    public void hset(@Connection LettuceRedisConnection connection,
                     String key,
                     @Content Map<String, String> fields,
                     CompletionCallback<Long, Void> callback) {
        LOGGER.debug("HSET {} {}", key, fields);
        if (null == fields || fields.size() == 0) {
            callback.error(new ArgumentException("HSET", new IllegalArgumentException("fields object must not be empty")));
        } else {
            mapWrongTypeError(connection.commands().hset(key, fields), "HSET", key)
                .subscribe(
                    result -> callback.success(Result.<Long, Void>builder()
                        .output(result)
                        .build()),
                    callback::error);
        }
    }
}
