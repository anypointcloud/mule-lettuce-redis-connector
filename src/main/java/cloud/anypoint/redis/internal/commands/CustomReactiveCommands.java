package cloud.anypoint.redis.internal.commands;

import io.lettuce.core.AbstractRedisReactiveCommands;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.ObjectOutput;
import io.lettuce.core.output.ValueOutput;
import java.util.List;
import java.util.stream.Collectors;

import io.lettuce.core.protocol.CommandArgs;
import org.mulesoft.common.ext.Diff;
import reactor.core.publisher.Mono;

public class CustomReactiveCommands extends AbstractRedisReactiveCommands<String, String> {
    public CustomReactiveCommands(StatefulConnection<String, String> connection, RedisCodec<String, String> codec) {
        super(connection, codec);
    }

    public Mono<Object> dynamic(String command, List<String> arguments, CommandReturnType returnType) {
        CommandArgs<String, String> args = new CommandArgs<>(StringCodec.UTF8);
        args.addValues(arguments);
        // take the first item from the flux. Does not support streaming this way.
        return dispatch(new RuntimeCommand(command), new ObjectOutput<String, String>(StringCodec.UTF8), args)
                .next();
    }
}
