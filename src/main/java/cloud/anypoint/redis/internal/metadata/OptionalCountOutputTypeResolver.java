package cloud.anypoint.redis.internal.metadata;

import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataKey;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;
import org.mule.runtime.api.metadata.resolving.TypeKeysResolver;

import java.util.Collections;
import java.util.Set;

public class OptionalCountOutputTypeResolver implements TypeKeysResolver, OutputTypeResolver<Integer> {

    @Override
    public MetadataType getOutputType(MetadataContext metadataContext, Integer integer) throws MetadataResolvingException, ConnectionException {
        if (null == integer) {
            return metadataContext.getTypeBuilder()
                    .stringType()
                    .build();
        }
        return metadataContext.getTypeBuilder()
                .arrayType().of(metadataContext.getTypeBuilder().stringType())
                .build();
    }

    @Override
    public Set<MetadataKey> getKeys(MetadataContext metadataContext) throws MetadataResolvingException, ConnectionException {
        return Collections.emptySet();
    }

    @Override
    public String getResolverName() {
        return OutputTypeResolver.super.getResolverName();
    }

    @Override
    public String getCategoryName() {
        return "Redis Reply";
    }
}
