package tr.com.huseyinaydin.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Earthquake {
    private double magnitude;
    private String location;
    private String date;
    private double depth;

    // Constructor
    public Earthquake(double magnitude, String location, String date, double depth) {
        this.magnitude = magnitude;
        this.location = location;
        this.date = date;
        this.depth = depth;
    }

    // Getter methods
    public double getMagnitude() { return magnitude; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public double getDepth() { return depth; }

    // Tarih formatlama
    public String getFormattedDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date parsedDate = sdf.parse(date);
            return new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(parsedDate);
        } catch (ParseException e) {
            return date;
        }
    }
}