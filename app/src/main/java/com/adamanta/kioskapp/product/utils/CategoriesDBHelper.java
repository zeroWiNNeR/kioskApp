package com.adamanta.kioskapp.product.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adamanta.kioskapp.product.model.Category;

public class CategoriesDBHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "CATEGORIES";
    private static final String COL1 = "id";
    private static final String COL2 = "TYPE";
    private static final String COL3 = "CATEGORY";
    private static final String COL4 = "PARENT_CATEGORY";
    private static final String COL5 = "POSITION";
    private static final String COL6 = "IS_ENABLE";
    private static final String COL7 = "NAME";

    public CategoriesDBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT TYPE," +
                COL3 + " INTEGER CATEGORY," +
                COL4 + " INTEGER PARENT_CATEGORY," +
                COL5 + " INTEGER POSITION," +
                COL6 + " INTEGER IS_ENABLE," +
                COL7 + " TEXT NAME)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean saveCategory(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, String.valueOf(category.getType()));
        contentValues.put(COL3, category.getCategory());
        contentValues.put(COL4, category.getParentCategory());
        contentValues.put(COL5, category.getPosition());
        contentValues.put(COL6, category.getIsEnable() ? 1 : 0);
        contentValues.put(COL7, category.getName());

        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null)
            return false;

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public Category getByCategory(Long categoryId) {
//        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL2 + " = '" + categoryId + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[] {COL1, COL2, COL3, COL4, COL5, COL6, COL7},
                "CATEGORY = ?",
                new String[] { categoryId.toString() },
                null, null, null);

        if (cursor==null || cursor.getCount()==0) {
            return null;
        }

        cursor.moveToFirst();
        Category category = new Category();
        category.setId(cursor.getLong(0));
        category.setType(cursor.getString(1).charAt(0));
        category.setCategory(cursor.getLong(2));
        category.setParentCategory(cursor.getLong(3));
        category.setPosition(cursor.getInt(4));
        category.setIsEnable( cursor.getInt(5) == 1 );
        category.setName(cursor.getString(6));
        cursor.close();
        db.close();
        return category;
    }

    public boolean updateCategory(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, String.valueOf(category.getType()));
        contentValues.put(COL3, category.getCategory());
        contentValues.put(COL4, category.getParentCategory());
        contentValues.put(COL5, category.getPosition());
        contentValues.put(COL6, category.getIsEnable() ? 1 : 0);
        contentValues.put(COL7, category.getName());

        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null)
            return false;

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean deleteByCategory(Long category) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(
                TABLE_NAME,
                "CATEGORY = ?", new String[]{category.toString()}
        );
        db.close();
        return result != -1;
    }
}
