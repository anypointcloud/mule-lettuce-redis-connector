package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.AllCommandsErrorTypeProvider;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelCommandOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(ChannelCommandOperations.class);

    @DisplayName("PUBLISH")
    @Throws(AllCommandsErrorTypeProvider.class)
    public void publish(@Connection LettuceRedisConnection connection,
                        String channel,
                        String message,
                        CompletionCallback<Long, Void> callback) {
        mapErrors(connection.commands().publish(channel, message), "PUBLISH", channel)
            .subscribe(
                result -> callback.success(Result.<Long, Void>builder()
                    .output(result)
                    .build()),
                callback::error);
    }
}
