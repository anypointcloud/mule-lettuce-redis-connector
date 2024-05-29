package cloud.anypoint.redis.internal.metadata;

import cloud.anypoint.redis.api.geospatial.GeoSearchResultOption;
import org.mule.metadata.api.builder.ArrayTypeBuilder;
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

public class GeoSearchOutputTypeResolver implements TypeKeysResolver, OutputTypeResolver<GeoSearchResultOption> {
    @Override
    public MetadataType getOutputType(MetadataContext metadataContext, GeoSearchResultOption option) throws MetadataResolvingException, ConnectionException {
        if (option.isWithCoord() || option.isWithDist() || option.isWithHash()) {
            ObjectTypeBuilder itemType = metadataContext.getTypeBuilder().objectType();
            itemType.addField().key("member").value(metadataContext.getTypeBuilder().stringType());
            if (option.isWithCoord()) {
                itemType.addField().key("latitude").value(metadataContext.getTypeBuilder().numberType());
                itemType.addField().key("longitude").value(metadataContext.getTypeBuilder().numberType());
            }
            if (option.isWithDist()) {
                itemType.addField().key("distance").value(metadataContext.getTypeBuilder().numberType());
            }
            if (option.isWithHash()) {
                itemType.addField().key("hash").value(metadataContext.getTypeBuilder().numberType());
            }
            return itemType.build();
        } else {
            return metadataContext.getTypeBuilder().stringType().build();
        }
    }

    @Override
    public Set<MetadataKey> getKeys(MetadataContext metadataContext) throws MetadataResolvingException, ConnectionException {
        return Collections.emptySet();
    }

    @Override
    public String getResolverName() {
        return "GEOSEARCH Result resolver";
    }

    @Override
    public String getCategoryName() {
        return "Redis Result";
    }
}
