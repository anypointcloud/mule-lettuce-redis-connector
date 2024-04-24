package cloud.anypoint.redis.internal.operation;

import cloud.anypoint.redis.api.ScanAttributes;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import io.lettuce.core.KeyScanArgs;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import org.mule.runtime.core.api.util.StringUtils;
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

public class SetOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(SortedSetOperations.class);

    @DisplayName("SADD")
    public void sadd(@Connection LettuceRedisConnection connection,
                     String key,
                     @Content List<String> members,
                     CompletionCallback<Long, Void> callback) {
        connection.commands().sadd(key, members.stream().toArray(String[]::new))
                .subscribe(
                        result -> callback.success(Result.<Long, Void>builder()
                                .output(result)
                                .build()),
                        callback::error
                );
    }

    @DisplayName("SREM")
    public void srem(@Connection LettuceRedisConnection connection,
                     String key,
                     @Content List<String> members,
                     CompletionCallback<Long, Void> callback) {
        connection.commands().srem(key, members.stream().toArray(String[]::new))
                .subscribe(
                        result -> callback.success(Result.<Long, Void>builder()
                                .output(result)
                                .build()),
                        callback::error
                );
    }

    @DisplayName("SRANDMEMBER")
    public void srandmember(@Connection LettuceRedisConnection connection,
                            String key,
                            @Optional Integer count,
                            CompletionCallback<List<String>, Void> callback) {
        Flux<String> cmd = Flux.from(connection.commands().srandmember(key));
        if (null != count) {
            cmd = connection.commands().srandmember(key, count);
        }
        cmd.collectList().subscribe(
                result -> callback.success(Result.<List<String>, Void>builder()
                        .output(result)
                        .build()),
                callback::error
        );
    }

    @DisplayName("SSCAN")
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
        connection.commands().sscan(key, ScanCursor.of(cursor.toString()), args)
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
