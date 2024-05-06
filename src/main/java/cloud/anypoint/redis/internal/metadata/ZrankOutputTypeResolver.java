package cloud.anypoint.redis.internal.metadata;

import org.mule.metadata.api.builder.ObjectTypeBuilder;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataKey;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;
import org.mule.runtime.api.metadata.resolving.TypeKeysResolver;

import java.util.Collections;
import java.util.Set;

public class ZrankOutputTypeResolver implements TypeKeysResolver, OutputTypeResolver<Boolean> {

    @Override
    public MetadataType getOutputType(MetadataContext metadataContext, Boolean b) throws MetadataResolvingException, ConnectionException {
        if (b) {
            ObjectTypeBuilder outputPayloadTypeBuilder = metadataContext.getTypeBuilder().objectType();
            outputPayloadTypeBuilder.addField().key("rank").value(metadataContext.getTypeBuilder().numberType());
            outputPayloadTypeBuilder.addField().key("score").value(metadataContext.getTypeBuilder().numberType());
            return outputPayloadTypeBuilder.build();
        }
        return metadataContext.getTypeBuilder()
                .numberType()
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
        return "Redis ZRANK Reply";
    }
}
