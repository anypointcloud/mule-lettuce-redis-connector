package cloud.anypoint.redis.internal.operation;

import cloud.anypoint.redis.api.LettucePagingProvider;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.ScanOutputTypeResolver;
import io.lettuce.core.KeyScanArgs;
import io.lettuce.core.KeyScanCursor;
import org.mule.runtime.core.api.util.StringUtils;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.runtime.streaming.PagingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(SearchOperations.class);

    @OutputResolver(output = ScanOutputTypeResolver.class)
    public PagingProvider<LettuceRedisConnection, String> searchKeys(
                            @Optional String match,
                            @Optional String type,
                            @Optional Integer pageSizeHint) {
        LOGGER.debug("Search keys with SCAN");
        KeyScanArgs args = new KeyScanArgs();
        if (!StringUtils.isEmpty(match)) {
            args.match(match);
        }
        if (null != pageSizeHint) {
            args.limit(pageSizeHint);
        }
        if (!StringUtils.isEmpty(type)) {
            args.type(type);
        }

        return new LettucePagingProvider<String>((connection, cursor) ->
                connection.commands().scan(KeyScanCursor.of(cursor), args));
    }

}
