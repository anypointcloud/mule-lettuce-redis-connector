package cloud.anypoint.redis.api.geospatial;

import io.lettuce.core.GeoArgs;

public enum DistanceUnit {
    M,
    KM,
    FT,
    MI;

    public GeoArgs.Unit getLettuceUnit() {
        switch (this) {
            case M:
                return GeoArgs.Unit.m;
            case KM:
                return GeoArgs.Unit.km;
            case FT:
                return GeoArgs.Unit.ft;
            case MI:
                return GeoArgs.Unit.mi;
            default:
                return GeoArgs.Unit.m;
        }
    }
}
