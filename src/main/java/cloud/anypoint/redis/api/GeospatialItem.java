package cloud.anypoint.redis.api;

public class GeospatialItem {
    private String name;
    private GeoLocation location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

}
