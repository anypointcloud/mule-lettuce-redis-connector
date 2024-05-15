package cloud.anypoint.redis.internal.source;

import cloud.anypoint.redis.api.ChannelAttributes;
import cloud.anypoint.redis.internal.connection.LettuceRedisPubSubConnection;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;

public abstract class BaseChannelListener extends Source<String, ChannelAttributes> {
    private final Logger LOGGER = LoggerFactory.getLogger(BaseChannelListener.class);
    @Connection
    private ConnectionProvider<LettuceRedisPubSubConnection> connectionProvider;
    protected LettuceRedisPubSubConnection connection;

    @Override
    public void onStart(SourceCallback<String, ChannelAttributes> sourceCallback) throws MuleException {
        connection = connectionProvider.connect();
    }

    @Override
    public void onStop() {
        connection.invalidate();
    }
}
