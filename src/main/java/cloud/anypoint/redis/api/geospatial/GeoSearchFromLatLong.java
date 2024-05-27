package cloud.anypoint.redis.api.geospatial;

import org.mule.runtime.extension.api.annotation.Alias;

@Alias("fromLatLong")
public class GeoSearchFromLatLong implements GeoSearchCenter {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
