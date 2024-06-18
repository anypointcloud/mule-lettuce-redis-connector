package cloud.anypoint.redis.api.paging;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import io.lettuce.core.MapScanCursor;
import io.lettuce.core.ScoredValueScanCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class LettuceScoredValuePagingProvider  extends LettucePagingProvider<Map<String, Double>> {
    private final Logger LOGGER = LoggerFactory.getLogger(LettuceScoredValuePagingProvider.class);
    private final BiFunction<LettuceRedisConnection, String, Mono<ScoredValueScanCursor<String>>> cursorCommand;

    public LettuceScoredValuePagingProvider(BiFunction<LettuceRedisConnection, String, Mono<ScoredValueScanCursor<String>>> cursorCommand) {
        this.cursorCommand = cursorCommand;
    }

    @Override
    public List<Map<String, Double>> getPage(LettuceRedisConnection connection) {
        if (finished) {
            LOGGER.trace("cursor is finished");
            return new ArrayList<>();
        }

        ScoredValueScanCursor<String> scanCursor = this.cursorCommand.apply(connection, cursor).block();
        cursor = scanCursor.getCursor();
        LOGGER.trace("[{}] got page of {}", cursor, scanCursor.getValues().size());
        // while loop here so that we can skip empty pages when cursor is nonzero
        while (scanCursor.getValues().isEmpty() && !scanCursor.isFinished()) {
            LOGGER.trace("found empty page");
            scanCursor = this.cursorCommand.apply(connection, cursor).block();
            LOGGER.trace("[{}] got page of {}", cursor, scanCursor.getValues().size());
            cursor = scanCursor.getCursor();
        }
        if (scanCursor.isFinished()) {
            this.finished = true;
        }
        return scanCursor.getValues().stream()
            .map(scoredValue -> Collections.singletonMap(scoredValue.getValue(), scoredValue.getScore()))
            .collect(Collectors.toList());
    }
}
