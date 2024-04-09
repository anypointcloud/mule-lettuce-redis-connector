package cloud.anypoint.redis.internal.operation;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.api.operation.args.ZAddArguments;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.ZAddArgs;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;

import java.util.Map;


public class RedisOperations {
  public void jsonSet(@Connection LettuceRedisConnection connection) {
    // dispatch
  }

  public void zadd(@Connection LettuceRedisConnection connection,
                   String key,
                   ZAddArguments options,
                   @Content Map<String, Double> memberScores,
                   CompletionCallback<Long, Void> callback) {
    ScoredValue<String>[] scoredValues = memberScores.entrySet().stream()
            .map((entry) -> ScoredValue.just(entry.getValue(), entry.getKey()))
            .toArray(ScoredValue[]::new);
    ZAddArgs args = new ZAddArgs();
    if (options.isXx()) {
      args = args.xx();
    }
    if (options.isNx()) {
      args = args.nx();
    }
    if (options.isGt()) {
      args = args.gt();
    }
    if (options.isLt()) {
      args = args.lt();
    }
    if (options.isCh()) {
      args = args.ch();
    }
    connection.commands().zadd(key, args, scoredValues)
            .subscribe(
                    (result) -> callback.success(
                      Result.<Long, Void>builder()
                              .output(result)
                              .build()),
                    callback::error);
  }
}
