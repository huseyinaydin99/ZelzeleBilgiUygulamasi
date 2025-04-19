package tr.com.huseyinaydin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tr.com.huseyinaydin.models.DropboxModel;

public class DropboxRepository {

    private FileDatabaseHelper dbHelper;

    public DropboxRepository(Context context) {
        dbHelper = new FileDatabaseHelper(context);
    }

    // 📥 Yeni Dropbox hesabı ekle
    public void insertDropboxAccount(String appName, String accessToken) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FileDatabaseHelper.COLUMN_APP_NAME, appName);
        values.put(FileDatabaseHelper.COLUMN_ACCESS_TOKEN, accessToken);
        db.insert(FileDatabaseHelper.TABLE_DROPBOX, null, values);
        db.close();
    }

    // 📤 Tüm Dropbox hesaplarını getir
    public List<DropboxModel> getAllDropboxAccounts() {
        List<DropboxModel> dropboxAccounts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(FileDatabaseHelper.TABLE_DROPBOX, null,
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_ID));
                String appName = cursor.getString(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_APP_NAME));
                String token = cursor.getString(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_ACCESS_TOKEN));
                dropboxAccounts.add(new DropboxModel(id, appName, token));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return dropboxAccounts;
    }

    // 🔍 Belirli bir hesabı ID ile getir
    public DropboxModel getDropboxAccountById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DropboxModel model = null;

        Cursor cursor = db.query(
                FileDatabaseHelper.TABLE_DROPBOX,
                null,
                FileDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String appName = cursor.getString(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_APP_NAME));
            String token = cursor.getString(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_ACCESS_TOKEN));
            model = new DropboxModel(id, appName, token);
        }

        cursor.close();
        db.close();
        return model;
    }

    // 🔄 Dropbox hesabı güncelle
    public int updateDropboxAccount(int id, String newAppName, String newAccessToken) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FileDatabaseHelper.COLUMN_APP_NAME, newAppName);
        values.put(FileDatabaseHelper.COLUMN_ACCESS_TOKEN, newAccessToken);

        int rowsAffected = db.update(
                FileDatabaseHelper.TABLE_DROPBOX,
                values,
                FileDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return rowsAffected;
    }

    // ❌ Dropbox hesabı sil
    public int deleteDropboxAccount(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(
                FileDatabaseHelper.TABLE_DROPBOX,
                FileDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return rowsDeleted;
    }
}