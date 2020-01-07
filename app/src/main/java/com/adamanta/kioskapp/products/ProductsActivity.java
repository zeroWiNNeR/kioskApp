package com.adamanta.kioskapp.products;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.adamanta.kioskapp.productImagesFragment.ProductImagesFragment;
import com.adamanta.kioskapp.productImagesFragment.ProductImagesSet;
import com.adamanta.kioskapp.products.interfaces.Postman;
import com.adamanta.kioskapp.products.utils.Utils;
import com.adamanta.kioskapp.products.adapters.CategoriesRVAdapter;
import com.adamanta.kioskapp.products.adapters.ProductsRVAdapter;
import com.adamanta.kioskapp.MainActivity;
import com.adamanta.kioskapp.products.fragments.ProductsCartFragment;
import com.adamanta.kioskapp.products.models.ProductsList;
import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.settings.AppSettings;
import com.adamanta.kioskapp.products.models.CategoriesList;

public class ProductsActivity extends AppCompatActivity implements Postman, ProductImagesSet {
    private final String TAG = this.getClass().getSimpleName();
    final AppSettings appSettings = new AppSettings();

    ImageView productsColumnImgV;
    ImageView productMainImgV;
    ImageView addToFavoritesImgB;
    TextView productNameTV;
    TextView manufacturerTV;            //производитель
    TextView vendorcodeTV;              //артикул
    TextView barcodeTV;                 //штрихкод
    TextView priceProductTV;            //цена
    TextView measureProductTV;          // чем измеряется
    TextView weightTV;                  //кол-во
    ImageButton removeProductImgB;
    TextView countProductTV;
    ImageButton addProductImgB;
    Button addProductToCartB;
    ImageButton showCartButton;

    String productsDir = "";
    String productPathToFragments = "";
    String productNameToFragments = "";
    float countProductTVChangeFloat = 0;
    int countProductTVChangeInt = 0;
    float startCountProduct = 0;
    boolean isCountInt;
    int amountProductAll = 1;

    final List<CategoriesList> categoriesList = new ArrayList<>();
    final CategoriesRVAdapter categoriesRVAdapter = new CategoriesRVAdapter(categoriesList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        appSettings.setDIR_SD("Retail/ProductsActivity/категории/");

        productsColumnImgV = findViewById(R.id.productscolumn_imgv);
        productMainImgV = findViewById(R.id.product_imgv);
        addToFavoritesImgB = findViewById(R.id.addtofavorites_ibtn);
        productNameTV = findViewById(R.id.productname_tv);
        manufacturerTV = findViewById(R.id.productman_tv);
        vendorcodeTV = findViewById(R.id.vendorcode_tv);
        barcodeTV = findViewById(R.id.barcode_tv);
        priceProductTV = findViewById(R.id.priceproduct_tv);
        weightTV = findViewById(R.id.productweight_tv);
        removeProductImgB = findViewById(R.id.removeproduct_imgb);
        countProductTV = findViewById(R.id.prodcount_et);
        measureProductTV = findViewById(R.id.productmeasure_tv);
        addProductImgB = findViewById(R.id.addproduct_imgb);
        addProductToCartB = findViewById(R.id.addtocart_b);
        showCartButton = findViewById(R.id.showcart_b);

        Log.e(TAG, "ProductsActivity loading successful");

        createCategoriesRVAdapter(categoriesList);
    }

    //***************************** Buttons and Actions *********************************
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.MainMenuButton:
                Context context = v.getContext();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                break;

            case R.id.removeproduct_imgb:
                if (isCountInt) {
                    int countProduct = Integer.parseInt(countProductTV.getText().toString());
                    if (countProduct > startCountProduct) {
                        countProductTV.setText(String.valueOf(countProduct - countProductTVChangeInt));
                        amountProductAll--;
                    }
                }
                else if (!isCountInt) {
                    float countProduct = Float.parseFloat(countProductTV.getText().toString());
                    if (countProduct > startCountProduct) {
                        float buf = countProduct - countProductTVChangeFloat;
                        countProductTV.setText(Utils.formatFloatToString(buf, 1));
                        amountProductAll--;
                    }
                }
                addProductToCartB.setText("Добавить");
                break;

            case R.id.addproduct_imgb:
                if (isCountInt) {
                    int countProduct = Integer.parseInt(countProductTV.getText().toString());
                    countProductTV.setText(String.valueOf(countProduct + countProductTVChangeInt));
                }
                else if (!isCountInt){
                    float countProduct = Float.parseFloat(countProductTV.getText().toString());
                    float buf = countProduct + countProductTVChangeFloat;
                    countProductTV.setText(Utils.formatFloatToString(buf, 1));
                }
                amountProductAll++;
                addProductToCartB.setText("Добавить");
                break;

            case R.id.addtocart_b:
                String productName = productNameTV.getText().toString(),
                        vendorcode = vendorcodeTV.getText().toString().substring(8),
                        amountMeasure = measureProductTV.getText().toString(),
                        countProduct = countProductTV.getText().toString(),
                        priceProduct = priceProductTV.getText().toString();

                Utils.addProductToCartFile(isCountInt, productName, vendorcode, priceProduct,
                        countProduct, amountMeasure, amountProductAll);
                //amountProductAll = 1;
                addProductToCartB.setText(R.string.addtocart);
                break;

            case R.id.showcart_b:
                Fragment cartFragment = ProductsCartFragment.newInstance(123);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.prodactivity_mainlayout, cartFragment, "cartFragment");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
                break;

            case R.id.addtofavorites_ibtn:
                String str = productNameToFragments + ";"
                        + Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                        + productsDir + "/";

                if (Utils.checkInFavorites(str)) {
                    Utils.deleteFromFavorites(str);
                    addToFavoritesImgB.setImageResource(R.drawable.ic_notfavorites);
                } else if (!Utils.checkInFavorites(str)) {
                    Utils.addToFavorites(str);
                    addToFavoritesImgB.setImageResource(R.drawable.ic_favorites);
                }
                break;

            case R.id.product_imgv:
                Fragment productImagesFragment = ProductImagesFragment.newInstance(productPathToFragments+";"+productNameToFragments);
                FragmentTransaction ftr = getSupportFragmentManager().beginTransaction();
                ftr.replace(R.id.prodactivity_mainlayout, productImagesFragment, "productImagesFragment");
                ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftr.addToBackStack(null);
                ftr.commit();
                break;

            default:
                break;
        }
    }

    public void backButtonClick(View view){
        if(categoriesRVAdapter.categoryLevel(0) > 0){
            int categoryLevel = categoriesRVAdapter.categoryLevel(0);
            String folder = categoriesRVAdapter.prevFolderPath(categoryLevel);
            categoriesRVAdapter.categoryLevel(-1);
            //Log.e(TAG, "Возврат к предыдущему каталогу:" + folder);
            appSettings.setDIR_SD(folder);
            //categoriesList.clear();
            categoriesRVAdapter.refreshProdActivityCatRV();
            //productsList.clear();
            createCategoriesRVAdapter(categoriesList);
            final RecyclerView productsRecyclerView = findViewById(R.id.products_rv);
            productsRecyclerView.setAdapter(null);
            productsRecyclerView.setLayoutManager(null);
            productsRecyclerView.setItemAnimator(null);
        }
        else if (categoriesRVAdapter.categoryLevel(0) == 0){
            Context context = view.getContext();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }

        clearProductCard();
        productsColumnImgVAction("hide");
        productsActivityMainImgVAction("show");
    }
    public void backAction(View view, String dir){
        productsDir = dir;
        backButtonClick(view);
    }

    private void createCategoriesRVAdapter(List<CategoriesList> categories){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "Память не доступна: " + Environment.getExternalStorageState());
            return;
        }
        //Log.e(TAG, "Считывание файла с категориями происходит из: " + appSettings.getDIR_SD());
        try {
            File sdFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"
                    +appSettings.getDIR_SD()+"/", "содержание");
            BufferedReader br = new BufferedReader(new FileReader(sdFile), 100);
            String categoryName, buttonColor, textColor;
            int number = 0;
            String str = br.readLine();
            if(str.contains("Каталоги")){
                while ((str = br.readLine()) != null){
                    String [] buf = str.split(";");
                    categoryName = buf[0];
                    buttonColor = buf[1];
                    textColor = buf[2];
                    number++;
                    categories.add(new CategoriesList(number, categoryName, buttonColor, textColor));
                }
            }
            br.close();
        }
        catch(FileNotFoundException e){ e.printStackTrace();Log.e(TAG,"FileNotFoundEx= "+e); }
        catch (IOException e) { e.printStackTrace(); Log.e(TAG,"IOException= " + e); }
        //Log.e(TAG, "Эти записи внесены в список адаптера, создан список catRV");

        final RecyclerView categoriesRecyclerView = findViewById(R.id.categories_rv);
        categoriesRecyclerView.setHasFixedSize(false);
        categoriesRecyclerView.setAdapter(categoriesRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        categoriesRecyclerView.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        categoriesRecyclerView.setItemAnimator(itemAnimator);
    }

    public void createProductsRVAdapter(List<ProductsList> products){
        final RecyclerView productsRecyclerView = findViewById(R.id.products_rv);
        productsRecyclerView.setHasFixedSize(false);
        final ProductsRVAdapter productsRVAdapter = new ProductsRVAdapter(products);
        productsRecyclerView.setAdapter(productsRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productsRecyclerView.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        productsRecyclerView.setItemAnimator(itemAnimator);
    }
    //***************************** Buttons and Actions *********************************

    //************* UI ***********************
    public void setProductCard(String productName){
        removeProductImgB.setVisibility(View.VISIBLE);
        removeProductImgB.setClickable(true);
        removeProductImgB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8BC34A")));

        addProductImgB.setVisibility(View.VISIBLE);
        addProductImgB.setClickable(true);
        addProductImgB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8BC34A")));

        priceProductTV.setVisibility(View.VISIBLE);
        countProductTV.setVisibility(View.VISIBLE);

        addProductToCartB.setVisibility(View.VISIBLE);
        addProductToCartB.setClickable(true);
        addProductToCartB.setText("Добавить");
        addProductToCartB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8BC34A")));

        showCartButton.setVisibility(View.VISIBLE);

        //vendorcode запоминаем для поиска цены
        String vendorcode = "";
        productPathToFragments = Environment.getExternalStorageDirectory().getAbsolutePath()
                +"/" + productsDir + "/";
        productNameToFragments = productName;
        //---------------------------------------------------------------
        //установка изображения товара
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/" + productsDir + "/");
        File sdFile = new File(sdPath, productName + "_1.webp");
        Bitmap bitmap = Utils.getScaledBitmap(sdFile.getAbsolutePath(), 250, 350);
        productMainImgV.setImageBitmap(bitmap);
        productMainImgV.setVisibility(View.VISIBLE);
        //---------------------------------------------------------------
        //заполнение элементов в карточке товара
        try {
            sdFile = new File(sdPath, productName);
            BufferedReader br = new BufferedReader(new FileReader(sdFile), 100);
            int strNum = 1;
            String str;
            while ((str = br.readLine()) != null){
                if (strNum == 1) {
                    productNameTV.setText(str);
                } else if (strNum == 2) {
                    manufacturerTV.setText(str);
                } else if (strNum == 3) {
                    weightTV.setText(str);
                } else if (strNum == 5) {
                    vendorcodeTV.setText(str);
                    vendorcode = str.substring(8);
                } else if(strNum == 6){
                    barcodeTV.setText(str);
                } else if(strNum == 7){
                    str = str.substring(11);
                    String[] buf = str.split(";");
                    countProductTV.setText(buf[0]);
                    startCountProduct = Float.parseFloat(buf[0]);
                    measureProductTV.setText(buf[2]);
                    if (buf[3].equals("1")) {
                        countProductTVChangeInt = Integer.parseInt(buf[1]);
                        isCountInt = true;
                    } else if (buf[3].equals("0")) {
                        countProductTVChangeFloat = Float.parseFloat(buf[1]);
                        isCountInt = false;
                    }
                    amountProductAll = Math.round(Float.parseFloat(buf[0])/Float.parseFloat(buf[1]));
                }
                strNum++;
            }
            br.close();
        //---------------------------------------------------------------
        //загрузка цены
            File sdFilePrice = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "Retail/ProductsActivity/", "цена");
            BufferedReader br2 = new BufferedReader(new FileReader(sdFilePrice), 100);
            while ((str = br2.readLine()) != null){
                if(str.contains(vendorcode)){
                    priceProductTV.setText(String.valueOf(str.substring(7) + " руб."));
                    break;
                }
            }
            br2.close();
        //---------------------------------------------------------------
        //загрузка количества
            File sdFileCount = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "Retail/ProductsActivity/", "колич");
            BufferedReader br3 = new BufferedReader(new FileReader(sdFileCount), 100);
            while ((str = br3.readLine()) != null){
                if(str.contains(vendorcode) && str.contains("не")){
                    removeProductImgB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
                    removeProductImgB.setClickable(false);
                    addProductImgB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
                    addProductImgB.setClickable(false);
                    addProductToCartB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
                    addProductToCartB.setClickable(false);

                    priceProductTV.setText("Нет в наличии");
                    countProductTV.setText("0");
                    break;
                }
            }
            br3.close();
        }
        catch(FileNotFoundException e){e.printStackTrace();Log.e(TAG,"FileNotFoundEx="+e); }
        catch (IOException e) { e.printStackTrace(); Log.e(TAG, "IOException= " + e); }
        //---------------------------------------------------------------
        if (Utils.checkInFavorites(productName)) {
            addToFavoritesImgB.setImageResource(R.drawable.ic_favorites);
        } else {
            addToFavoritesImgB.setImageResource(R.drawable.ic_notfavorites);
        }
        addToFavoritesImgB.setVisibility(View.VISIBLE);
    }
    public void clearProductCard(){
        removeProductImgB.setVisibility(View.INVISIBLE);
        addProductImgB.setVisibility(View.INVISIBLE);

        countProductTV.setText(null);
        measureProductTV.setText(null);

        addProductToCartB.setVisibility(View.INVISIBLE);
        showCartButton.setVisibility(View.INVISIBLE);

        productMainImgV.setImageBitmap(null);
        productMainImgV.setVisibility(View.INVISIBLE);

        addToFavoritesImgB.setVisibility(View.INVISIBLE);

        productNameTV.setText(null);
        manufacturerTV.setText(null);

        weightTV.setText(null);
        priceProductTV.setText(null);

        vendorcodeTV.setText(null);
        barcodeTV.setText(null);

        productsColumnImgV.setVisibility(View.INVISIBLE);
    }

    public void productsColumnImgVAction(@NonNull String action){
        if(action.contains("show"))
            productsColumnImgV.setVisibility(View.VISIBLE);
        if (action.contains("hide"))
            productsColumnImgV.setVisibility(View.INVISIBLE);
    }
    public void productsActivityMainImgVAction(@NonNull String action){
        ImageView productsActivityMainImgV = findViewById(R.id.productsActivityMainImage);
        if(action.contains("show"))
            productsActivityMainImgV.setVisibility(View.VISIBLE);
        else if (action.contains("hide"))
            productsActivityMainImgV.setVisibility(View.INVISIBLE);
    }
    //************* UI ***********************

    //************ Interfaces ******************
    @Override
    public void calculation(){
        ProductsCartFragment fragment = (ProductsCartFragment)getSupportFragmentManager()
                .findFragmentByTag("cartFragment");
        if (fragment != null)
            fragment.calculation();
    }
    @Override
    public void productImagesSetImgV(String path, String productName) {
        ProductImagesFragment fragment = (ProductImagesFragment)getSupportFragmentManager()
                .findFragmentByTag("productImagesFragment");
        if (fragment != null) {
            fragment.setImageView(path, productName);
        }
    }
    @Override
    public void productImagesFragmentClose(){
        ProductImagesFragment fragment = (ProductImagesFragment)getSupportFragmentManager()
            .findFragmentByTag("productImagesFragment");
        if (fragment != null) {
            FragmentTransaction ftr = getSupportFragmentManager().beginTransaction().remove(fragment);
            ftr.commit();
        }
    }
    //************ Interfaces ******************
}