package tr.com.huseyinaydin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FileDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "files.db";
    private static final int DATABASE_VERSION = 1;

    // Tablo ve sütun isimleri
    public static final String TABLE_FILES = "files";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PATH = "file_path";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_FILES = "CREATE TABLE " + TABLE_FILES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PATH + " TEXT NOT NULL, " +
            COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ");";

    public FileDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FILES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Gelecek sürümler için tabloyu yenileme işlemi
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
        onCreate(db);
    }
}