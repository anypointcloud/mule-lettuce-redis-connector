package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapWrongTypeError;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.exception.ArgumentException;
import cloud.anypoint.redis.internal.metadata.ArgumentErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ListCommandOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(SortedSetCommandOperations.class);

    @DisplayName("LPUSH")
    @Throws({WrongTypeErrorTypeProvider.class, ArgumentErrorTypeProvider.class})
    public void lpush(@Connection LettuceRedisConnection connection,
                      String key,
                      @Content List<String> members,
                      CompletionCallback<Long, Void> callback) {
        try {
            mapWrongTypeError(connection.commands().lpush(key, members.stream().toArray(String[]::new)), "LPUSH", key)
                    .subscribe(
                        result -> callback.success(Result.<Long, Void>builder()
                            .output(result)
                            .build()),
                        callback::error);
        }
        catch (IllegalArgumentException e) {
            callback.error(new ArgumentException("LPUSH", e));
        }
    }
}
