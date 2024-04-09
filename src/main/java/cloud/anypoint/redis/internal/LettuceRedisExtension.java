package cloud.anypoint.redis.internal;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

@Xml(prefix = "lettuce")
@Extension(name = "Lettuce Redis Configuration", vendor = "Anypoint Cloud")
@Configurations(RedisConfiguration.class)
public class LettuceRedisExtension {

}
