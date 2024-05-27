package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;

import cloud.anypoint.redis.api.SortOrder;
import cloud.anypoint.redis.api.geospatial.*;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.exception.ArgumentException;
import cloud.anypoint.redis.internal.metadata.ArgumentErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.AllCommandsErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.GeoSearchOutputTypeResolver;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import io.lettuce.core.*;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.*;
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

    @DisplayName("GEODIST")
    @Throws({AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void geodist(@Connection LettuceRedisConnection connection,
                        String key,
                        String member1,
                        String member2,
                        @Optional(defaultValue = "M") DistanceUnit unit,
                        CompletionCallback<Double, Void> callback) {
        LOGGER.debug("GEODIST {} {} {}", key, member1, member2);
        GeoArgs.Unit unitArg = GeoArgs.Unit.m;
        switch (unit) {
            case M:
                unitArg = GeoArgs.Unit.m;
            break;
            case KM:
                unitArg = GeoArgs.Unit.km;
            break;
            case MI:
                unitArg = GeoArgs.Unit.mi;
            break;
            case FT:
                unitArg = GeoArgs.Unit.ft;
        }
        Mono<Double> cmd = connection.commands().geodist(key, member1, member2, unitArg);
        mapErrors(cmd, "GEODIST", key).subscribe(
            result -> callback.success(Result.<Double, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }

    @DisplayName("GEOSEARCH")
    @OutputResolver(output = GeoSearchOutputTypeResolver.class)
    @Throws({ArgumentErrorTypeProvider.class, AllCommandsErrorTypeProvider.class, WrongTypeErrorTypeProvider.class})
    public void geosearch(@Connection LettuceRedisConnection connection,
                          String key,
                          @DisplayName("FROMMEMBER") @Optional String fromMember,
                          @DisplayName("FROMLONLAT") @Optional GeoLocation fromLocation,
                          @DisplayName("BYRADIUS") @Optional ByRadiusOption byRadius,
                          @DisplayName("BYBOX") @Optional ByBoxOption byBox,
                          SortOrder sortOrder,
                          @DisplayName("COUNT") @Optional Integer count,
                          @DisplayName("ANY") @Optional boolean countAny,
                          @MetadataKeyId @ParameterGroup(name = "Result options") GeoSearchResultOption searchResultOptions,
                          CompletionCallback<List, Void> callback) {
        if (null == fromMember && null == fromLocation) {
            callback.error(new ArgumentException("GEOSEARCH", new IllegalArgumentException("At least one of FROMMEMBER or FROMLONLAT is required")));
            return;
        }
        if (null != fromMember && null != fromLocation) {
            callback.error(new ArgumentException("GEOSEARCH", new IllegalArgumentException("Only one of FROMMEMBER or FROMLATLON may be provided")));
            return;
        }

        callback.error(new ArgumentException("GEOSEARCH", new IllegalArgumentException("not implemented yet do not use this operation")));
    }
}
