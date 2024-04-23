package cloud.anypoint.redis.internal.operation;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
