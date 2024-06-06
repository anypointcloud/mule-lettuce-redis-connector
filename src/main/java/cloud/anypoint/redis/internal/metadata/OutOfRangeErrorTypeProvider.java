package cloud.anypoint.redis.internal.metadata;

import cloud.anypoint.redis.api.error.ConnectorError;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.HashSet;
import java.util.Set;

public class OutOfRangeErrorTypeProvider implements ErrorTypeProvider {
    @Override
    public Set<ErrorTypeDefinition> getErrorTypes() {
        Set<ErrorTypeDefinition> errorTypes = new HashSet<>();
        errorTypes.add(ConnectorError.OUT_OF_RANGE);
        return errorTypes;    }
}
