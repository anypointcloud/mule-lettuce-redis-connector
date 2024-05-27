package cloud.anypoint.redis.api.geospatial;

import org.mule.runtime.extension.api.annotation.Alias;

@Alias("fromMember")
public class GeoSearchFromMember implements GeoSearchCenter {
    private String member;

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}
