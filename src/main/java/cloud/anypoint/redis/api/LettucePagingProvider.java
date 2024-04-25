package cloud.anypoint.redis.api;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import io.lettuce.core.KeyScanCursor;
import org.mule.runtime.api.exception.MuleException;
import org.mule.sdk.api.runtime.streaming.PagingProvider;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.BiFunction;

public class LettucePagingProvider<T> implements PagingProvider<LettuceRedisConnection, T> {

    private String cursor = "0";
    private final BiFunction<LettuceRedisConnection, String, Mono<KeyScanCursor<T>>> cursorCommand;

    public LettucePagingProvider(BiFunction<LettuceRedisConnection, String, Mono<KeyScanCursor<T>>> cursorCommand) {
        this.cursorCommand = cursorCommand;
    }

    @Override
    public List<T> getPage(LettuceRedisConnection connection) {
        KeyScanCursor<T> keyScanCursor = null;
        // while loop here so that we can skip empty pages when cursor is nonzero
        while (keyScanCursor == null ||
                keyScanCursor.getKeys().isEmpty() && !keyScanCursor.isFinished()) {
            // this is annoying that we have to block... asked some muleys about
            // CompletionCallback and PagingpProvider. We'll see what they say.
            keyScanCursor = this.cursorCommand.apply(connection, cursor).block();
            cursor = keyScanCursor.getCursor();
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
