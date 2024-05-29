package cloud.anypoint.redis.api.geospatial;

import io.lettuce.core.GeoSearch;

public interface GeoSearchCenter {
    GeoSearch.GeoRef<String> reference();
}
