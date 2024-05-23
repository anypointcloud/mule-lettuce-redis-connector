package cloud.anypoint.redis.api.paging;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import io.lettuce.core.KeyScanCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class LettuceKeyPagingProvider<T> extends LettucePagingProvider<T> {
    private final Logger LOGGER = LoggerFactory.getLogger(LettuceKeyPagingProvider.class);
    private final BiFunction<LettuceRedisConnection, String, Mono<KeyScanCursor<T>>> cursorCommand;

    public LettuceKeyPagingProvider(BiFunction<LettuceRedisConnection, String, Mono<KeyScanCursor<T>>> cursorCommand) {
        this.cursorCommand = cursorCommand;
    }

    @Override
    public List<T> getPage(LettuceRedisConnection connection) {
        if (finished) {
            LOGGER.trace("cursor is finished");
            return new ArrayList<>();
        }

        KeyScanCursor<T> keyScanCursor = this.cursorCommand.apply(connection, cursor).block();
        cursor = keyScanCursor.getCursor();
        LOGGER.trace("[{}] got page of {}", cursor, keyScanCursor.getKeys().size());
        // while loop here so that we can skip empty pages when cursor is nonzero
        while (keyScanCursor.getKeys().isEmpty() && !keyScanCursor.isFinished()) {
            LOGGER.trace("found empty page");
            keyScanCursor = this.cursorCommand.apply(connection, cursor).block();
            LOGGER.trace("[{}] got page of {}", cursor, keyScanCursor.getKeys().size());
            cursor = keyScanCursor.getCursor();
        }
        if (keyScanCursor.isFinished()) {
            this.finished = true;
        }
        return keyScanCursor.getKeys();
    }

}
