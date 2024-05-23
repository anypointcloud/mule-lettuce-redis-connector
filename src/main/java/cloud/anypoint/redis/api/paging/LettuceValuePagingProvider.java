package cloud.anypoint.redis.api.paging;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import io.lettuce.core.ValueScanCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class LettuceValuePagingProvider<T> extends LettucePagingProvider<T> {
    private final Logger LOGGER = LoggerFactory.getLogger(LettuceValuePagingProvider.class);
    private final BiFunction<LettuceRedisConnection, String, Mono<ValueScanCursor<T>>> cursorCommand;

    public LettuceValuePagingProvider(BiFunction<LettuceRedisConnection, String, Mono<ValueScanCursor<T>>> cursorCommand) {
        this.cursorCommand = cursorCommand;
    }

    @Override
    public List<T> getPage(LettuceRedisConnection connection) {
        if (finished) {
            LOGGER.trace("cursor is finished");
            return new ArrayList<>();
        }

        ValueScanCursor<T> valueScanCursor = this.cursorCommand.apply(connection, cursor).block();
        cursor = valueScanCursor.getCursor();
        LOGGER.trace("[{}] got page of {}", cursor, valueScanCursor.getValues().size());
        // while loop here so that we can skip empty pages when cursor is nonzero
        while (valueScanCursor.getValues().isEmpty() && !valueScanCursor.isFinished()) {
            LOGGER.trace("found empty page");
            valueScanCursor = this.cursorCommand.apply(connection, cursor).block();
            LOGGER.trace("[{}] got page of {}", cursor, valueScanCursor.getValues().size());
            cursor = valueScanCursor.getCursor();
        }
        if (valueScanCursor.isFinished()) {
            this.finished = true;
        }
        return valueScanCursor.getValues();
    }
}
