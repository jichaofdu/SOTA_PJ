package projectmanager.dada.model;

import java.io.Serializable;

public class Location implements Serializable {

    private double longitude;
    private double latitude;
    private String description;

    public Location(int locationId, double longitude, double latitude, String description) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
    }

    public Location() {}

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
