package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;

import cloud.anypoint.redis.api.GeoLocation;
import cloud.anypoint.redis.api.GeospatialItem;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.exception.ArgumentException;
import cloud.anypoint.redis.internal.metadata.ArgumentErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.AllCommandsErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import io.lettuce.core.GeoAddArgs;
import io.lettuce.core.GeoCoordinates;
import io.lettuce.core.GeoValue;
import io.lettuce.core.Value;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

public class GeoCommandOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(GeoCommandOperations.class);

    @DisplayName("GEOADD")
    @Throws({ArgumentErrorTypeProvider.class, AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void geoadd(@Connection LettuceRedisConnection connection,
                       String key,
                       List<GeospatialItem> items,
                       @Optional @DisplayName("NX") boolean nx,
                       @Optional @DisplayName("XX") boolean xx,
                       @Optional @DisplayName("CH") boolean ch,
                       CompletionCallback<Long, Void> callback) {
        LOGGER.debug("GEOADD {}", key);
        if (items.isEmpty()) {
            callback.error(new ArgumentException("GEOADD", new IllegalArgumentException("GEOADD requires at least one item")));
            return;
        }
        GeoAddArgs args = new GeoAddArgs();
        if (nx) args = args.nx();
        if (xx) args = args.xx();
        if (ch) args = args.ch();
        Stream<GeoValue<String>> geoValues = items.stream()
                .map(item -> GeoValue.just(item.getLongitude(), item.getLatitude(), item.getName()));
        Mono<Long> cmd = connection.commands().geoadd(key, args, geoValues.toArray(GeoValue[]::new));
        mapErrors(cmd, "GEOADD", key).subscribe(
            result -> callback.success(Result.<Long, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }

    @DisplayName("GEOPOS")
    @Throws({ArgumentErrorTypeProvider.class, AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void geopos(@Connection LettuceRedisConnection connection,
                       String key,
                       @Content List<String> members,
                       CompletionCallback<List<GeoLocation>, Void> callback) {
        LOGGER.debug("GEOPOS {} {}", key, members);
        Flux<Value<GeoCoordinates>> baseCommand = connection.commands().geopos(key, members.stream().toArray(String[]::new));
        Mono<List<GeoLocation>> cmd = baseCommand
                .map(v -> v.map(coords -> new GeoLocation(coords.getX().doubleValue(), coords.getY().doubleValue())).getValueOrElse(null))
                .collectList();
        mapErrors(cmd, "GEOPOS").subscribe(
            result -> callback.success(Result.<List<GeoLocation>, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }
}
