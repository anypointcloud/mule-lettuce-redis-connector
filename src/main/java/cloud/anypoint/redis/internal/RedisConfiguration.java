package cloud.anypoint.redis.internal;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnectionProvider;
import cloud.anypoint.redis.internal.operation.*;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

//https://lettuce.io/core/release/reference/#getting-started.start-coding
@Operations({
        ChannelCommandOperations.class,
        DynamicOperations.class,
        GeoCommandOperations.class,
        ListCommandOperations.class,
        HashCommandOperations.class,
        KeyValueCommandOperations.class,
        SearchOperations.class,
        ServerCommandOperations.class,
        SetCommandOperations.class,
        SortedSetCommandOperations.class,
        StreamCommandOperations.class})
@ConnectionProviders({LettuceRedisConnectionProvider.class})
public class RedisConfiguration {

}
