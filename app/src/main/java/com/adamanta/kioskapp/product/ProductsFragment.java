package com.adamanta.kioskapp.product;

import android.database.Cursor;
import android.os.Bundle;
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

import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.products.models.CategoriesList;
import com.adamanta.kioskapp.products.utils.ProductsCategoriesDbHelper;

import java.util.List;

public class ProductsFragment extends Fragment implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    private ProductsCategoriesDbHelper productsCategoriesDBHelper;
    private ImageView productsColumnImgV,
            productMainImgV,
            addToFavoritesImgB;
    private TextView productNameTV,
            manufacturerTV,            //производитель
            vendorcodeTV,              //артикул
            barcodeTV,                 //штрихкод
            priceProductTV,            //цена
            measureProductTV,          // чем измеряется
            weightTV,                  //кол-во
            countProductTV;
    private ImageButton removeProductImgB,
            addProductImgB,
            showCartButton;
    private Button addProductToCartB;

    private float countProductTVChangeFloat = 0;
    private int countProductTVChangeInt = 0;
    private float startCountProduct = 0;
    private boolean isCountInt;
    private int amountProductAll = 1;

    public static ProductsFragment newInstance(int num) {
        ProductsFragment productsFragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        productsFragment.setArguments(args);
        return productsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        ImageButton closeFragmentBtn = view.findViewById(R.id.fragment_products_main_menu_imgbtn);
        closeFragmentBtn.setOnClickListener(this);

        productsColumnImgV = view.findViewById(R.id.fragment_products_productscolumn_imgv);
        productMainImgV = view.findViewById(R.id.fragment_products_product_imgv);
        addToFavoritesImgB = view.findViewById(R.id.fragment_products_addtofavorites_imgbtn);
        productNameTV = view.findViewById(R.id.fragment_products_productname_tv);
        manufacturerTV = view.findViewById(R.id.fragment_products_productman_tv);
        vendorcodeTV = view.findViewById(R.id.fragment_products_vendorcode_tv);
        barcodeTV = view.findViewById(R.id.fragment_products_barcode_tv);
        priceProductTV = view.findViewById(R.id.fragment_products_priceproduct_tv);
        weightTV = view.findViewById(R.id.fragment_products_productweight_tv);
        removeProductImgB = view.findViewById(R.id.fragment_products_removeproduct_imgbtn);
        countProductTV = view.findViewById(R.id.fragment_products_prodcount_et);
        measureProductTV = view.findViewById(R.id.fragment_products_productmeasure_tv);
        addProductImgB = view.findViewById(R.id.fragment_products_addproduct_imgbtn);
        addProductToCartB = view.findViewById(R.id.fragment_products_addtocart_btn);
        showCartButton = view.findViewById(R.id.fragment_products_showcart_btn);

        productsCategoriesDBHelper = new ProductsCategoriesDbHelper(view.getContext());

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fragment_products_main_menu_imgbtn:
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                }
                break;

            default:
                break;
        }
    }

    private void createCategoriesRVAdapter(List<CategoriesList> categories) {
        try {
            int number = 0;
            Cursor data = productsCategoriesDBHelper.getWithParent("main");
            while (data.moveToNext()) {
                categories.add(new CategoriesList(
                        number,
                        data.getString(1),
                        data.getString(2),
                        data.getString(3)
                ));
                number++;
            }
            data.close();

        }
        catch(Exception e) { e.printStackTrace();Log.e( TAG,"Exc= "+e ); }

//        final RecyclerView categoriesRecyclerView = findViewById(R.id.categories_rv);
//        categoriesRecyclerView.setHasFixedSize(false);
//        categoriesRecyclerView.setAdapter(categoriesRVAdapter);
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        categoriesRecyclerView.setLayoutManager(layoutManager);
//        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
//        categoriesRecyclerView.setItemAnimator(itemAnimator);
    }
}
