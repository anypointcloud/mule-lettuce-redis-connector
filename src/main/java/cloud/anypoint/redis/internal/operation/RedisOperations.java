package cloud.anypoint.redis.internal.operation;

import cloud.anypoint.redis.internal.commands.CommandReturnType;
import cloud.anypoint.redis.internal.commands.RuntimeCommand;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.DynamicCommandOutputTypeResolver;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.ZAddArgs;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.Command;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


public class RedisOperations {

    private final Logger LOGGER = LoggerFactory.getLogger(RedisOperations.class);

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

    @DisplayName("ZADD")
    public void zadd(@Connection LettuceRedisConnection connection,
                     String key,
                     @DisplayName("XX") boolean xx,
                     @DisplayName("NX") boolean nx,
                     @DisplayName("GT") boolean gt,
                     @DisplayName("LT") boolean lt,
                     @DisplayName("CH") boolean ch,
                     @Content Map<String, Double> memberScores,
                     CompletionCallback<Long, Void> callback) {
        ScoredValue<String>[] scoredValues = memberScores.entrySet().stream()
                .map((entry) -> ScoredValue.just(entry.getValue(), entry.getKey()))
                .toArray(ScoredValue[]::new);
        ZAddArgs args = new ZAddArgs();
        if (xx) {
            args = args.xx();
        }
        if (nx) {
            args = args.nx();
        }
        if (gt) {
            args = args.gt();
        }
        if (lt) {
            args = args.lt();
        }
        if (ch) {
            args = args.ch();
        }
        connection.commands().zadd(key, args, scoredValues)
                .subscribe(
                        (result) -> callback.success(
                                Result.<Long, Void>builder()
                                        .output(result)
                                        .build()),
                        callback::error);
    }
}
