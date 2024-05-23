package cloud.anypoint.redis.internal.source;

import cloud.anypoint.redis.api.attributes.ChannelAttributes;
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

@DisplayName("SUBSCRIBE")
@Alias("subscribe-channel")
@MediaType(value = "text/plain", strict = false)
public class ChannelListener extends BaseChannelListener {
    protected final Logger LOGGER = LoggerFactory.getLogger(ChannelListener.class);
    private Disposable subscriptionDisposer;

    @Parameter
    private List<String> channels;

    @Override
    public void onStart(SourceCallback<String, ChannelAttributes> sourceCallback) throws MuleException {
        LOGGER.debug("Starting channel listener {}", channels);
        super.onStart(sourceCallback);
        subscriptionDisposer = connection.commands().observeChannels().subscribe(
                message -> sourceCallback.handle(Result.<String, ChannelAttributes>builder()
                        .output(message.getMessage())
                        .attributes(new ChannelAttributes() {{
                            setChannel(message.getChannel());
                        }})
                        .build()),
                e -> sourceCallback.onConnectionException(new ConnectionException(e)));
        connection.commands().subscribe(channels.stream().toArray(String[]::new))
                .doOnError(e -> sourceCallback.onConnectionException(new ConnectionException(e)))
                .doOnSuccess(v -> LOGGER.trace("Completed SUBSCRIBE {}", channels))
                .subscribe();
    }

    @Override
    public void onStop() {
        LOGGER.debug("Stopping channel listener {}", channels);
        super.onStop();
        subscriptionDisposer.dispose();
        connection.commands().unsubscribe(channels.toArray(new String[0]))
            .doOnSuccess(result -> LOGGER.trace("UNSUBSCRIBE succeeded {}", channels))
            .subscribe();
    }
}
