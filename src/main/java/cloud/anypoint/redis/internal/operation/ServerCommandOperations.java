package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;
import cloud.anypoint.redis.api.CommandReturnType;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.AllCommandsErrorTypeProvider;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class ServerCommandOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(ServerCommandOperations.class);

    @DisplayName("PING")
    @MediaType(value = "text/plain", strict = true)
    @Throws(AllCommandsErrorTypeProvider.class)
    public void ping(@Connection LettuceRedisConnection connection,
                     @Optional String message,
                     CompletionCallback<String, Void> callback) {
        LOGGER.debug("PING");
        Mono<String> cmd = connection.commands().ping();
        if (null != message) {
            cmd = connection.customCommands().dynamic("PING", Collections.singletonList(message), CommandReturnType.STRING)
                    .map(o -> o.toString());
        }

        mapErrors(cmd, "PING").subscribe(
            result -> callback.success(Result.<String, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }
}
