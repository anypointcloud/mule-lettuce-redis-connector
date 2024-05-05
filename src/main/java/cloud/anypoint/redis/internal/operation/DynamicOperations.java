package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;
import cloud.anypoint.redis.internal.exception.NilValueException;
import cloud.anypoint.redis.api.CommandReturnType;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.DynamicCommandOutputTypeResolver;
import cloud.anypoint.redis.internal.metadata.NilErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;


public class DynamicOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(DynamicOperations.class);

    @OutputResolver(output = DynamicCommandOutputTypeResolver.class)
    @MediaType(value = "application/java", strict = false)
    @Throws({NilErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void sendCommand(@Connection LettuceRedisConnection connection,
                            String command,
                            List<String> arguments,
                            @MetadataKeyId CommandReturnType returnType,
                            CompletionCallback<Object, Void> callback) {
        LOGGER.debug("dynamic command {} with args {}", command, arguments);
        mapErrors(connection.customCommands().dynamic(command, arguments, returnType), command, String.join(" ", arguments))
                // TODO: add validator parameter to control whether we throw NilValueException
                .switchIfEmpty(Mono.error(new NilValueException(command)))
                .subscribe(
                        (result) -> callback.success(
                                Result.<Object, Void>builder()
                                        .output(result)
                                        .build()),
                        callback::error
                );
    }
}
