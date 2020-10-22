package com.adamanta.kioskapp.settings.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SettingsDBHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "SETTINGS";
    private static final String COL1 = "_id";
    private static final String COL2 = "ARGUMENT";
    private static final String COL3 = "VALUE";

    public SettingsDBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT," +
                COL3 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String argument, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, argument);
        contentValues.put(COL3, value);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Returns value by argument from db
     * @return long value - значение lastAppliedChangeId из БД
     */
    public long getLastAppliedChangeId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL3 + " FROM " + TABLE_NAME + " WHERE " + COL2 + " = 'lastAppliedChangeId'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor==null || cursor.getCount()==0) {
            if (cursor != null)
                cursor.close();
            db.close();
            return 0L;
        }
        cursor.moveToFirst();
        long value = cursor.getLong(0);
        cursor.close();
        db.close();
        return value;
    }

    /**
     * Returns value by argument from db
     * @return String value
     */
    public String getStringValueByArgument(String argument) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String query = "SELECT " + COL3 + " FROM " + TABLE_NAME +
                    " WHERE " + COL2 + " = '" + argument + "'";
            Cursor cursor = db.rawQuery(query, null);
//            этот вариант также работает, нативный удобнее
//            Cursor data = db.query(
//                    TABLE_NAME,
//                    new String[] {"argument", "value" },
//                    "argument = ?", new String[] {"lastAppliedChangeID"},
//                    null, null, null
//            );
            if (cursor == null || cursor.getCount() == 0) {
                if (cursor != null)
                    cursor.close();
                if (argument.equals("lastAppliedChangeId")) {
                    return "0";
                } else {
                    return "null";
                }
            }
            cursor.moveToFirst();
            String value = cursor.getString(0);
            cursor.close();
            return value;
        } catch (SQLiteException e) {
            Log.e("SettingsDBHelper", "Ошибка выполнения запроса: Чтение записи по аргументу " + argument);
            return "null";
        }
    }

    /**
     * Returns all the data from database
     * @param argument - меняемый параметр
     * @param value - новое значение параметра
     * @return result of setValue operation
     */
    public void setValue(String argument, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL3 + " = '" + value +
                "' WHERE " + COL2 + " = '" + argument + "'";
        db.execSQL(query);
        db.close();
    }

    /**
     * Returns all the data from database
     * @return data
     */
    public String[] getAllValues() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL3 + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor==null || cursor.getCount()==0) {
            if (cursor != null)
                cursor.close();
            db.close();
            return null;
        }
        String[] values = new String[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;
        while (cursor.moveToNext()) {
            values[i] = cursor.getString(0);
            i++;
        }
        cursor.close();
        db.close();
        return values;
    }

    /**
     * Updates the Value field
     * @param id - id изменяемой строки в БД
     * @param argument - изменяемый аргумент
     * @param newValue - новое значение аргумента
     */
    public void updateRowField(int id, String argument, String newValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL3 + " = '" + newValue +
                "' WHERE " + COL1 + " = '" + id + "' AND " + COL2 + " = '" + argument + "'";
        db.execSQL(query);
        db.close();
    }

    /**
     * Delete from database
     * @param argument - название удаляемого аргумента
     */
    public void deleteName(String argument) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL2 + " = '" + argument + "'";
        db.execSQL(query);
        db.close();
    }
}
