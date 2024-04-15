package cloud.anypoint.redis.internal;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;

import cloud.anypoint.redis.api.ConnectorError;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;

@Xml(prefix = "lettuce")
@Extension(name = "Lettuce Redis", vendor = "Anypoint Cloud")
@Configurations(RedisConfiguration.class)
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
@ErrorTypes(ConnectorError.class)
public class LettuceRedisExtension {

}
