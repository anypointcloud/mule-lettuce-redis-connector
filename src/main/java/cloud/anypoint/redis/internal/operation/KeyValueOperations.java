package cloud.anypoint.redis.internal.operation;

import cloud.anypoint.redis.api.CursorResult;
import cloud.anypoint.redis.internal.NilValueException;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.NilErrorTypeProvider;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.SetArgs;
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
import reactor.core.publisher.Mono;

import java.util.List;

public class KeyValueOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(KeyValueOperations.class);
    @DisplayName("SET")
    @MediaType(value = MediaType.TEXT_PLAIN, strict = false)
    public void set(@Connection LettuceRedisConnection connection,
                    String key,
                    @Content String value,
                    @Optional @DisplayName("XX") boolean xx,
                    @Optional @DisplayName("NX") boolean nx,
                    @Optional @DisplayName("GET") boolean get,
                    @Optional @DisplayName("EX") Integer ex,
                    @Optional @DisplayName("PX") Integer px,
                    @Optional @DisplayName("EXAT") Integer exat,
                    @Optional @DisplayName("PXAT") Integer pxat,
                    @Optional @DisplayName("KEEPTTL") boolean keepttl,
                    CompletionCallback<String, Void> callback) {
        LOGGER.debug("SET {}", key);
        SetArgs args = new SetArgs();
        if (xx) {
            args = args.xx();
        }
        if (nx) {
            args = args.nx();
        }
        if (null != ex) {
            args = args.ex(ex);
        }
        if (null != px) {
            args = args.px(px);
        }
        if (null != exat) {
            args = args.exAt(exat);
        }
        if (null != pxat) {
            args = args.pxAt(pxat);
        }
        if (keepttl) {
            args = args.keepttl();
        }
        Mono<String> cmd = connection.commands().set(key, value, args);
        if (get) {
            cmd = connection.commands().setGet(key, value, args);
        }
        cmd.subscribe(
                result -> callback.success(Result.<String, Void>builder()
                        .output(result)
                        .build()),
                callback::error);
    }

    @DisplayName("GET")
    @MediaType(value = MediaType.TEXT_PLAIN, strict = false)
    @Throws(NilErrorTypeProvider.class)
    public void get(@Connection LettuceRedisConnection connection,
                    String key,
                    CompletionCallback<String, Void> callback) {
        LOGGER.debug("GET {}", key);
        connection.commands().get(key)
                .switchIfEmpty(Mono.error(new NilValueException("GET", key)))
                .subscribe(
                    result -> {
                        LOGGER.trace("GET result {}", result);
                        callback.success(Result.<String, Void>builder()
                                .output(result)
                                .build());
                    },
                    error -> {
                        LOGGER.warn("GET error {}", error.getMessage());
                        callback.error(error);
                    });
    }

    @DisplayName("SCAN")
    public void scan(@Connection LettuceRedisConnection connection,
                     Integer cursor,
                     @Optional String match,
                     @Optional int count,
                     @Optional String type,
                     CompletionCallback<CursorResult<String>, Void> callback) {
        ScanArgs args = new ScanArgs();
        connection.commands().scan(ScanCursor.of(cursor.toString()), args)
                .subscribe(
                    result -> callback.success(
                        Result.<CursorResult<String>, Void>builder()
                            .output(new CursorResult<String>(Integer.parseInt(result.getCursor()), result.getKeys()))
                            .build()),
                    callback::error
                );
    }
}
