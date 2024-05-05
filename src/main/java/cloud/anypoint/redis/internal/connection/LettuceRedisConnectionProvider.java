package cloud.anypoint.redis.internal.connection;

import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.TimeoutOptions;
import org.mule.runtime.api.connection.*;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class LettuceRedisConnectionProvider implements CachedConnectionProvider<LettuceRedisConnection>, Initialisable, Disposable {

    private final Logger LOGGER = LoggerFactory.getLogger(LettuceRedisConnectionProvider.class);

    @Parameter
    private String host;

    @Parameter
    private boolean tls;

    @Parameter
    private int port;

    @Parameter
    private String password;


    @Optional
    @Parameter
    @DisplayName("Command Timeout")
    @Placement(tab = "Advanced", order = 1)
    private Integer commandTimeout;

    @Optional
    @Parameter
    @DisplayName("Command Timeout Time Unit")
    @Placement(tab = "Advanced", order = 2)
    private TimeUnit commandTimeoutUnit;

    private RedisClient redisClient;

    @Override
    public void initialise() throws InitialisationException {
        try {
            // workaround for an issue similar to this: https://github.com/redis/lettuce/issues/1767
            System.setProperty("io.lettuce.core.jfr", "false");
            RedisURI uri = RedisURI.Builder
                    .redis(host, port)
                    .withSsl(tls)
                    .withPassword(password) // TODO: CredentialsProvider to allow expressions
                    .build();

            this.redisClient = RedisClient.create(uri);
            if (null != commandTimeout) {
                this.redisClient.setOptions(ClientOptions.builder()
                        .timeoutOptions(TimeoutOptions.builder()
                                .fixedTimeout(Duration.ofMillis(commandTimeoutUnit.toMillis(commandTimeout)))
                                .build())
                        .build());
            }
        } catch (IllegalStateException e) {
            throw new InitialisationException(createStaticMessage(e.getLocalizedMessage()), e, this);
        }
    }

    @Override
    public void dispose() {
        this.redisClient.shutdown();
    }

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
