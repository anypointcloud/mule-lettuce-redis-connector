package cloud.anypoint.redis.internal.connection;

import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

import org.mule.runtime.api.connection.*;
import org.mule.runtime.api.lifecycle.Disposable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class LettuceRedisConnectionProvider extends BaseLettuceConnectionProvider<LettuceRedisConnection> implements Disposable {

    private final Logger LOGGER = LoggerFactory.getLogger(LettuceRedisConnectionProvider.class);

    @Override
    public LettuceRedisConnection connect() throws ConnectionException {
        return new LettuceRedisConnection(redisClient);
    }

    @Override
    public void disconnect(LettuceRedisConnection connection) {
        connection.invalidate();
    }

    @Override
    public ConnectionValidationResult validate(LettuceRedisConnection connection) {
        return connection.commands().ping()
            .map((r) -> ConnectionValidationResult.success())
            .onErrorResume((t) ->
                Mono.just(ConnectionValidationResult.failure(
                    t.getMessage(),
                    new Exception(t))))
            .block();
    }

}
