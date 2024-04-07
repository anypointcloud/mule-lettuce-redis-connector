package cloud.anypoint.redis.internal;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

@Xml(prefix = "realredis")
@org.mule.runtime.extension.api.annotation.Extension(name = "RealRedis Configuration", vendor = "Anypoint Cloud")
@Configurations(RealRedisConfiguration.class)
public class RealRedisExtension {

}
