package tr.com.huseyinaydin.utils;

import java.util.List;

import tr.com.huseyinaydin.models.Earthquake;

public interface EarthquakeExporter {
    void exportToPdf(List<Earthquake> earthquakes, String filePath);
    void exportToHtml(List<Earthquake> earthquakes, String filePath);
    void exportToTxt(List<Earthquake> earthquakes, String filePath);
    void exportToWord(List<Earthquake> earthquakes, String filePath);
}