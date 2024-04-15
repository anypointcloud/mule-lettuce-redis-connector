package cloud.anypoint.redis.internal.commands;

import cloud.anypoint.redis.api.CommandReturnType;
import io.lettuce.core.AbstractRedisReactiveCommands;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.*;

import java.util.List;

import io.lettuce.core.protocol.CommandArgs;
import reactor.core.publisher.Mono;

public class CustomReactiveCommands extends AbstractRedisReactiveCommands<String, String> {
    public CustomReactiveCommands(StatefulConnection<String, String> connection, RedisCodec<String, String> codec) {
        super(connection, codec);
    }

    public Mono<Object> dynamic(String command, List<String> arguments, CommandReturnType returnType) {
        CommandArgs<String, String> args = new CommandArgs<>(StringCodec.UTF8);
        args.addValues(arguments);
        switch (returnType) {
            case STATUS:
                return Mono.from(dispatch(new RuntimeCommand(command), new StatusOutput<>(StringCodec.UTF8), args));
            case ARRAY:
                return Mono.from(dispatch(new RuntimeCommand(command), new ArrayOutput<>(StringCodec.UTF8), args));
            case LONG:
                return Mono.from(dispatch(new RuntimeCommand(command), new IntegerOutput<>(StringCodec.UTF8), args));
            case STRING:
                return Mono.from(dispatch(new RuntimeCommand(command), new ValueOutput<>(StringCodec.UTF8), args));
            default:
                // take the first item from the flux. Does not support streaming this way.
                return Mono.from(dispatch(new RuntimeCommand(command), new ObjectOutput<String, String>(StringCodec.UTF8), args));
        }
    }

}
