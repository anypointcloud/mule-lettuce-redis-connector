package cloud.anypoint.redis.internal.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class (as it's name implies) provides connection instances and the functionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * This particular example is a {@link PoolingConnectionProvider} which declares that connections resolved by this provider
 * will be pooled and reused. There are other implementations like {@link CachedConnectionProvider} which lazily creates and
 * caches connections or simply {@link org.mule.runtime.api.connection.ConnectionProvider} if you want a new connection each time something requires one.
 */
public class RealRedisConnectionProvider implements PoolingConnectionProvider<RealRedisConnection> {

  private final Logger LOGGER = LoggerFactory.getLogger(RealRedisConnectionProvider.class);

 /**
  * A parameter that is always required to be configured.
  */
  @Parameter
  private String requiredParameter;

 /**
  * A parameter that is not required to be configured by the user.
  */
  @DisplayName("Friendly Name")
  @Parameter
  @Optional(defaultValue = "100")
  private int optionalParameter;

  @Override
  public RealRedisConnection connect() throws ConnectionException {
    return new RealRedisConnection(requiredParameter + ":" + optionalParameter);
  }

  @Override
  public void disconnect(RealRedisConnection connection) {
    try {
      connection.invalidate();
    } catch (Exception e) {
      LOGGER.error("Error while disconnecting [" + connection.getId() + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public ConnectionValidationResult validate(RealRedisConnection connection) {
    return ConnectionValidationResult.success();
  }
}
