package cloud.anypoint.redis.internal.connection;


import io.lettuce.core.RedisURI;

public final class LettuceRedisConnection {

  public LettuceRedisConnection(RedisURI uri) {

  }

  public void invalidate() {
    // do something to invalidate this connection!
  }
}
