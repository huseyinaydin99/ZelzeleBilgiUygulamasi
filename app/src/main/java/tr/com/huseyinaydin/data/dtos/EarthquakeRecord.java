package tr.com.huseyinaydin.data.dtos;

public class EarthquakeRecord {
    private String date;
    private String location;
    private double magnitude;
    private double latitude;
    private double longitude;
    private double depth; // 🆕 Derinlik eklendi

    public EarthquakeRecord(String date, String location, double magnitude, double latitude, double longitude, double depth) {
        this.date = date;
        this.location = location;
        this.magnitude = magnitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth = depth;
    }

    public EarthquakeRecord() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
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

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }
}