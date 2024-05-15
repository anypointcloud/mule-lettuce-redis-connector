package cloud.anypoint.redis.internal;

import cloud.anypoint.redis.internal.connection.LettuceRedisPubSubConnectionProvider;
import cloud.anypoint.redis.internal.source.ChannelListener;
import cloud.anypoint.redis.internal.source.ChannelPatternListener;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

@Configuration(name = "pubsub")
@Sources({ChannelListener.class, ChannelPatternListener.class})
@ConnectionProviders(LettuceRedisPubSubConnectionProvider.class)
public class RedisPubSubConfiguration {
}
