package tr.com.huseyinaydin.models;

public class DropboxModel {
    private int id;
    private String appName;
    private String accessToken;

    public DropboxModel(int id, String appName, String accessToken) {
        this.id = id;
        this.appName = appName;
        this.accessToken = accessToken;
    }

    public int getId() {
        return id;
    }

    public String getAppName() {
        return appName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}