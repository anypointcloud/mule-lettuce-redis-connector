package cloud.anypoint.redis.internal.metadata;

import cloud.anypoint.redis.api.stream.PendingDetailsArgs;
import org.mule.metadata.api.builder.ObjectTypeBuilder;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.api.model.ObjectType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataKey;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;
import org.mule.runtime.api.metadata.resolving.TypeKeysResolver;

import java.util.Collections;
import java.util.Set;

public class XPendingOutputTypeResolver implements TypeKeysResolver, OutputTypeResolver<PendingDetailsArgs> {
    @Override
    public MetadataType getOutputType(MetadataContext metadataContext, PendingDetailsArgs pendingDetailsArgs) throws MetadataResolvingException, ConnectionException {
        if (null == pendingDetailsArgs) {
            // object with keys representing consumers and values representing message counts
            return metadataContext.getTypeBuilder().objectType().build();
        }
        ObjectTypeBuilder pendingEntryType = metadataContext.getTypeBuilder().objectType();
        pendingEntryType.addField().key("id").value(metadataContext.getTypeBuilder().stringType());
        pendingEntryType.addField().key("consumer").value(metadataContext.getTypeBuilder().stringType());
        pendingEntryType.addField().key("elapsed").value(metadataContext.getTypeBuilder().numberType());
        pendingEntryType.addField().key("deliveries").value(metadataContext.getTypeBuilder().numberType());
        return metadataContext.getTypeBuilder()
                .arrayType().of(pendingEntryType.build())
                .build();
    }

    @Override
    public Set<MetadataKey> getKeys(MetadataContext metadataContext) throws MetadataResolvingException, ConnectionException {
        return Collections.emptySet();
    }

    @Override
    public String getResolverName() {
        return TypeKeysResolver.super.getResolverName();
    }

    @Override
    public String getCategoryName() {
        return "Redis XPENDING Reply";
    }
}
