package cloud.anypoint.redis.internal.operation;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import cloud.anypoint.redis.api.SortOrder;
import cloud.anypoint.redis.api.geospatial.*;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.exception.ArgumentException;
import cloud.anypoint.redis.internal.metadata.ArgumentErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.AllCommandsErrorTypeProvider;
import cloud.anypoint.redis.internal.metadata.GeoSearchOutputTypeResolver;
import cloud.anypoint.redis.internal.metadata.WrongTypeErrorTypeProvider;
import io.lettuce.core.*;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                          @ParameterDsl(allowReferences = false) @Expression(NOT_SUPPORTED) GeoSearchCenter searchCenter,
                          @ParameterDsl(allowReferences = false) @Expression(NOT_SUPPORTED) GeoSearchBy searchBy,
                          SortOrder sortOrder,
                          @DisplayName("COUNT") @Optional Integer count,
                          @DisplayName("ANY") @Optional boolean countAny,
                          @MetadataKeyId @ParameterGroup(name = "Result options") @Placement(tab = "Return")
                              GeoSearchResultOption searchResultOptions,
                          CompletionCallback<List, Void> callback) {
        GeoArgs args = new GeoArgs();
        switch (sortOrder) {
            case ASC:
                args = args.asc();
                break;
            case DESC:
                args = args.desc();
                break;
        }
        if (null != count) {
            args = args.withCount(count, countAny);
        }
        if (searchResultOptions.isWithCoord()) {
            args = args.withCoordinates();
        }
        if (searchResultOptions.isWithDist()) {
            args = args.withDistance();
        }
        if (searchResultOptions.isWithHash()) {
            args = args.withHash();
        }
        Flux<GeoWithin<String>> baseCmd = connection.commands().geosearch(key, searchCenter.reference(), searchBy.getPredicate(), args);
        Flux<Object> cmd = baseCmd.map( result -> {
            if (searchResultOptions.hasDetails()) {
                Map<String, Object> details = new HashMap<>();
                details.put("member", result.getMember());
                if (searchResultOptions.isWithCoord()) {
                    details.put("longitude", result.getCoordinates().getX());
                    details.put("latitude", result.getCoordinates().getY());
                }
                if (searchResultOptions.isWithDist()) {
                    details.put("distance", result.getDistance());
                }
                if (searchResultOptions.isWithHash()) {
                    details.put("hash", result.getGeohash());
                }
                return details;
            } else {
                return result.getMember();
            }
        });
        mapErrors(cmd.collectList(), "GEOSEARCH", key).subscribe(
            result -> callback.success(Result.<List, Void>builder()
                .output(result)
                .build()),
            callback::error);
    }
}
