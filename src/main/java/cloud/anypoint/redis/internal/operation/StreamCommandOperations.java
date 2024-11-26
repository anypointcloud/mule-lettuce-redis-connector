package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;

import cloud.anypoint.redis.api.stream.PendingDetailsArgs;
import cloud.anypoint.redis.api.stream.StreamEntry;
import cloud.anypoint.redis.api.stream.StreamEvictionOption;
import cloud.anypoint.redis.api.stream.StreamWatermark;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.exception.ArgumentException;
import cloud.anypoint.redis.internal.metadata.*;
import io.lettuce.core.*;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
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

    @DisplayName("XRANGE")
    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class, ArgumentErrorTypeProvider.class})
    public void xrange(@Connection LettuceRedisConnection connection,
                       String key,
                       String start,
                       String end,
                       @Optional Integer count,
                       CompletionCallback<Map<String, Map<String, String>>, Void> callback) {
        Flux<StreamMessage<String, String>> baseCmd = connection.commands().xrange(key, Range.create(start, end));
        if (null != count) {
            baseCmd = connection.commands().xrange(key, Range.create(start, end), Limit.from(count));
        }
        Mono<Map<String, Map<String, String>>> cmd = baseCmd.collectMap(StreamMessage::getId, StreamMessage::getBody);
        mapErrors(cmd, "XRANGE", key)
            .switchIfEmpty(Mono.just(new HashMap<>()))
            .subscribe(
                result -> callback.success(Result.<Map<String, Map<String, String>>, Void>builder()
                    .output(result)
                    .build()),
                callback::error);
    }

    @DisplayName("XREAD")
    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class, ArgumentErrorTypeProvider.class})
    public void xread(@Connection LettuceRedisConnection connection,
                      @DisplayName("COUNT") @Optional Integer count,
                      @DisplayName("BLOCK") @Optional Integer block,
                      @DisplayName("STREAMS") @Alias("streams") @ParameterDsl(allowReferences = false) List<StreamWatermark> streamWatermarks,
                      CompletionCallback<Map<String, List<StreamEntry>>, Void> callback) {
        LOGGER.debug("XREAD {}", streamWatermarks.stream().map(w -> w.getKey()).collect(Collectors.joining(", ")));
        if (streamWatermarks.isEmpty()) {
            callback.error(new ArgumentException("XREAD", new IllegalArgumentException("STREAMS must contain at least one key and id")));
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

    @DisplayName("XREADGROUP")
    @Throws({AllCommandsErrorTypeProvider.class, ArgumentErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void xreadgroup(@Connection LettuceRedisConnection connection,
                           String group,
                           String consumer,
                           @DisplayName("COUNT") @Optional Long count,
                           @DisplayName("BLOCK") @Optional Long block,
                           @DisplayName("NOACK") @Optional boolean noack,
                           @DisplayName("STREAMS") @Alias("streams") @ParameterDsl(allowReferences = false) List<StreamWatermark> streamWatermarks,
                           CompletionCallback<Map<String, List<StreamEntry>>, Void> callback) {
        LOGGER.debug("XREADGROUP {}", streamWatermarks.stream().map(w -> w.getKey()).collect(Collectors.joining(", ")));
        if (streamWatermarks.isEmpty()) {
            callback.error(new ArgumentException("XREADGROUP", new IllegalArgumentException("STREAMS must contain at least one key and id")));
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

        Flux<StreamMessage<String, String>> cmd = connection.commands().xreadgroup(Consumer.from(group, consumer), args, offsets);

        mapErrors(cmd.collectList(), "XREADGROUP").subscribe(
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

    @DisplayName("XACK")
    @Throws({AllCommandsErrorTypeProvider.class, ArgumentErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void xack(@Connection LettuceRedisConnection connection,
                     String key,
                     String group,
                     @ParameterDsl(allowReferences = false) List<String> ids,
                     CompletionCallback<Long, Void> callback) {
        LOGGER.debug("XACK {} {}", key, group);
        if (ids.isEmpty()) {
            callback.error(new ArgumentException("XACK", new IllegalArgumentException("XACK requires at least one entry id")));
            return;
        }
        Mono<Long> cmd = connection.commands().xack(key, group, ids.stream().toArray(String[]::new));
        mapErrors(cmd, "XACK", key).subscribe(
            result -> callback.success(Result.<Long, Void>builder()
                .output(result)
                .build()),
            callback::error
        );
    }

    @DisplayName("XDEL")
    @Throws({AllCommandsErrorTypeProvider.class, ArgumentErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void xdel(@Connection LettuceRedisConnection connection,
                     String key,
                     @ParameterDsl(allowReferences = false) List<String> ids,
                     CompletionCallback<Long, Void> callback) {
        LOGGER.debug("XDEL {}", key);
        if (ids.isEmpty()) {
            callback.error(new ArgumentException("XDEL", new IllegalArgumentException("at least one id is required")));
            return;
        }
        Mono<Long> cmd = connection.commands().xdel(key, ids.stream().toArray(String[]::new));
        mapErrors(cmd, "XDEL", key).subscribe(
            result -> callback.success(Result.<Long, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }

    @DisplayName("XTRIM")
    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void xtrim(@Connection LettuceRedisConnection connection,
                      String key,
                      @ParameterDsl(allowReferences = false) @Expression(ExpressionSupport.NOT_SUPPORTED) StreamEvictionOption evictionOption,
                      CompletionCallback<Long, Void> callback) {
        LOGGER.debug("XTRIM {}", key);
        XTrimArgs args = evictionOption.decorate(new XTrimArgs());
        Mono<Long> cmd = connection.commands().xtrim(key, args);
        mapErrors(cmd, "XTRIM", key).subscribe(
            result -> callback.success(Result.<Long, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }
// TOOD: Complete XPENDING
//    @DisplayName("XPENDING")
//    @MediaType(value = "application/java", strict = true)
//    @OutputResolver(output = XPendingOutputTypeResolver.class)
//    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
//    public void xpending(@Connection LettuceRedisConnection connection,
//                         String key,
//                         String group,
//                         @Optional
//                             @ParameterDsl(allowReferences = false)
//                             @Expression(ExpressionSupport.NOT_SUPPORTED)
//                             @MetadataKeyId
//                             PendingDetailsArgs pendingDetailsArgs,
//                         CompletionCallback<Object, Void> callback) {
//        XPendingArgs args = new XPendingArgs();
//    }

    @DisplayName("XGROUP CREATE")
    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void xgroupCreate(@Connection LettuceRedisConnection connection,
                             String key,
                             String group,
                             @Optional @Summary("ID of the last delivered entry. Use $ or leave this blank to indicate the current last entry of the stream") String lastEntryId,
                             @DisplayName("MKSTREAM") boolean mkstream,
                             @DisplayName("ENTRIESREAD") @Optional Long entriesRead,
                             CompletionCallback<Void, Void> callback) {
        LOGGER.debug("XGROUP CREATE {} {}", key, group);
        XReadArgs.StreamOffset<String> offset = XReadArgs.StreamOffset.latest(key);
        if (null != lastEntryId) {
            offset = XReadArgs.StreamOffset.from(key, lastEntryId);
        }
        XGroupCreateArgs args = new XGroupCreateArgs();
        if (mkstream) {
            args = args.mkstream(mkstream);
        }
        if (null != entriesRead) {
            args = args.entriesRead(entriesRead);
        }
        Mono<String> cmd = connection.commands().xgroupCreate(offset, group, args);
        mapErrors(cmd, "XGROUP CREATE", key).subscribe(
            result -> callback.success(Result.<Void, Void>builder()
                .build()),
            callback::error);
    }

    @DisplayName("XGROUP DESTROY")
    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void xgroupDestroy(@Connection LettuceRedisConnection connection,
                              String key,
                              String group,
                              CompletionCallback<Boolean, Void> callback) {
        LOGGER.debug("XGROUP DESTROY {} {}", key, group);
        Mono<Boolean> cmd = connection.commands().xgroupDestroy(key, group);
        mapErrors(cmd, "XGROUP DESTROY", key).subscribe(
            result -> callback.success(Result.<Boolean, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }

    @DisplayName("XINFO GROUPS")
    @OutputResolver(output = XinfoOutputTypeResolver.class)
    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void xinfoGroups(@Connection LettuceRedisConnection connection,
                            String key,
                            CompletionCallback<List<Map<String, Object>>, Void> callback) {
        LOGGER.debug("XINFO GROUPS {}", key);
        Flux<Map<String, Object>> baseCmd = connection.commands().xinfoGroups(key)
            .mapNotNull(o -> (Flux.fromIterable((List<Object>) o)
                .buffer(2)
                .collectMap(pair -> pair.get(0).toString(), pair -> pair.get(1))
                .block()));
        mapErrors(baseCmd.collectList(), "XINFO GROUPS", key).subscribe(
            result -> callback.success(Result.<List<Map<String, Object>>, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }
}
