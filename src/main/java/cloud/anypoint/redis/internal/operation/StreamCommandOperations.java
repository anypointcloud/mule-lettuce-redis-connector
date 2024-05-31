package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;
import cloud.anypoint.redis.api.stream.StreamEvictionOption;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.AllCommandsErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.ArgumentErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import io.lettuce.core.XAddArgs;
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

import java.util.Map;

public class StreamCommandOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(StreamCommandOperations.class);

    @DisplayName("XADD")
    @MediaType(value = "text/plain", strict = true)
    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class, ArgumentErrorTypeProvider.class})
    public void xadd(@Connection LettuceRedisConnection connection,
                     String key,
                     @Optional @DisplayName("NOMKSTREAM") boolean noMkStream,
                     @Optional String id,
                     @Optional StreamEvictionOption evictionOption,
                     @Content Map<String, String> entry,
                     CompletionCallback<String, Void> callback) {
        XAddArgs args = new XAddArgs();
        if (noMkStream) {
            args.nomkstream(noMkStream);
        }
        if (null != evictionOption) {
            args = evictionOption.decorate(args);
        }
        if (null == id) {
            id = "*";
        }
        args = args.id(id);
        mapErrors(connection.commands().xadd(key, args, entry), "XADD", key).subscribe(
            result -> callback.success(Result.<String, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }
}
