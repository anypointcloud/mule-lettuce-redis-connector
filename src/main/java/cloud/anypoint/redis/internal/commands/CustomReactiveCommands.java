package cloud.anypoint.redis.internal.commands;

import io.lettuce.core.AbstractRedisReactiveCommands;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.ValueOutput;
import reactor.core.publisher.Mono;

public class CustomReactiveCommands extends AbstractRedisReactiveCommands<String, String> {
    public CustomReactiveCommands(StatefulConnection<String, String> connection, RedisCodec<String, String> codec) {
        super(connection, codec);
    }

    public Mono<Object> dynamic(String command, CommandReturnType returnType) {
        // take the first item from the flux. Does not support streaming this way.
        return Mono.from(dispatch(new RuntimeCommand(command), new ValueOutput<String, String>(StringCodec.UTF8)));
    }
}
