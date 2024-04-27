package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapWrongTypeError;
import cloud.anypoint.redis.api.ScanAttributes;
import cloud.anypoint.redis.internal.WrongTypeException;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.ValueScanCursor;
import org.mule.runtime.core.api.util.StringUtils;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class SetCommandOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(SortedSetCommandOperations.class);

    @DisplayName("SADD")
    @Throws(WrongTypeErrorTypeProvider.class)
    public void sadd(@Connection LettuceRedisConnection connection,
                     String key,
                     @Content List<String> members,
                     CompletionCallback<Long, Void> callback) {
        LOGGER.debug("SADD {} {}", key, members);
        Mono<Long> cmd = connection.commands().sadd(key, members.stream().toArray(String[]::new));
        mapWrongTypeError(cmd, "SADD", key)
                .subscribe(
                        result -> callback.success(Result.<Long, Void>builder()
                                .output(result)
                                .build()),
                        callback::error
                );
    }

    @DisplayName("SREM")
    @Throws(WrongTypeErrorTypeProvider.class)
    public void srem(@Connection LettuceRedisConnection connection,
                     String key,
                     @Content List<String> members,
                     CompletionCallback<Long, Void> callback) {
        LOGGER.debug("SREM {} {}", key, members);
        Mono<Long> cmd = connection.commands().srem(key, members.stream().toArray(String[]::new));
        mapWrongTypeError(cmd, "SREM", key)
                .subscribe(
                        result -> callback.success(Result.<Long, Void>builder()
                                .output(result)
                                .build()),
                        callback::error
                );
    }

    @DisplayName("SISMEMBER")
    @Throws(WrongTypeErrorTypeProvider.class)
    public void sismember(@Connection LettuceRedisConnection connection,
                          String key,
                          String member,
                          CompletionCallback<Boolean, Void> callback) {
        LOGGER.debug("SISMEMBER {} {}", key, member);
        Mono<Boolean> cmd = connection.commands().sismember(key, member);
        mapWrongTypeError(cmd, "SISMEMBER", key)
                .onErrorMap(RedisCommandExecutionException.class, t -> {
                    if (t.getMessage().startsWith("WRONGTYPE")) {
                        return new WrongTypeException("SISMEMBER", key, t);
                    }
                    return t;
                })
                .subscribe(
                    result -> callback.success(Result.<Boolean, Void>builder()
                        .output(result)
                        .build()),
                    callback::error
        );
    }

    @DisplayName("SMISMEMBER")
    @Throws(WrongTypeErrorTypeProvider.class)
    public void smismember(@Connection LettuceRedisConnection connection,
                           String key,
                           @Content List<String> members,
                           CompletionCallback<List<Boolean>, Void> callback) {
        LOGGER.debug("SMISMEMBER {} {}", key, members);
        Mono<List<Boolean>> cmd = connection.commands().smismember(key, members.stream().toArray(String[]::new))
                .collectList();
        mapWrongTypeError(cmd, "SMISMEMBER", key)
                .subscribe(result -> callback.success(Result.<List<Boolean>, Void>builder()
                        .output(result)
                        .build()),
                callback::error);
    }

    @DisplayName("SRANDMEMBER")
    @Throws(WrongTypeErrorTypeProvider.class)
    public void srandmember(@Connection LettuceRedisConnection connection,
                            String key,
                            @Optional Integer count,
                            CompletionCallback<List<String>, Void> callback) {
        LOGGER.debug("SRANDMEMBER {}", key);
        Flux<String> cmd = Flux.from(connection.commands().srandmember(key));
        if (null != count) {
            cmd = connection.commands().srandmember(key, count);
        }
        mapWrongTypeError(cmd.collectList(), "SRANDMEMBER", key)
                .subscribe(
                    result -> callback.success(Result.<List<String>, Void>builder()
                        .output(result)
                        .build()),
                 callback::error
        );
    }

    @DisplayName("SSCAN")
    @Throws(WrongTypeErrorTypeProvider.class)
    public void sscan(@Connection LettuceRedisConnection connection,
                      String key,
                      Integer cursor,
                      @Optional String match,
                      @Optional Integer count,
                      CompletionCallback<List<String>, ScanAttributes> callback) {
        ScanArgs args = new ScanArgs();
        if (!StringUtils.isEmpty(match)) {
            args.match(match);
        }
        if (null != count) {
            args.limit(count);
        }
        LOGGER.debug("SSCAN {} {}", key, cursor);
        Mono<ValueScanCursor<String>> cmd = connection.commands().sscan(key, ScanCursor.of(cursor.toString()), args);
        mapWrongTypeError(cmd, "SSCAN", key)
                .onErrorMap(RedisCommandExecutionException.class, t -> {
                    if (t.getMessage().startsWith("WRONGTYPE")) {
                        return new WrongTypeException("SSCAN", key, t);
                    }
                    return t;
                })
                .subscribe(
                        result -> callback.success(
                                Result.<List<String>, ScanAttributes>builder()
                                        .output(result.getValues())
                                        .attributes(new ScanAttributes() {{
                                            LOGGER.debug("cursor {}", result.getCursor());
                                            setCursor(Integer.parseInt(result.getCursor()));
                                        }})
                                        .build()),
                        callback::error
                );
    }
}