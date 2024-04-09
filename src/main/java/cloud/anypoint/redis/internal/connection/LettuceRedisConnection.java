package cloud.anypoint.redis.internal.connection;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ZAddArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;

public final class LettuceRedisConnection {
  private final StatefulRedisConnection<String, String> connection;

  public LettuceRedisConnection(RedisClient client) {
    connection = client.connect();
  }

  public void invalidate() {
    connection.close();
  }

  public RedisReactiveCommands<String, String> commands() {
    return connection.reactive();
  }

}
