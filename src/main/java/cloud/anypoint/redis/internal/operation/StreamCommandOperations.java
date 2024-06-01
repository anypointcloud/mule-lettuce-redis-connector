package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;

import cloud.anypoint.redis.api.stream.StreamEntry;
import cloud.anypoint.redis.api.stream.StreamEvictionOption;
import cloud.anypoint.redis.api.stream.StreamWatermark;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.exception.ArgumentException;
import cloud.anypoint.redis.internal.metadata.AllCommandsErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.ArgumentErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XAddArgs;
import io.lettuce.core.XReadArgs;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamCommandOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(StreamCommandOperations.class);

    @DisplayName("XADD")
    @MediaType(value = "text/plain", strict = true)
    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class, ArgumentErrorTypeProvider.class})
    public void xadd(@Connection LettuceRedisConnection connection,
                     String key,
                     @Optional @DisplayName("NOMKSTREAM") boolean noMkStream,
                     @Optional String id,
                     @Optional StreamEvictionOption evictionOption,
                     @Content Map<String, String> entry,
                     CompletionCallback<String, Void> callback) {
        LOGGER.debug("XADD {}", key);
        XAddArgs args = new XAddArgs();
        if (noMkStream) {
            args.nomkstream(noMkStream);
        }
        if (null != evictionOption) {
            args = evictionOption.decorate(args);
        }
        if (null == id) {
            id = "*";
        }
        args = args.id(id);
        mapErrors(connection.commands().xadd(key, args, entry), "XADD", key).subscribe(
            result -> callback.success(Result.<String, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }

    @DisplayName("XREAD")
    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class, ArgumentErrorTypeProvider.class})
    public void xread(@Connection LettuceRedisConnection connection,
                      @DisplayName("COUNT") @Optional Integer count,
                      @DisplayName("BLOCK") @Optional Integer block,
                      @DisplayName("STREAM") @Alias("stream-watermarks") @ParameterDsl(allowReferences = false) List<StreamWatermark> streamWatermarks,
                      CompletionCallback<Map<String, List<StreamEntry>>, Void> callback) {
        LOGGER.debug("XREAD {}", streamWatermarks.stream().map(w -> w.getKey()).collect(Collectors.joining(", ")));
        if (streamWatermarks.isEmpty()) {
            callback.error(new ArgumentException("XREAD", new IllegalArgumentException("STREAM must contain at least one key and id")));
            return;
        }
        XReadArgs args = new XReadArgs();
        if (null != count) {
            args = args.count(count);
        }
        if (null != block) {
            args = args.block(block);
        }
        XReadArgs.StreamOffset<String>[] offsets = streamWatermarks.stream()
                .map(w -> XReadArgs.StreamOffset.from(w.getKey(), w.getId()))
                .toArray(XReadArgs.StreamOffset[]::new);

        Flux<StreamMessage<String, String>> cmd = connection.commands().xread(args, offsets);

        mapErrors(cmd.collectList(), "XREAD").subscribe(
            result -> {
                Map<String, List<StreamEntry>> resultMap = new HashMap<>();
                result.stream().forEach( streamMessage -> {
                    if (! (resultMap.containsKey(streamMessage.getStream()))) {
                        resultMap.put(streamMessage.getStream(), new ArrayList<>());
                    }
                    resultMap.get(streamMessage.getStream()).add(
                            new StreamEntry(streamMessage.getId(), streamMessage.getBody()));
                });
                callback.success(Result.<Map<String, List<StreamEntry>>, Void>builder()
                        .output(resultMap)
                        .build());
            },
            callback::error);
    }
}
