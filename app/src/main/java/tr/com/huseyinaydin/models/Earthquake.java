package tr.com.huseyinaydin.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Earthquake implements Serializable {

    private double magnitude;
    private String type;
    private String location;
    private String province;
    private String district;
    private String formattedDate; // ya da LocalDateTime / Date olabilir
    private String depth;
    private String latitude;
    private String longitude;
    private Date date;

    // Constructor
    public Earthquake(double magnitude, String type, String location, String province,
                      String district, String formattedDate, String depth,
                      String latitude, String longitude) throws ParseException {
        this.magnitude = magnitude;
        this.type = type;
        this.location = location;
        this.province = province;
        this.district = district;
        this.formattedDate = formattedDate;
        this.depth = depth;
        this.latitude = latitude;
        this.longitude = longitude;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        Date parsedDate = sdf.parse(formattedDate);
        setDate(parsedDate);
    }

    // Getters and Setters
    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    // Tarih formatlama
    public String getFormattedDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            Date parsedDate = sdf.parse(formattedDate);
            setDate(parsedDate);
            return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return formattedDate;
        }
    }

    public Date getDate() {
        return date;
    }

    public String getStringDate(){
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(getDate());
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Earthquake{" +
                "magnitude=" + magnitude +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", province='" + province + '\'' +
                ", district='" + district + '\'' +
                ", formattedDate='" + formattedDate + '\'' +
                ", depth='" + depth + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}