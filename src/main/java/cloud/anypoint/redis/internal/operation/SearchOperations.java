package cloud.anypoint.redis.internal.operation;

import cloud.anypoint.redis.api.LettuceKeyPagingProvider;
import cloud.anypoint.redis.api.LettucePagingProvider;
import cloud.anypoint.redis.api.LettuceValuePagingProvider;
import cloud.anypoint.redis.internal.connection.LettuceRedisConnection;
import io.lettuce.core.*;
import org.mule.runtime.core.api.util.StringUtils;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.runtime.streaming.PagingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SearchOperations {
    private final Logger LOGGER = LoggerFactory.getLogger(SearchOperations.class);

    @Summary("Uses the <a href=\"https://redis.io/docs/latest/commands/scan/\">SCAN</a> command repeatedly to retrieve all keys that match the arguments, streaming the results and automatically handling the cursor returned from redis.")
    @MediaType(value = "application/java", strict = true)
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
                connection.commands().scan(KeyScanCursor.of(cursor), args));
    }

    @Summary("Uses the <a href=\"https://redis.io/docs/latest/commands/sscan/\">SSCAN</a> command repeatedly to retrieve all set members that match the arguments, streaming the results and automatically handling the cursor returned from redis.")
    @MediaType(value = "application/java", strict = true)
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
                connection.commands().sscan(key, ValueScanCursor.of(cursor), args));
    }

}
