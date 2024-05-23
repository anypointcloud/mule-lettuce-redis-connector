package cloud.anypoint.redis.api.paging;

import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ValueScanCursor;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.runtime.streaming.PagingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public abstract class LettucePagingProvider<T> implements PagingProvider<LettuceRedisConnection, T> {

    protected String cursor = "0";
    protected boolean finished = false;

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
