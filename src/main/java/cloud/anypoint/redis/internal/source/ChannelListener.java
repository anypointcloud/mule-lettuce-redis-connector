package cloud.anypoint.redis.internal.source;

import cloud.anypoint.redis.api.ChannelAttributes;
import cloud.anypoint.redis.internal.connection.LettuceRedisPubSubConnection;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;

import java.io.InputStream;
import java.util.List;

@DisplayName("SUBSCRIBE")
@Alias("subscribe-channel")
@MediaType(value = "text/plain", strict = false)
public class ChannelListener extends Source<String, ChannelAttributes> {
    private final Logger LOGGER = LoggerFactory.getLogger(ChannelListener.class);

    @Connection
    private ConnectionProvider<LettuceRedisPubSubConnection> connectionProvider;
    private LettuceRedisPubSubConnection connection;

    @Parameter
    private List<String> channels;

    private Disposable subscriptionDisposer;

    @Override
    public void onStart(SourceCallback<String, ChannelAttributes> sourceCallback) throws MuleException {
        LOGGER.debug("Starting channel listener {}", channels);
        connection = connectionProvider.connect();
        connection.commands().subscribe(channels.stream().toArray(String[]::new))
            .doOnError(e -> sourceCallback.onConnectionException(new ConnectionException(e)))
            .doOnSuccess(v -> LOGGER.trace("Completed SUBSCRIBE {}", channels))
            .subscribe();
        subscriptionDisposer = connection.commands().observeChannels().subscribe(
            message -> sourceCallback.handle(Result.<String, ChannelAttributes>builder()
                .output(message.getMessage())
                .attributes(new ChannelAttributes() {{
                    setChannel(message.getChannel());
                }})
                .build()),
            e -> sourceCallback.onConnectionException(new ConnectionException(e)));
    }

    @Override
    public void onStop() {
        LOGGER.debug("Stopping channel listener {}", channels);
        subscriptionDisposer.dispose();
        connection.invalidate();
        connection.commands().unsubscribe(channels.toArray(new String[0]))
            .doOnSuccess(result -> LOGGER.trace("UNSUBSCRIBE succeeded {}", channels))
            .subscribe();
    }
}
