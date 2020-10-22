package com.adamanta.kioskapp.favorites.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FavoritesDBHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "FAVORITES";
    private static final String COL1 = "_id";
    private static final String COL2 = "ARTICLE";

    public FavoritesDBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*
     * Добавляет продукт в БД избранного
     * @param article - артикул сохраняемого продукта
     */
    public boolean saveProduct(long article) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, article);

        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null)
            return false;

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    /*
     * Ищет продукт в БД избранного по артикулу
     * @param article - артикул искомого продукта
     */
    public boolean findByArticle(long article) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                    " WHERE " + COL2 + " = '" + article + "'";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor == null || cursor.getCount() == 0) {
                if (cursor != null)
                    cursor.close();
                return false;
            }
            cursor.moveToFirst();
            String value = cursor.getString(0);
            cursor.close();
            return value.length() > 0;
        } catch (SQLiteException e) {
            Log.e("SettingsDBHelper", "Ошибка выполнения запроса: Чтение записи по артикулу " + article);
            return false;
        }
    }

    /**
     * Returns all the data from database
     * @return data
     */
    public long[] getAllValues() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor==null || cursor.getCount()==0) {
            if (cursor != null)
                cursor.close();
            db.close();
            return new long[0];
        }
        long[] values = new long[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        while (!cursor.isAfterLast()) {
            values[i] = cursor.getLong(0);
            i++;
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return values;
    }

    public boolean deleteByArticle(long article) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(
                TABLE_NAME,
                "ARTICLE = ?", new String[] { String.valueOf(article) }
        );
        db.close();
        return result != -1;
    }



}
