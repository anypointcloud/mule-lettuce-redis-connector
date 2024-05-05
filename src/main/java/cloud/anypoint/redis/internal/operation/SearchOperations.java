package cloud.anypoint.redis.internal.operation;

import cloud.anypoint.redis.api.LettuceKeyPagingProvider;
import cloud.anypoint.redis.api.LettuceValuePagingProvider;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import cloud.anypoint.redis.internal.metadata.TimeoutErrorTypeProvider;
import io.lettuce.core.*;
import org.mule.runtime.core.api.util.StringUtils;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.streaming.PagingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloud.anypoint.redis.internal.util.ErrorDecorator.mapErrors;


public class SearchOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(SearchOperations.class);

    @Summary("Uses the SCAN command repeatedly to retrieve all keys that match the arguments, streaming the results and automatically handling the cursor returned from redis.")
    @MediaType(value = "application/java", strict = true)
    @Throws(TimeoutErrorTypeProvider.class)
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

        return new LettuceKeyPagingProvider<String>((connection, cursor) ->
                mapErrors(connection.commands().scan(KeyScanCursor.of(cursor), args), "SCAN"));
    }

    @Summary("Uses the SSCAN command repeatedly to retrieve all set members that match the arguments, streaming the results and automatically handling the cursor returned from redis.")
    @MediaType(value = "application/java", strict = true)
    @Throws(TimeoutErrorTypeProvider.class)
    public PagingProvider<LettuceRedisConnection, String> searchSetMembers(
            String key,
            @Optional String match,
            @Optional Integer pageSizeHint) {
        LOGGER.debug("Search set members with SSCAN");
        ScanArgs args = new ScanArgs();
        if (!StringUtils.isEmpty(match)) {
            args.match(match);
        }
        if (null != pageSizeHint) {
            args.limit(pageSizeHint);
        }

        return new LettuceValuePagingProvider<String>((connection, cursor) ->
                mapErrors(connection.commands().sscan(key, ValueScanCursor.of(cursor), args), "SSCAN"));
    }
}
