package tr.com.huseyinaydin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tr.com.huseyinaydin.models.FileModel;

public class FileRepository {

    private FileDatabaseHelper dbHelper;

    public FileRepository(Context context) {
        dbHelper = new FileDatabaseHelper(context);
    }

    public void insertFilePath(String filePath) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FileDatabaseHelper.COLUMN_PATH, filePath);
        db.insert(FileDatabaseHelper.TABLE_FILES, null, values);
        db.close();
    }

    public List<FileModel> getAllFiles() {
        List<FileModel> fileList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(FileDatabaseHelper.TABLE_FILES, null,
                null, null, null, null,
                FileDatabaseHelper.COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_PATH));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_TIMESTAMP));
                fileList.add(new FileModel(id, path, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return fileList;
    }
}