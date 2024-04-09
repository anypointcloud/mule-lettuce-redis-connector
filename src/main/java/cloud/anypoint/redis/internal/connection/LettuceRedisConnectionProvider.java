package cloud.anypoint.redis.internal.connection;

import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.mule.runtime.api.connection.*;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private RedisClient redisClient;
  @Override
  public void initialise() throws InitialisationException {
    try {
      RedisURI uri = RedisURI.Builder
              .redis(host, port)
              .withSsl(tls)
              .withPassword(password) // TODO: CredentialsProvider to allow expressions
              .build();
      this.redisClient = RedisClient.create(uri);
    }
    catch (IllegalStateException e) {
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
    return ConnectionValidationResult.success();
  }

}
