package cloud.anypoint.redis.api.geospatial;

import io.lettuce.core.GeoSearch;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

public class GeoSearchByRadius implements GeoSearchBy {
    @Placement(order = 1)
    @Parameter
    private double radius;
    @Placement(order = 2)
    @Parameter
    private DistanceUnit unit;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public DistanceUnit getUnit() {
        return unit;
    }

    public void setUnit(DistanceUnit unit) {
        this.unit = unit;
    }

    @Override
    public GeoSearch.GeoPredicate getPredicate() {
        return GeoSearch.byRadius(getRadius(), getUnit().getLettuceUnit());
    }
}
