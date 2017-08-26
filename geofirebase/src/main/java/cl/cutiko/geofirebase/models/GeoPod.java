package cl.cutiko.geofirebase.models;

/**
 * Created by cutiko on 24-08-17.
 */

public class GeoPod {

    private double latitude, longitude;
    private String key;

    public GeoPod() {
    }

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
