package cloud.anypoint.redis.internal.connection;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;

public class LettuceRedisPubSubConnection {
    private StatefulRedisPubSubConnection<String, String> connection;

    public LettuceRedisPubSubConnection(RedisClient client) {
        connection = client.connectPubSub();
    }

    public void invalidate() {
        connection.close();
    }

    public RedisPubSubReactiveCommands<String, String> commands() {
        return connection.reactive();
    }
}
