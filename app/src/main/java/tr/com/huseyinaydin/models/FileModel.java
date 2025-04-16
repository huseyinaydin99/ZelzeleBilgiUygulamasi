package tr.com.huseyinaydin.models;

public class FileModel {
    private int id;
    private String filePath;
    private String timestamp;

    public FileModel(int id, String filePath, String timestamp) {
        this.id = id;
        this.filePath = filePath;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getFilePath() { return filePath; }
    public String getTimestamp() { return timestamp; }
}