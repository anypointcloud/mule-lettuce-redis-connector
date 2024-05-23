package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;

import cloud.anypoint.redis.api.attributes.ScanAttributes;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.exception.ArgumentException;
import cloud.anypoint.redis.internal.exception.NilValueException;
import cloud.anypoint.redis.internal.metadata.ArgumentErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.NilErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.TimeoutErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import io.lettuce.core.*;
import org.mule.runtime.core.api.util.StringUtils;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class HashCommandOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(HashCommandOperations.class);

    @DisplayName("HGETALL")
    @Throws({TimeoutErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void hgetall(@Connection LettuceRedisConnection connection,
                        String key,
                        CompletionCallback<Map<String, String>, Void> callback) {
        LOGGER.debug("HGETALL {}", key);
        Mono<Map<String, String>> cmd = connection.commands().hgetall(key)
                .collectMap(KeyValue::getKey, Value::getValue);
        mapErrors(cmd, "HGETALL", key)
            .subscribe(
                result -> callback.success(Result.<Map<String, String>, Void>builder()
                    .output(result)
                    .build()),
                callback::error);
    }

    @DisplayName("HEXISTS")
    @Throws({TimeoutErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void hexists(@Connection LettuceRedisConnection connection,
                        String key,
                        String field,
                        CompletionCallback<Boolean, Void> callback) {
        LOGGER.debug("HEXISTS {} {}", key, field);
        mapErrors(connection.commands().hexists(key, field), "HEXISTS", field)
            .subscribe(
                result -> callback.success(Result.<Boolean, Void>builder()
                    .output(result)
                    .build()),
                callback::error);
    }

    @DisplayName("HGET")
    @MediaType(value = "text/plain", strict = false)
    @Throws({TimeoutErrorTypeProvider.class, WrongTypeErrorTypeProvider.class, NilErrorTypeProvider.class})
    public void hget(@Connection LettuceRedisConnection connection,
                     String key,
                     String field,
                     CompletionCallback<String, Void> callback) {
        LOGGER.debug("HGET {} {}", key, field);
        mapErrors(connection.commands().hget(key, field), "HGET", key)
            // TODO: Add validator parameter to make this optional
            .switchIfEmpty(Mono.error(new NilValueException("HGET", key)))
            .subscribe(
                result ->
                    callback.success(Result.<String, Void>builder()
                        .output(result)
                        .build()),
                callback::error);
    }

    @DisplayName("HMGET")
    @Throws({ArgumentErrorTypeProvider.class, TimeoutErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void hmget(@Connection LettuceRedisConnection connection,
                      String key,
                      @Content List<String> fieldNames,
                      CompletionCallback<List<String>, Void> callback) {
        if (fieldNames.isEmpty()) {
            callback.error(new ArgumentException("HMGET", new IllegalArgumentException("at least one field is required")));
        } else {
            Flux<KeyValue<String, String>> cmd = connection.commands().hmget(key, fieldNames.stream().toArray(String[]::new));
            mapErrors(cmd.map(kv -> kv.getValue()).collectList(), "HMGET", key)
                .subscribe(
                    result -> callback.success(Result.<List<String>, Void>builder()
                        .output(result)
                        .build()),
                    callback::error);
        }
    }

    @DisplayName("HLEN")
    @Throws({TimeoutErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void hlen(@Connection LettuceRedisConnection connection,
                     String key,
                     CompletionCallback<Long, Void> callback) {
        LOGGER.debug("HLEN {}", key);
        mapErrors(connection.commands().hlen(key), "HLEN", key)
            .subscribe(
                result -> callback.success(Result.<Long, Void>builder()
                    .output(result)
                    .build()),
                callback::error);
    }

    @DisplayName("HSET")
    @Throws({ArgumentErrorTypeProvider.class, TimeoutErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void hset(@Connection LettuceRedisConnection connection,
                     String key,
                     @Content Map<String, String> fields,
                     CompletionCallback<Long, Void> callback) {
        LOGGER.debug("HSET {} {}", key, fields);
        if (null == fields || fields.size() == 0) {
            callback.error(new ArgumentException("HSET", new IllegalArgumentException("fields object must not be empty")));
        } else {
            mapErrors(connection.commands().hset(key, fields), "HSET", key)
                .subscribe(
                    result -> callback.success(Result.<Long, Void>builder()
                        .output(result)
                        .build()),
                    callback::error);
        }
    }

    @DisplayName("HSCAN")
    @Throws({TimeoutErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void hscan(@Connection LettuceRedisConnection connection,
                      String key,
                      Integer cursor,
                      @Optional String match,
                      @Optional Integer count,
                      CompletionCallback<Map<String, String>, ScanAttributes> callback) {
        ScanArgs args = new ScanArgs();
        if (!StringUtils.isEmpty(match)) {
            args.match(match);
        }
        if (null != count) {
            args.limit(count);
        }
        LOGGER.debug("HSCAN {} {}", key, cursor);
        Mono<MapScanCursor<String, String>> cmd = connection.commands().hscan(key, ScanCursor.of(cursor.toString()), args);
        mapErrors(cmd, "HSCAN", key)
                .subscribe(
                        result -> callback.success(
                                Result.<Map<String, String>, ScanAttributes>builder()
                                        .output(result.getMap())
                                        .attributes(new ScanAttributes() {{
                                            LOGGER.debug("cursor {}", result.getCursor());
                                            setCursor(Integer.parseInt(result.getCursor()));
                                        }})
                                        .build()),
                        callback::error);
    }
}
