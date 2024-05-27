package cloud.anypoint.redis.api.geospatial;

import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

public class GeoSearchResultOption {
    @DisplayName("WITHCOORD")
    @Parameter
    private boolean withCoord;
    @DisplayName("WITHDIST")
    @Parameter
    private boolean withDist;
    @DisplayName("WITHHASH")
    @Parameter
    private boolean withHash;

    public boolean isWithCoord() {
        return withCoord;
    }

    public void setWithCoord(boolean withCoord) {
        this.withCoord = withCoord;
    }

    public boolean isWithDist() {
        return withDist;
    }

    public void setWithDist(boolean withDist) {
        this.withDist = withDist;
    }

    public boolean isWithHash() {
        return withHash;
    }

    public void setWithHash(boolean withHash) {
        this.withHash = withHash;
    }
}
