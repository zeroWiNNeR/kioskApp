package com.adamanta.kioskapp.shopcart.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adamanta.kioskapp.product.model.CategoryAndProduct;
import com.adamanta.kioskapp.shopcart.model.ShopCartProduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShopCartDBHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "SHOP_CART";
    private static final String COL1 = "id_";
    private static final String COL2 = "PARENT_CATEGORY";
    private static final String COL3 = "POSITION";
    private static final String COL4 = "IS_ENABLE";
    private static final String COL5 = "NAME";
    private static final String COL6 = "FULL_NAME";
    private static final String COL7 = "ARTICLE";
    private static final String COL8 = "BARCODE";
    private static final String COL9 = "WEIGHT";
    private static final String COL10 = "MIN_SIZE";
    private static final String COL11 = "SIZE_STEP";
    private static final String COL12 = "PRICE_PER_SIZE_STEP";
    private static final String COL13 = "WEIGHT_PER_SIZE_STEP";
    private static final String COL14 = "MAX_SIZE";
    private static final String COL15 = "ALL_COUNT";
    private static final String COL16 = "SIZE_TYPE";

    public ShopCartDBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                " (id_ INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " INTEGER," +
                COL3 + " INTEGER," +
                COL4 + " INTEGER," +
                COL5 + " TEXT," +
                COL6 + " TEXT," +
                COL7 + " INTEGER," +
                COL8 + " INTEGER," +
                COL9 + " TEXT," +
                COL10 + " TEXT," +
                COL11 + " TEXT," +
                COL12 + " TEXT," +
                COL13 + " TEXT," +
                COL14 + " TEXT," +
                COL15 + " TEXT," +
                COL16 + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /*
     * Сохраняет продукт в корзину
     * @param product - сохраняемый продукт
     */
    public boolean addToShopCart(CategoryAndProduct product, BigDecimal additionalCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null)
            return false;

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL7 + " = '" +product.getArticle() + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor==null || cursor.getCount()==0) {
            if (cursor != null)
                cursor.close();

            ContentValues contentValues = new ContentValues();
            contentValues.put(COL2, String.valueOf(product.getParentCategory()));
            contentValues.put(COL3, product.getPosition());
            contentValues.put(COL4, product.getIsEnable() ? 1 : 0 );
            contentValues.put(COL5, product.getName());
            contentValues.put(COL6, product.getFullName());
            contentValues.put(COL7, product.getArticle());
            contentValues.put(COL8, product.getBarcode());
            contentValues.put(COL9, product.getWeight());
            contentValues.put(COL10, product.getMinSize().toString());
            contentValues.put(COL11, product.getSizeStep().toString());
            contentValues.put(COL12, product.getPricePerSizeStep().toString());
            contentValues.put(COL13, product.getPricePerSizeStep().toString());
            contentValues.put(COL14, product.getMaxSize().toString());
            contentValues.put(COL15, additionalCount.toString());
            contentValues.put(COL16, product.getSizeType());

            long result = db.insert(TABLE_NAME, null, contentValues);
            db.close();
            return result != -1;
        } else {
            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL2, cursor.getLong(1));
            contentValues.put(COL3, cursor.getInt(2));
            contentValues.put(COL4, cursor.getInt(3) == 1 );
            contentValues.put(COL5, cursor.getString(4));
            contentValues.put(COL6, cursor.getString(5));
            contentValues.put(COL7, cursor.getString(6));
            contentValues.put(COL8, cursor.getLong(7));
            contentValues.put(COL9, cursor.getLong(8));
            contentValues.put(COL10, cursor.getString(9));
            contentValues.put(COL11, cursor.getString(10));
            contentValues.put(COL12, cursor.getString(11));
            contentValues.put(COL13, cursor.getString(12));
            contentValues.put(COL14, cursor.getString(13));
            contentValues.put(COL15, new BigDecimal(cursor.getString(14)).add(additionalCount).toString());
            contentValues.put(COL16, cursor.getString(15));

            long result = db.update(
                    TABLE_NAME, contentValues,
                    "ARTICLE = ?", new String[] { product.getArticle().toString() }
            );
            cursor.close();
            db.close();
            return result != -1;
        }
    }

    public boolean addNewShopCartProduct(CategoryAndProduct product, BigDecimal allCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null)
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, String.valueOf(product.getParentCategory()));
        contentValues.put(COL3, product.getPosition());
        contentValues.put(COL4, product.getIsEnable() ? 1 : 0 );
        contentValues.put(COL5, product.getName());
        contentValues.put(COL6, product.getFullName());
        contentValues.put(COL7, product.getArticle());
        contentValues.put(COL8, product.getBarcode());
        contentValues.put(COL9, product.getWeight());
        contentValues.put(COL10, product.getMinSize().toString());
        contentValues.put(COL11, product.getSizeStep().toString());
        contentValues.put(COL12, product.getPricePerSizeStep().toString());
        contentValues.put(COL13, product.getPricePerSizeStep().toString());
        contentValues.put(COL14, product.getMaxSize().toString());
        contentValues.put(COL15, allCount.toString());
        contentValues.put(COL16, product.getSizeType());

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean addAdditionalCountToShopCartProduct(ShopCartProduct shopCartProduct, BigDecimal additionalCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null)
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, shopCartProduct.getParentCategory());
        contentValues.put(COL3, shopCartProduct.getPosition());
        contentValues.put(COL4, shopCartProduct.getIsEnable() );
        contentValues.put(COL5, shopCartProduct.getName());
        contentValues.put(COL6, shopCartProduct.getFullName());
        contentValues.put(COL7, shopCartProduct.getArticle());
        contentValues.put(COL8, shopCartProduct.getBarcode());
        contentValues.put(COL9, shopCartProduct.getWeight());
        contentValues.put(COL10, shopCartProduct.getMinSize().toString());
        contentValues.put(COL11, shopCartProduct.getSizeStep().toString());
        contentValues.put(COL12, shopCartProduct.getPricePerSizeStep().toString());
        contentValues.put(COL13, shopCartProduct.getWeightPerSizeStep().toString());
        contentValues.put(COL14, shopCartProduct.getMaxSize().toString());
        contentValues.put(COL15, shopCartProduct.getAllCount().add(additionalCount).toString());
        contentValues.put(COL16, shopCartProduct.getSizeType());

        long result = db.update(
                TABLE_NAME, contentValues,
                "ARTICLE = ?", new String[] { shopCartProduct.getArticle().toString() }
        );

        return result != -1;
    }

    /*
     * Сохраняет продукт в корзину
     * @param product - сохраняемый продукт
     */
    public boolean minusOneProductSizeStepFromAllCount(CategoryAndProduct product) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null)
            return false;

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL7 + " = '" +product.getArticle() + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor==null || cursor.getCount()==0) {
            if (cursor != null)
                cursor.close();
            db.close();
            return true;
        } else {
            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL2, cursor.getLong(1));
            contentValues.put(COL3, cursor.getInt(2));
            contentValues.put(COL4, cursor.getInt(3) == 1 );
            contentValues.put(COL5, cursor.getString(4));
            contentValues.put(COL6, cursor.getString(5));
            contentValues.put(COL7, cursor.getString(6));
            contentValues.put(COL8, cursor.getLong(7));
            contentValues.put(COL9, cursor.getLong(8));
            contentValues.put(COL10, cursor.getString(9));
            contentValues.put(COL11, cursor.getString(10));
            contentValues.put(COL12, cursor.getString(11));
            contentValues.put(COL13, cursor.getString(12));
            contentValues.put(COL14, cursor.getString(13));
            contentValues.put(COL15, new BigDecimal(cursor.getString(14)).subtract(product.getSizeStep()).toString());
            contentValues.put(COL16, cursor.getString(15));

            long result = db.update(
                    TABLE_NAME, contentValues,
                    "ARTICLE = ?", new String[] { product.getArticle().toString() }
            );
            cursor.close();
            db.close();
            return result != -1;
        }
    }

    /*
     * Получаем продукт из БД по артикулу
     * @param article - артикул получаемого продукта
     */
    public ShopCartProduct getByArticle(Long article) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL7 + " = '" + article + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor==null || cursor.getCount()==0) {
            if (cursor != null)
                cursor.close();
            db.close();
            return null;
        }

        cursor.moveToFirst();
        ShopCartProduct product = new ShopCartProduct();
        product.setId(cursor.getLong(0));
        product.setParentCategory(cursor.getLong(1));
        product.setPosition(cursor.getInt(2));
        product.setIsEnable( cursor.getInt(3) == 1 );
        product.setName(cursor.getString(4));
        product.setFullName(cursor.getString(5));
        product.setArticle(cursor.getLong(6));
        product.setBarcode(cursor.getLong(7));
        product.setWeight(cursor.getString(8));
        product.setMinSize(new BigDecimal(cursor.getString(9)));
        product.setSizeStep(new BigDecimal(cursor.getString(10)));
        product.setPricePerSizeStep(new BigDecimal(cursor.getString(11)));
        product.setWeightPerSizeStep(new BigDecimal(cursor.getString(12)));
        product.setMaxSize(new BigDecimal(cursor.getString(13)));
        product.setAllCount(new BigDecimal(cursor.getString(14)));
        product.setSizeType(cursor.getString(15));

        cursor.close();
        db.close();
        return product;
    }

    public List<ShopCartProduct> getProductsFromShopCart() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor==null || cursor.getCount()==0) {
            if (cursor != null)
                cursor.close();
            db.close();
            return new ArrayList<>();
        }

//        cursor.moveToFirst();
        List<ShopCartProduct> products = new ArrayList<>();
        while (cursor.moveToNext()) {
            ShopCartProduct product = new ShopCartProduct();
            product.setId(cursor.getLong(0));
            product.setParentCategory(cursor.getLong(1));
            product.setPosition(cursor.getInt(2));
            product.setIsEnable( cursor.getInt(3) == 1 );
            product.setName(cursor.getString(4));
            product.setFullName(cursor.getString(5));
            product.setArticle(cursor.getLong(6));
            product.setBarcode(cursor.getLong(7));
            product.setWeight(cursor.getString(8));
            product.setMinSize(new BigDecimal(cursor.getString(9)));
            product.setSizeStep(new BigDecimal(cursor.getString(10)));
            product.setPricePerSizeStep(new BigDecimal(cursor.getString(11)));
            product.setWeightPerSizeStep(new BigDecimal(cursor.getString(12)));
            product.setMaxSize(new BigDecimal(cursor.getString(13)));
            product.setAllCount(new BigDecimal(cursor.getString(14)));
            product.setSizeType(cursor.getString(15));

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
//    public boolean updateProduct(Product product) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL2, String.valueOf(product.getType()));
//        contentValues.put(COL3, product.getParentCategory());
//        contentValues.put(COL4, product.getPosition());
//        contentValues.put(COL5, product.getIsEnable() ? 1 : 0 );
//        contentValues.put(COL6, product.getName());
//        contentValues.put(COL7, product.getFullName());
//        contentValues.put(COL8, product.getArticle());
//        contentValues.put(COL9, product.getBarcode());
//        contentValues.put(COL10, product.getWeight());
//        contentValues.put(COL11, product.getMinSize().toString());
//        contentValues.put(COL12, product.getSizeStep().toString());
//        contentValues.put(COL13, product.getPricePerSizeStep().toString());
//        contentValues.put(COL14, product.getPricePerSizeStep().toString());
//        contentValues.put(COL15, product.getMaxSize().toString());
//        contentValues.put(COL16, product.getSizeType());
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        if (db == null) {
//            return false;
//        }
//        long result = db.update(
//                TABLE_NAME, contentValues,
//                "ARTICLE = ?", new String[] {product.getArticle().toString()}
//                );
//        db.close();
//        return result != -1;
//    }

    public boolean deleteByArticle(Long article) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(
                TABLE_NAME,
                "ARTICLE = ?", new String[]{ article.toString() }
        );
        db.close();
        return result != -1;
    }

}
