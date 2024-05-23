package cloud.anypoint.redis.internal.metadata;

import cloud.anypoint.redis.api.error.ConnectorError;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.HashSet;
import java.util.Set;

public class WrongTypeErrorTypeProvider implements ErrorTypeProvider {
    @Override
    public Set<ErrorTypeDefinition> getErrorTypes() {
        Set<ErrorTypeDefinition> errorTypes = new HashSet<>();
        errorTypes.add(ConnectorError.WRONGTYPE);
        return errorTypes;
    }
}