package tr.com.huseyinaydin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FileDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "files.db";
    private static final int DATABASE_VERSION = 2; // Versiyon güncellendi

    // Tablo ve sütun isimleri - Dosyalar tablosu
    public static final String TABLE_FILES = "files";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PATH = "file_path";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Dropbox tablosu
    public static final String TABLE_DROPBOX = "dropbox";
    public static final String COLUMN_APP_NAME = "appname";
    public static final String COLUMN_ACCESS_TOKEN = "access_token";

    private static final String CREATE_TABLE_FILES = "CREATE TABLE " + TABLE_FILES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PATH + " TEXT NOT NULL, " +
            COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ");";

    private static final String CREATE_TABLE_DROPBOX = "CREATE TABLE " + TABLE_DROPBOX + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_APP_NAME + " TEXT NOT NULL, " +
            COLUMN_ACCESS_TOKEN + " TEXT NOT NULL" +
            ");";

    public FileDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FILES);
        db.execSQL(CREATE_TABLE_DROPBOX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Tabloları silip yeniden oluştur
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DROPBOX);
        onCreate(db);
    }
}