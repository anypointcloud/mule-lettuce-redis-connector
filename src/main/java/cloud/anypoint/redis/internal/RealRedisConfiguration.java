package cloud.anypoint.redis.internal;

import cloud.anypoint.redis.internal.connection.RealRedisConnectionProvider;
import cloud.anypoint.redis.internal.operation.RealRedisOperations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Parameter;

@org.mule.runtime.extension.api.annotation.Operations(RealRedisOperations.class)
@ConnectionProviders(RealRedisConnectionProvider.class)
public class RealRedisConfiguration {

  @Parameter
  private String configId;

  public String getConfigId(){
    return configId;
  }
}
