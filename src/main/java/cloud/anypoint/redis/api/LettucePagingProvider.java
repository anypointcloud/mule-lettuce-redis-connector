package cloud.anypoint.redis.api;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import io.lettuce.core.KeyScanCursor;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.runtime.streaming.PagingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class LettucePagingProvider<T> implements PagingProvider<LettuceRedisConnection, T> {
    private final Logger LOGGER = LoggerFactory.getLogger(LettucePagingProvider.class);

    private String cursor = "0";
    private boolean finished = false;
    private final BiFunction<LettuceRedisConnection, String, Mono<KeyScanCursor<T>>> cursorCommand;

    public LettucePagingProvider(BiFunction<LettuceRedisConnection, String, Mono<KeyScanCursor<T>>> cursorCommand) {
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


    @Override
    public java.util.Optional<Integer> getTotalResults(LettuceRedisConnection lettuceRedisConnection) {
        return java.util.Optional.empty();
    }

    @Override
    public boolean useStickyConnections() {
        return false;
    }

    @Override
    public void close(LettuceRedisConnection lettuceRedisConnection) throws MuleException {
    }
}
