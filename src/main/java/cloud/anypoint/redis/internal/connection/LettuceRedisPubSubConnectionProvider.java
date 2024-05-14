package cloud.anypoint.redis.internal.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.extension.api.annotation.Alias;
import reactor.core.publisher.Mono;

@Alias("pubsub")
public class LettuceRedisPubSubConnectionProvider extends BaseLettuceConnectionProvider<LettuceRedisPubSubConnection> implements Disposable {
    @Override
    public LettuceRedisPubSubConnection connect() throws ConnectionException {
        return new LettuceRedisPubSubConnection(redisClient);
    }

    @Override
    public void disconnect(LettuceRedisPubSubConnection lettuceRedisPubSubConnection) {
        lettuceRedisPubSubConnection.invalidate();
    }

    @Override
    public ConnectionValidationResult validate(LettuceRedisPubSubConnection connection) {
        return connection.commands().ping()
            .map((r) -> ConnectionValidationResult.success())
            .onErrorResume((t) ->
                Mono.just(ConnectionValidationResult.failure(
                    t.getMessage(),
                    new Exception(t))))
            .block();
    }
}
