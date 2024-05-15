package cloud.anypoint.redis.internal.source;

import cloud.anypoint.redis.api.ChannelAttributes;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;

import java.util.List;

@DisplayName("PSUBSCRIBE")
@Alias("subscribe-channel-pattern")
@MediaType(value = "text/plain", strict = false)
public class ChannelPatternListener extends BaseChannelListener {
    protected final Logger LOGGER = LoggerFactory.getLogger(ChannelPatternListener.class);
    private Disposable subscriptionDisposer;

    @Parameter
    private List<String> patterns;

    @Override
    public void onStart(SourceCallback<String, ChannelAttributes> sourceCallback) throws MuleException {
        LOGGER.debug("Starting channel pattern listener {}", patterns);
        super.onStart(sourceCallback);
        subscriptionDisposer = connection.commands().observePatterns().subscribe(
                message -> sourceCallback.handle(Result.<String, ChannelAttributes>builder()
                        .output(message.getMessage())
                        .attributes(new ChannelAttributes() {{
                            setChannel(message.getChannel());
                        }})
                        .build()),
                e -> sourceCallback.onConnectionException(new ConnectionException(e)));
        connection.commands().psubscribe(patterns.stream().toArray(String[]::new))
                .doOnError(e -> sourceCallback.onConnectionException(new ConnectionException(e)))
                .doOnSuccess(v -> LOGGER.trace("Completed PSUBSCRIBE {}", patterns))
                .subscribe();
    }

    @Override
    public void onStop() {
        LOGGER.debug("Stopping channel pattern listener {}", patterns);
        super.onStop();
        subscriptionDisposer.dispose();
        connection.commands().punsubscribe(patterns.toArray(new String[0]))
                .doOnSuccess(result -> LOGGER.trace("PUNSUBSCRIBE succeeded {}", patterns))
                .subscribe();
    }
}
