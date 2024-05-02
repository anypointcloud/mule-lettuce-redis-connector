package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapWrongTypeError;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import io.lettuce.core.KeyValue;
import io.lettuce.core.Value;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import reactor.core.publisher.Mono;

import java.util.Map;

public class HashCommandOperations {

    @DisplayName("HGETALL")
    @Throws(WrongTypeErrorTypeProvider.class)
    public void hgetall(@Connection LettuceRedisConnection connection,
                        String key,
                        CompletionCallback<Map<String, String>, Void> callback) {
        Mono<Map<String, String>> cmd = connection.commands().hgetall(key)
                .collectMap(KeyValue::getKey, Value::getValue);
        mapWrongTypeError(cmd, "HGETALL", key)
            .subscribe(
                result -> callback.success(Result.<Map<String, String>, Void>builder()
                    .output(result)
                    .build()),
                callback::error);
    }
}
