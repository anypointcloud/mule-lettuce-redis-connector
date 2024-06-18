package cloud.anypoint.redis.api.paging;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import io.lettuce.core.MapScanCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class LettuceMapPagingProvider extends LettucePagingProvider<Map<String, String>> {
    private final Logger LOGGER = LoggerFactory.getLogger(LettuceMapPagingProvider.class);
    private final BiFunction<LettuceRedisConnection, String, Mono<MapScanCursor<String, String>>> cursorCommand;

    public LettuceMapPagingProvider(BiFunction<LettuceRedisConnection, String, Mono<MapScanCursor<String, String>>> cursorCommand) {
        this.cursorCommand = cursorCommand;
    }

    @Override
    public List<Map<String, String>> getPage(LettuceRedisConnection connection) {
        if (finished) {
            LOGGER.trace("cursor is finished");
            return new ArrayList<>();
        }

        MapScanCursor<String, String> mapScanCursor = this.cursorCommand.apply(connection, cursor).block();
        cursor = mapScanCursor.getCursor();
        LOGGER.trace("[{}] got page of {}", cursor, mapScanCursor.getMap().size());
        // while loop here so that we can skip empty pages when cursor is nonzero
        while (mapScanCursor.getMap().isEmpty() && !mapScanCursor.isFinished()) {
            LOGGER.trace("found empty page");
            mapScanCursor = this.cursorCommand.apply(connection, cursor).block();
            LOGGER.trace("[{}] got page of {}", cursor, mapScanCursor.getMap().size());
            cursor = mapScanCursor.getCursor();
        }
        if (mapScanCursor.isFinished()) {
            this.finished = true;
        }
        return Arrays.asList(mapScanCursor.getMap());
    }
}
