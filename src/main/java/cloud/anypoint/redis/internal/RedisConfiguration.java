package cloud.anypoint.redis.internal;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnectionProvider;
import cloud.anypoint.redis.internal.operation.*;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

//https://lettuce.io/core/release/reference/#getting-started.start-coding
@Operations({DynamicOperations.class, SearchOperations.class, HashCommandOperations.class, KeyValueCommandOperations.class, SetCommandOperations.class, ListCommandOperations.class, SortedSetCommandOperations.class})
@ConnectionProviders(LettuceRedisConnectionProvider.class)
public class RedisConfiguration {

}
