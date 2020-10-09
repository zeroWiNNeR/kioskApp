package com.adamanta.kioskapp.product.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adamanta.kioskapp.product.model.CategoryAndProduct;
import com.adamanta.kioskapp.product.model.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductsDBHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "PRODUCTS";
    private static final String COL1 = "id";
    private static final String COL2 = "TYPE";
    private static final String COL3 = "PARENT_CATEGORY";
    private static final String COL4 = "POSITION";
    private static final String COL5 = "IS_ENABLE";
    private static final String COL6 = "NAME";
    private static final String COL7 = "FULL_NAME";
    private static final String COL8 = "ARTICLE";
    private static final String COL9 = "BARCODE";
    private static final String COL10 = "WEIGHT";
    private static final String COL11 = "MIN_SIZE";
    private static final String COL12 = "SIZE_STEP";
    private static final String COL13 = "PRICE_PER_SIZE_STEP";
    private static final String COL14 = "WEIGHT_PER_SIZE_STEP";
    private static final String COL15 = "MAX_SIZE";
    private static final String COL16 = "SIZE_TYPE";
    private static final String COL17 = "STOCK_QUANTITY";
    private static final String COL18 = "MANUFACTURER";
    private static final String COL19 = "DESCRIPTION";
    private static final String COL20 = "COMPOSITION";
    private static final String COL21 = "PREV_COMPOSITION";
    private static final String COL22 = "PREV_COMPOSITION_DATE";
    private static final String COL23 = "PREV_PREV_COMPOSITION";
    private static final String COL24 = "PREV_PREV_COMPOSITION_DATE";
    private static final String COL25 = "INFORMATION";
    private static final String COL26 = "IMAGES_INFO";

    public ProductsDBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT," +
                COL3 + " INTEGER," +
                COL4 + " INTEGER," +
                COL5 + " INTEGER," +
                COL6 + " TEXT," +
                COL7 + " TEXT," +
                COL8 + " INTEGER," +
                COL9 + " INTEGER," +
                COL10 + " TEXT," +
                COL11 + " TEXT," +
                COL12 + " TEXT," +
                COL13 + " TEXT," +
                COL14 + " TEXT," +
                COL15 + " TEXT," +
                COL16 + " TEXT," +
                COL17 + " TEXT," +
                COL18 + " TEXT," +
                COL19 + " TEXT," +
                COL20 + " TEXT," +
                COL21 + " TEXT," +
                COL22 + " TEXT," +
                COL23 + " TEXT," +
                COL24 + " TEXT," +
                COL25 + " TEXT," +
                COL26 + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*
     * Сохраняет продукт в БД
     * @param product - сохраняемый продукт
     */
    public boolean saveProduct(Product product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, String.valueOf(product.getType()));
        contentValues.put(COL3, product.getParentCategory());
        contentValues.put(COL4, product.getPosition());
        contentValues.put(COL5, product.getIsEnable() ? 1 : 0 );
        contentValues.put(COL6, product.getName());
        contentValues.put(COL7, product.getFullName());
        contentValues.put(COL8, product.getArticle());
        contentValues.put(COL9, product.getBarcode());
        contentValues.put(COL10, product.getWeight());
        contentValues.put(COL11, product.getMinSize().toString());
        contentValues.put(COL12, product.getSizeStep().toString());
        contentValues.put(COL13, product.getPricePerSizeStep().toString());
        contentValues.put(COL14, product.getPricePerSizeStep().toString());
        contentValues.put(COL15, product.getMaxSize().toString());
        contentValues.put(COL16, product.getSizeType());
        contentValues.put(COL17, product.getStockQuantity().toString());
        contentValues.put(COL18, product.getManufacturer());
        contentValues.put(COL19, product.getDescription());
        contentValues.put(COL20, product.getComposition());
        contentValues.put(COL21, product.getPrevComposition());
        contentValues.put(COL22, product.getPrevCompositionDate());
        contentValues.put(COL23, product.getPrevPrevComposition());
        contentValues.put(COL24, product.getPrevPrevCompositionDate());
        contentValues.put(COL25, product.getInformation());
        contentValues.put(COL26, product.getImagesInfo());

        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null)
            return false;

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    /*
     * Получаем продукт из БД по артикулу
     * @param article - артикул получаемого продукта
     */
    public Product getByArticle(Long article) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL8 + " = '" + article + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor==null || cursor.getCount()==0) {
            if (cursor != null)
                cursor.close();
            db.close();
            return null;
        }

        cursor.moveToFirst();
        Product product = new Product();
        product.setId(cursor.getLong(0));
        product.setType(cursor.getString(1).charAt(0));
        product.setParentCategory(cursor.getLong(2));
        product.setPosition(cursor.getInt(3));
        product.setIsEnable( cursor.getInt(4) == 1 );
        product.setName(cursor.getString(5));
        product.setFullName(cursor.getString(6));
        product.setArticle(cursor.getLong(7));
        product.setBarcode(cursor.getLong(8));
        product.setWeight(cursor.getString(9));
        product.setMinSize(new BigDecimal(cursor.getString(10)));
        product.setSizeStep(new BigDecimal(cursor.getString(11)));
        product.setPricePerSizeStep(new BigDecimal(cursor.getString(12)));
        product.setWeightPerSizeStep(new BigDecimal(cursor.getString(13)));
        product.setMaxSize(new BigDecimal(cursor.getString(14)));
        product.setSizeType(cursor.getString(15));
        product.setStockQuantity(new BigDecimal(cursor.getString(16)));
        product.setManufacturer(cursor.getString(17));
        product.setDescription(cursor.getString(18));
        product.setComposition(cursor.getString(19));
        product.setPrevComposition(cursor.getString(20));
        product.setPrevCompositionDate(cursor.getString(21));
        product.setPrevPrevComposition(cursor.getString(22));
        product.setPrevPrevCompositionDate(cursor.getString(23));
        product.setInformation(cursor.getString(24));
        product.setImagesInfo(cursor.getString(25));
        cursor.close();
        db.close();
        return product;
    }

    public List<CategoryAndProduct> getListCategoryAndProductByParentCategory(long parentCategory) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL3 + " = '" + parentCategory + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor==null || cursor.getCount()==0) {
            if (cursor != null)
                cursor.close();
            db.close();
            return new ArrayList<>();
        }

//        cursor.moveToFirst();
        List<CategoryAndProduct> products = new ArrayList<>();
        while (cursor.moveToNext()) {
            CategoryAndProduct product = new CategoryAndProduct();
            product.setId(cursor.getLong(0));
            product.setType(cursor.getString(1).charAt(0));
            product.setParentCategory(cursor.getLong(2));
            product.setPosition(cursor.getInt(3));
            product.setIsEnable( cursor.getInt(4) == 1 );
            product.setName(cursor.getString(5));
            product.setFullName(cursor.getString(6));
            product.setArticle(cursor.getLong(7));
            product.setBarcode(cursor.getLong(8));
            product.setWeight(cursor.getString(9));
            product.setMinSize(new BigDecimal(cursor.getString(10)));
            product.setSizeStep(new BigDecimal(cursor.getString(11)));
            product.setPricePerSizeStep(new BigDecimal(cursor.getString(12)));
            product.setWeightPerSizeStep(new BigDecimal(cursor.getString(13)));
            product.setMaxSize(new BigDecimal(cursor.getString(14)));
            product.setSizeType(cursor.getString(15));
            product.setStockQuantity(new BigDecimal(cursor.getString(16)));
            product.setManufacturer(cursor.getString(17));
            product.setDescription(cursor.getString(18));
            product.setComposition(cursor.getString(19));
            product.setPrevComposition(cursor.getString(20));
            product.setPrevCompositionDate(cursor.getString(21));
            product.setPrevPrevComposition(cursor.getString(22));
            product.setPrevPrevCompositionDate(cursor.getString(23));
            product.setInformation(cursor.getString(24));
            product.setImagesInfo(cursor.getString(25));
            products.add(product);
        }

        cursor.close();
        db.close();
        return products;
    }

    /*
     * Обновляем продукт в БД по артикулу
     * @param product - вносимая запись продукта
     */
    public boolean updateProduct(Product product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, String.valueOf(product.getType()));
        contentValues.put(COL3, product.getParentCategory());
        contentValues.put(COL4, product.getPosition());
        contentValues.put(COL5, product.getIsEnable() ? 1 : 0 );
        contentValues.put(COL6, product.getName());
        contentValues.put(COL7, product.getFullName());
        contentValues.put(COL8, product.getArticle());
        contentValues.put(COL9, product.getBarcode());
        contentValues.put(COL10, product.getWeight());
        contentValues.put(COL11, product.getMinSize().toString());
        contentValues.put(COL12, product.getSizeStep().toString());
        contentValues.put(COL13, product.getPricePerSizeStep().toString());
        contentValues.put(COL14, product.getPricePerSizeStep().toString());
        contentValues.put(COL15, product.getMaxSize().toString());
        contentValues.put(COL16, product.getSizeType());
        contentValues.put(COL17, product.getStockQuantity().toString());
        contentValues.put(COL18, product.getManufacturer());
        contentValues.put(COL19, product.getDescription());
        contentValues.put(COL20, product.getComposition());
        contentValues.put(COL21, product.getPrevComposition());
        contentValues.put(COL22, product.getPrevCompositionDate());
        contentValues.put(COL23, product.getPrevPrevComposition());
        contentValues.put(COL24, product.getPrevPrevCompositionDate());
        contentValues.put(COL25, product.getInformation());
        contentValues.put(COL26, product.getImagesInfo());

        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            return false;
        }
        long result = db.update(
                TABLE_NAME, contentValues,
                "ARTICLE = ?", new String[] {product.getArticle().toString()}
                );
        db.close();
        return result != -1;
    }

    public boolean deleteByArticle(Long article) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(
                TABLE_NAME,
                "ARTICLE = ?", new String[]{article.toString()}
        );
        db.close();
        return result != -1;
    }

}
