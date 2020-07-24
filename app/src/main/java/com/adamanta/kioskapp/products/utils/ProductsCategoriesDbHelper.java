package com.adamanta.kioskapp.products.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProductsCategoriesDbHelper extends SQLiteOpenHelper {
    private final String TAG = this.getClass().getSimpleName();

    private static final String TABLE_NAME = "products_categories";
    private static final String COL1 = "id";
    private static final String COL2 = "parent_level";
    private static final String COL3 = "child_level";
    private static final String COL4 = "button_text";
    private static final String COL5 = "button_color";
    private static final String COL6 = "text_color";

    public ProductsCategoriesDbHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " PARENT_LEVEL," +
                COL3 + " NEXT_TYPE," +
                COL4 + " BUTTON_TEXT," +
                COL5 + " BUTTON_COLOR," +
                COL6 + " TEXT_COLOR)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String parentLevel, String nextType, String buttonText,
                           String buttonColor, String textColor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, parentLevel);
        contentValues.put(COL3, nextType);
        contentValues.put(COL4, buttonText);
        contentValues.put(COL5, buttonColor);
        contentValues.put(COL6, textColor);

        Log.d(TAG, "addData: Addcategory " + parentLevel + " " + nextType +
                " " + buttonText + " " + buttonColor + " " + textColor);
        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Returns all the data from database
     * @return
     */
    public Cursor getWithParent(String parentLevel){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL3 + " AND " + COL4 + " AND " + COL5 + " AND " + COL6 +
                " FROM " + TABLE_NAME + " WHERE " + COL2 + " = '" + parentLevel + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /*
     * Returns all the data from database
     * @return
     */
    public Cursor getRowsFromCOL3(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL3 + " FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /*
     * Returns only the ID that matches the name passed in
     * @param argument
     * @return
     */
    public Cursor getID(String argument){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + argument + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    /*
     * Updates the name field
     * @param newValue
     * @param id
     * @param oldValue
     */
    public void updateText(int id, String argument, String oldValue, String newValue){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME +
                " SET " + COL3 + " = '" + newValue +
                "' WHERE " + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + argument + "'" +
                " AND " + COL3 + " = '" + oldValue + "'";
        Log.d(TAG, "updateText: query: " + query);
        Log.d(TAG, "updateText: Setting text to " + argument + ": " + newValue);
        db.execSQL(query);
    }

    /*
     * Delete from database
     * @param argument
     */
    public void deleteName(String argument){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + argument + "'";
        Log.d(TAG, "deleteText: query: " + query);
        Log.d(TAG, "deleteText: Deleting " + argument + "from database.");
        db.execSQL(query);
    }
}
