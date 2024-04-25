package cloud.anypoint.redis.internal.metadata;

import cloud.anypoint.redis.api.CommandReturnType;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataKey;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;
import org.mule.runtime.api.metadata.resolving.TypeKeysResolver;

import java.util.Collections;
import java.util.Set;

public class DynamicCommandOutputTypeResolver implements TypeKeysResolver, OutputTypeResolver<CommandReturnType> {
    @Override
    // FIXME: this needs to use the metadata key
    public MetadataType getOutputType(MetadataContext metadataContext, CommandReturnType commandReturnType) throws MetadataResolvingException, ConnectionException {
        return metadataContext.getTypeBuilder().anyType().build();
    }

    @Override
    public String getCategoryName() {
        return "Redis Reply";
    }

    @Override
    public Set<MetadataKey> getKeys(MetadataContext metadataContext) throws MetadataResolvingException, ConnectionException {
        return Collections.emptySet();
    }

    @Override
    public String getResolverName() {
        return TypeKeysResolver.super.getResolverName();
    }
}
