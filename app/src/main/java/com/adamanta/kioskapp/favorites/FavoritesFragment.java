package com.adamanta.kioskapp.favorites;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.product.fragments.productImagesFragment.ProductImagesFragment;
import com.adamanta.kioskapp.product.utils.Utils;
import com.adamanta.kioskapp.shopcart.ProductsCartFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements View.OnClickListener {
    private View v;
    private List<FavoritesList> favoritesList = new ArrayList<>();
    private final String TAG = this.getClass().getSimpleName();

    private ImageView favoritesProductMainImgV;
    private TextView favoritesProductNameTV;
    private TextView favoritesProductmanTV;
    private TextView favoritesVendorcodeTV;
    private TextView favoritesBarcodeTV;
    private TextView favoritesPriceProductTV;
    private TextView favoritesMeasureProductTV;
    private TextView favoritesWeightTV;
    private ImageButton favoritesRemoveProductImgB;
    private TextView favoritesCountProductTV;
    private ImageButton favoritesAddProductImgB;
    private Button favoritesAddProductToCartB;
    private ImageButton favoritesShowCartButton;

    private String productPathToFragments = "";
    private String productNameToFragments = "";
    private float countProductTVChangeFloat = 0;
    private int countProductTVChangeInt = 0;
    private float startCountProduct = 0;
    private int startCountProductInt = 0;
    private boolean isCountInt;
    private int amountProductAll = 1;

    private FavoritesRVAdapter favoritesRVAdapter = new FavoritesRVAdapter(favoritesList);

    public static FavoritesFragment newInstance(int num) {
        FavoritesFragment f = new FavoritesFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.favorites_fragment, container, false);

        try{
            File sdFileCart = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "Retail/", "избранное");

            BufferedReader br = new BufferedReader(new FileReader(sdFileCart), 100);
            if (br.readLine() == null) { Utils.writeStringToFile(sdFileCart, " ", false); }
            br.close();

            String str;
            BufferedReader br2 = new BufferedReader(new FileReader(sdFileCart), 100);
            while((str = br2.readLine()) != null) {
                if (str.contains(";")) {
                    String[] sArr = str.split(";");
                    favoritesList.add(new FavoritesList(sArr[0], sArr[1]));
                }
            }
            br2.close();
        }
        catch(IOException e) { e.printStackTrace(); Log.e(TAG, "IOException= " + e); }


        //****************************** Buttons ***********************************
        ImageButton closeFragmentBtn = v.findViewById(R.id.favorites_closefragment_imgb);
        closeFragmentBtn.setOnClickListener(this);

        favoritesProductMainImgV = v.findViewById(R.id.favorites_product_imgv);
        favoritesProductMainImgV.setOnClickListener(this);
        favoritesProductNameTV = v.findViewById(R.id.favorites_productname_tv);
        favoritesProductmanTV = v.findViewById(R.id.favorites_productman_tv);
        favoritesVendorcodeTV = v.findViewById(R.id.favorites_vendorcode_tv);
        favoritesBarcodeTV = v.findViewById(R.id.favorites_barcode_tv);
        favoritesPriceProductTV = v.findViewById(R.id.favorites_priceproduct_tv);
        favoritesWeightTV = v.findViewById(R.id.favorites_productweight_tv);
        favoritesRemoveProductImgB = v.findViewById(R.id.favorites_removeproduct_imgb);
        favoritesRemoveProductImgB.setOnClickListener(this);
        favoritesCountProductTV = v.findViewById(R.id.favorites_prodcount_et);
        favoritesMeasureProductTV = v.findViewById(R.id.favorites_productmeasure_tv);
        favoritesAddProductImgB = v.findViewById(R.id.favorites_addproduct_imgb);
        favoritesAddProductImgB.setOnClickListener(this);
        favoritesAddProductToCartB = v.findViewById(R.id.favorites_addtocart_b);
        favoritesAddProductToCartB.setOnClickListener(this);
        favoritesShowCartButton = v.findViewById(R.id.favorites_showcart_b);
        favoritesShowCartButton.setOnClickListener(this);
        //****************************** Buttons ***********************************

        //****************************** Favorites Adapter *****************************
        final RecyclerView favoritesRecyclerView = v.findViewById(R.id.favorites_rv);
        favoritesRecyclerView.setHasFixedSize(false);
        favoritesRecyclerView.setAdapter(favoritesRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        favoritesRecyclerView.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        favoritesRecyclerView.setItemAnimator(itemAnimator);
        //****************************** Favorites Adapter *****************************

        return v;
    }

    //***************************** Buttons *********************************
    public void onClick(@NonNull View v) {
        switch (v.getId()){
            case R.id.favorites_closefragment_imgb:
                try{
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                } catch (NullPointerException e){ Log.e(TAG,"NullPointExc" + e); }
                break;

            case R.id.favorites_removeproduct_imgb:
                if (isCountInt) {
                    int countProduct = Integer.parseInt(favoritesCountProductTV.getText().toString());
                    if (countProduct > startCountProduct) {
                        favoritesCountProductTV.setText(String.valueOf(countProduct - countProductTVChangeInt));
                        amountProductAll--;
                    }
                } else if (!isCountInt) {
                    float countProduct = Float.parseFloat(favoritesCountProductTV.getText().toString());
                    if (countProduct > startCountProduct) {
                        float buf = countProduct - countProductTVChangeFloat;
                        favoritesCountProductTV.setText(Utils.formatFloatToString(buf, 1));
                        amountProductAll--;
                    }
                }
                favoritesAddProductToCartB.setText("Добавить");
                break;

            case R.id.favorites_addproduct_imgb:
                if (isCountInt) {
                    int countProduct = Integer.parseInt(favoritesCountProductTV.getText().toString());
                    favoritesCountProductTV.setText(String.valueOf(countProduct + countProductTVChangeInt));
                } else if (!isCountInt){
                    float countProduct = Float.parseFloat(favoritesCountProductTV.getText().toString());
                    float buf = countProduct + countProductTVChangeFloat;
                    favoritesCountProductTV.setText(Utils.formatFloatToString(buf, 1));
                }
                amountProductAll++;
                favoritesAddProductToCartB.setText("Добавить");
                break;

            case R.id.favorites_addtocart_b:
                String productName = favoritesProductNameTV.getText().toString(),
                        vendorcode = favoritesVendorcodeTV.getText().toString().substring(8),
                        amountMeasure = favoritesMeasureProductTV.getText().toString(),
                        countProduct = favoritesCountProductTV.getText().toString(),
                        priceProduct = favoritesPriceProductTV.getText().toString();

                Utils.addProductToCartFile(isCountInt, productName, vendorcode, priceProduct,
                        countProduct, amountMeasure, amountProductAll);
                amountProductAll = 1;
                favoritesAddProductToCartB.setText(R.string.addtocart);
                break;

            case R.id.favorites_showcart_b:
                Fragment cartFragment = ProductsCartFragment.newInstance(123);
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.favorites_mainlayout, cartFragment, "cartFragment");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
                break;

            case R.id.favorites_product_imgv:
                Fragment productImagesFragment = ProductImagesFragment.newInstance(0L, "123");
                FragmentTransaction ftr = getChildFragmentManager().beginTransaction();
                ftr.replace(R.id.favorites_mainlayout, productImagesFragment, "productImagesFragment");
                ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftr.addToBackStack(null);
                ftr.commit();
                break;

            default:
                break;
        }
    }

    //***************************** Buttons *********************************

    //************ Interfaces ******************
    public void setCard(String productPath, String productName) {
        favoritesRemoveProductImgB.setVisibility(View.VISIBLE);
        favoritesRemoveProductImgB.setClickable(true);
        favoritesRemoveProductImgB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8BC34A")));

        favoritesAddProductImgB.setVisibility(View.VISIBLE);
        favoritesAddProductImgB.setClickable(true);
        favoritesAddProductImgB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8BC34A")));

        favoritesPriceProductTV.setVisibility(View.VISIBLE);
        favoritesCountProductTV.setVisibility(View.VISIBLE);

        favoritesAddProductToCartB.setVisibility(View.VISIBLE);
        favoritesAddProductToCartB.setClickable(true);
        favoritesAddProductToCartB.setText("Добавить");
        favoritesAddProductToCartB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8BC34A")));

        favoritesShowCartButton.setVisibility(View.VISIBLE);

        //vendorcode запоминаем для поиска цены
        String vendorcode = "";
        productPathToFragments = productPath;
        productNameToFragments = productName;
        //---------------------------------------------------------------
        //установка изображения товара
        //File sdPath = Environment.getExternalStorageDirectory();
        //sdPath = new File(sdPath.getAbsolutePath() + "/" + productsDir + "/");
        File sdFile = new File(productPath, productName + "_1.webp");
        Bitmap bitmap = Utils.getScaledBitmap(sdFile.getAbsolutePath(), 250, 350);
        favoritesProductMainImgV.setImageBitmap(bitmap);
        favoritesProductMainImgV.setVisibility(View.VISIBLE);
        //---------------------------------------------------------------
        try {
            String str;
            //заполнение элементов в карточке товара
            sdFile = new File(productPath, productName);
            BufferedReader br = new BufferedReader(new FileReader(sdFile), 100);
            int strNum = 1;
            while ((str = br.readLine()) != null){
                if (strNum == 1) {
                    favoritesProductNameTV.setText(str);
                } else if (strNum == 2) {
                    favoritesProductmanTV.setText(str);
                } else if (strNum == 3) {
                    favoritesWeightTV.setText(str);
                } else if (strNum == 5) {
                    favoritesVendorcodeTV.setText(str);
                    vendorcode = str.substring(8);
                } else if(strNum == 6){
                    favoritesBarcodeTV.setText(str);
                } else if(strNum == 7){
                    str = str.substring(11);
                    String[] buf = str.split(";");
                    favoritesCountProductTV.setText(buf[0]);
                    startCountProduct = Float.parseFloat(buf[0]);
                    if (buf[0].contains(".")){
                        int ibuf = buf[0].indexOf(".");
                        String sbuf = buf[0].substring(0, ibuf);
                        startCountProductInt = Integer.parseInt(sbuf);
                    } else {
                        startCountProductInt = Integer.parseInt(buf[0]);
                    }
                    favoritesMeasureProductTV.setText(buf[2]);
                    if (buf[3].contains("1")) {
                        countProductTVChangeInt = Integer.parseInt(buf[1]);
                        isCountInt = true;
                    } else if (buf[3].contains("0")) {
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
                    favoritesPriceProductTV.setText(String.valueOf(str.substring(7) + " руб."));
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
                    favoritesRemoveProductImgB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
                    favoritesRemoveProductImgB.setClickable(false);
                    favoritesAddProductImgB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
                    favoritesAddProductImgB.setClickable(false);
                    favoritesAddProductToCartB.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
                    favoritesAddProductToCartB.setClickable(false);

                    favoritesPriceProductTV.setText("Нет в наличии");
                    favoritesCountProductTV.setText("0");
                    break;
                }
            }
            br3.close();
        }
        catch(FileNotFoundException e){e.printStackTrace();Log.e(TAG,"FileNotFoundEx="+e); }
        catch (IOException e) { e.printStackTrace(); Log.e(TAG, "IOException= " + e); }
        //---------------------------------------------------------------
    }

    public void ftr(String path, String productName) {
        ProductImagesFragment fragment = (ProductImagesFragment)getChildFragmentManager()
                .findFragmentByTag("productImagesFragment");
        if (fragment != null) {
            fragment.changeMainImage(path);
        }
    }

    public void closeProductsImagesFragment(){
        ProductImagesFragment fragment = (ProductImagesFragment)getChildFragmentManager()
                .findFragmentByTag("productImagesFragment");
        if (fragment != null) {
            FragmentTransaction ftr = getChildFragmentManager().beginTransaction().remove(fragment);
            ftr.commit();
        }
    }
    //************ Interfaces ******************
}
