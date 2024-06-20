package cloud.anypoint.redis.internal.metadata;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.builder.ObjectTypeBuilder;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.MetadataResolvingException;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

import java.util.List;

public class XinfoOutputTypeResolver implements OutputTypeResolver {
    @Override
    public MetadataType getOutputType(MetadataContext metadataContext, Object key) throws MetadataResolvingException, ConnectionException {
        BaseTypeBuilder b = metadataContext.getTypeBuilder();
        ObjectTypeBuilder itemType = b.objectType();
        itemType.addField().key("name").value(b.stringType()).required();
        itemType.addField().key("consumers").value(b.numberType()).required();
        itemType.addField().key("pending").value(b.numberType()).required();
        itemType.addField().key("last-delivered-id").value(b.stringType()).required();
        itemType.addField().key("entries-read").value(b.numberType());
        itemType.addField().key("lag").value(b.numberType());
        return itemType.build();
    }

    @Override
    public String getCategoryName() {
        return "redis XINFO reply";
    }
}
