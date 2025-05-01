package tr.com.huseyinaydin.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Earthquake.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EarthquakeDao earthquakeDao();
}