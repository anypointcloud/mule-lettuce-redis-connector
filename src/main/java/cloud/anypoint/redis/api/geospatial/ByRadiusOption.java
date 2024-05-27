package cloud.anypoint.redis.api.geospatial;

public class ByRadiusOption {
    private double radius;
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
}
