package cloud.anypoint.redis.internal.operation;

import cloud.anypoint.redis.internal.commands.CommandReturnType;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.DynamicCommandOutputTypeResolver;
import io.lettuce.core.SetArgs;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;


public class DynamicOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(DynamicOperations.class);

    @OutputResolver(output = DynamicCommandOutputTypeResolver.class)
    public void sendCommand(@Connection LettuceRedisConnection connection,
                            String command,
                            List<String> arguments,
                            @MetadataKeyId CommandReturnType returnType,
                            CompletionCallback<Object, Void> callback) {

        connection.customCommands().dynamic(command, arguments, returnType)
                .subscribe(
                        (result) -> callback.success(
                                Result.<Object, Void>builder()
                                        .output(result)
                                        .build()),
                        callback::error
                );
    }
}
