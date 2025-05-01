package tr.com.huseyinaydin.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "earthquakes")
public class Earthquake {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String description;
    public String time;

    public Earthquake(String title, String description, String time) {
        this.title = title;
        this.description = description;
        this.time = time;
    }
}