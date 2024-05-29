package cloud.anypoint.redis.api.geospatial;

import io.lettuce.core.GeoSearch;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

public class GeoSearchByBox implements GeoSearchBy {
    @Placement(order = 1)
    @Parameter
    private double width;
    @Placement(order = 2)
    @Parameter
    private double height;

    @Placement(order = 3)
    @Parameter
    private DistanceUnit unit;

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public DistanceUnit getUnit() {
        return unit;
    }

    public void setUnit(DistanceUnit unit) {
        this.unit = unit;
    }

    @Override
    public GeoSearch.GeoPredicate getPredicate() {
        return GeoSearch.byBox(getWidth(), getHeight(), getUnit().getLettuceUnit());
    }
}
