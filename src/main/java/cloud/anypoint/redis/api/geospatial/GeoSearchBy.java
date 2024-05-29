package cloud.anypoint.redis.api.geospatial;

import io.lettuce.core.GeoSearch;

public interface GeoSearchBy {
    GeoSearch.GeoPredicate getPredicate();
}
