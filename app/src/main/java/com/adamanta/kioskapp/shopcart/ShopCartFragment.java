package com.adamanta.kioskapp.shopcart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.products.utils.ProductsDBHelper;
import com.adamanta.kioskapp.shopcart.model.ShopCartProduct;
import com.adamanta.kioskapp.shopcart.utils.ShopCartDBHelper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class ShopCartFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView totalpriceTV;

    public static ShopCartFragment newInstance(int num) {
        ShopCartFragment f = new ShopCartFragment();
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
        view = inflater.inflate(R.layout.fragment_shopcart, container, false);

        totalpriceTV = view.findViewById(R.id.fragment_shopcart_totalprice_tv);
        ImageButton closeFragmentBtn = view.findViewById(R.id.fragment_shopcart_closefragment_btn);
        closeFragmentBtn.setOnClickListener(this);

        ShopCartDBHelper shopCartDBHelper = new ShopCartDBHelper(view.getContext());
        ProductsDBHelper productsDBHelper = new ProductsDBHelper(view.getContext());
        List<ShopCartProduct> shopCartList = shopCartDBHelper.getProductsFromShopCart();
        for (ShopCartProduct shopCartProduct : shopCartList) {
            shopCartProduct.setStockQuantity(productsDBHelper.getByArticle(shopCartProduct.getArticle()).getStockQuantity());
        }

        ShopCartRVAdapter shopCartRVAdapter = new ShopCartRVAdapter(shopCartList);
        final RecyclerView cartRecyclerView = view.findViewById(R.id.fragment_shopcart_rv);
        cartRecyclerView.setHasFixedSize(false);
        cartRecyclerView.setMotionEventSplittingEnabled(false);
        cartRecyclerView.setAdapter(shopCartRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        cartRecyclerView.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        cartRecyclerView.setItemAnimator(itemAnimator);

        calculation();

        return view;
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.fragment_shopcart_closefragment_btn) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
        }
    }

    public void calculation() {
        ShopCartDBHelper shopCartDBHelper = new ShopCartDBHelper(view.getContext());
        List<ShopCartProduct> shopCartList = shopCartDBHelper.getProductsFromShopCart();
        BigDecimal totalPrice = new BigDecimal(0);
        if (shopCartList.size() == 0) {
            totalpriceTV.setText("0,00");
        } else {
            for (ShopCartProduct shopCartProduct : shopCartList) {
                totalPrice = totalPrice.add(shopCartProduct.getAllCount().divide(shopCartProduct.getSizeStep()).multiply(shopCartProduct.getPricePerSizeStep()));
            }
            DecimalFormat priceFormat = new DecimalFormat("###.##");
            totalpriceTV.setText(priceFormat.format(totalPrice));
        }
    }
}
