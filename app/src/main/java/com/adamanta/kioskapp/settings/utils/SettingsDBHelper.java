package com.adamanta.kioskapp.settings.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SettingsDBHelper extends SQLiteOpenHelper {

    private final String TAG = this.getClass().getSimpleName();

    private static final String TABLE_NAME = "settings";
    private static final String COL1 = "id";
    private static final String COL2 = "argument";
    private static final String COL3 = "value";

    public SettingsDBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " ARGUMENT," +
                COL3 + " VALUE)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String argument, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, argument);
        contentValues.put(COL3, value);

        Log.d(TAG, "addValue: Adding " + argument + ": " + value + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns value by argument from db
     * @return data
     */
    public Cursor getValue(String argument) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL3 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + argument + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Returns value by argument from db
     * @return long value - значение lastAppliedChangeId из БД
     */
    public long getLastAppliedChangeId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL3 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = 'lastAppliedChangeId'";
        Cursor data = db.rawQuery(query, null);
        if (data==null || data.getCount()==0) {
            return 0L;
        }
        data.moveToFirst();
        long value = data.getLong(0);
        data.close();
        return value;
    }

    /**
     * Returns value by argument from db
     * @return String value
     */
    public String getStringValueByArgument(String argument) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL3 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + argument + "'";
        Cursor data = db.rawQuery(query, null);
        //этот вариант также работает, нативный удобнее
//        Cursor data = db.query(
//                TABLE_NAME,
//                new String[] {"argument", "value" },
//                "argument = ?", new String[] {"lastAppliedChangeID"},
//                null, null, null
//                );
        if (data==null || data.getCount()==0) {
            if (argument.equals("lastAppliedChangeId")){
                return "0";
            } else {
                return "null";
            }
        }
        data.moveToFirst();
        String value = data.getString(0);
        data.close();
        return value;
    }

    /**
     * Returns all the data from database
     * @param argument - меняемый параметр
     * @param value - новое значение параметра
     * @return result of setValue operation
     */
    public void setValue(String argument, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME +
                " SET " + COL3 + " = '" + value + "'" +
                " WHERE " + COL2 + " = '" + argument + "'";
        db.execSQL(query);
    }

    /**
     * Returns all the data from database
     * @return data
     */
    public Cursor getRowsFromCOL3() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL3 + " FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Returns only the ID that matches the name passed in
     * @param argument - название аргумента
     */
    public Cursor getID(String argument) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + argument + "'";
        return db.rawQuery(query, null);
    }


    /**
     * Updates the Value field
     * @param id - id изменяемой строки в БД
     * @param argument - изменяемый аргумент
     * @param newValue - новое значение аргумента
     */
    public void updateRowField(int id, String argument, String newValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME +
                " SET " + COL3 + " = '" + newValue +
                "' WHERE " + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + argument + "'";
        Log.d(TAG, "updateText: query: " + query);
        Log.d(TAG, "updateText: Setting text to " + argument + ": " + newValue);
        db.execSQL(query);
    }

    /**
     * Delete from database
     * @param argument - название удаляемого аргумента
     */
    public void deleteName(String argument) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + argument + "'";
        Log.d(TAG, "deleteText: query: " + query);
        Log.d(TAG, "deleteText: Deleting " + argument + "from database.");
        db.execSQL(query);
    }
}
