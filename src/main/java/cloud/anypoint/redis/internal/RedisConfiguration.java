package cloud.anypoint.redis.internal;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnectionProvider;
import cloud.anypoint.redis.internal.operation.RedisOperations;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Parameter;

//https://lettuce.io/core/release/reference/#getting-started.start-coding
@Operations(RedisOperations.class)
@ConnectionProviders(LettuceRedisConnectionProvider.class)
public class RedisConfiguration {


}
