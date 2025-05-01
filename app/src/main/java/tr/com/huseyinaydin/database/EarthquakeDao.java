package tr.com.huseyinaydin.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface EarthquakeDao {
    @Insert
    void insert(Earthquake earthquake);

    @Query("SELECT COUNT(*) FROM earthquakes WHERE title = :title")
    int countByTitle(String title);
}