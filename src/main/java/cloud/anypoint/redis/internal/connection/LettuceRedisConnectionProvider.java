package cloud.anypoint.redis.internal.connection;

import io.lettuce.core.RedisURI;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LettuceRedisConnectionProvider implements PoolingConnectionProvider<LettuceRedisConnection> {

  private final Logger LOGGER = LoggerFactory.getLogger(LettuceRedisConnectionProvider.class);

  @Parameter
  private String host;

  @Parameter
  private Boolean tls;

  @Parameter
  private int port;

  @Parameter
  private String password;

  @Override
  public LettuceRedisConnection connect() throws ConnectionException {
    RedisURI uri = RedisURI.Builder.redis(host, port).withSsl(tls).build();
    return new LettuceRedisConnection(uri);
  }

  @Override
  public void disconnect(LettuceRedisConnection connection) {
    try {
      connection.invalidate();
    } catch (Exception e) {
//      LOGGER.error("Error while disconnecting [" + connection.getId() + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public ConnectionValidationResult validate(LettuceRedisConnection connection) {
    return ConnectionValidationResult.success();
  }
}
