package cloud.anypoint.redis.internal;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnectionProvider;
import cloud.anypoint.redis.internal.operation.DynamicOperations;
import cloud.anypoint.redis.internal.operation.KeyValueOperations;
import cloud.anypoint.redis.internal.operation.SetOperations;
import cloud.anypoint.redis.internal.operation.SortedSetOperations;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

//https://lettuce.io/core/release/reference/#getting-started.start-coding
@Operations({DynamicOperations.class, KeyValueOperations.class, SetOperations.class, SortedSetOperations.class})
@ConnectionProviders(LettuceRedisConnectionProvider.class)
public class RedisConfiguration {

}
