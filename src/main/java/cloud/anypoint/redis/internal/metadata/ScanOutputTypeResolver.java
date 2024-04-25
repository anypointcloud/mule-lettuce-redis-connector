package cloud.anypoint.redis.internal.metadata;

import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

public class ScanOutputTypeResolver implements OutputTypeResolver<String> {
    @Override
    public MetadataType getOutputType(MetadataContext metadataContext, String s) throws MetadataResolvingException, ConnectionException {
        return metadataContext.getTypeBuilder().arrayType().of(
                metadataContext.getTypeBuilder().stringType()
        ).build();
    }

    @Override
    public String getCategoryName() {
        return "Keys";
    }
}
