package com.adamanta.kioskapp.shopcart;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import com.adamanta.kioskapp.product.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductsCartFragment extends Fragment implements View.OnClickListener {

    private View view;
    private List<CartList> cartList = new ArrayList<>();
    private TextView totalpriceTV;
    private CartRVAdapter cartRVAdapter = new CartRVAdapter(cartList);

    public static ProductsCartFragment newInstance(int num) {
        ProductsCartFragment f = new ProductsCartFragment();
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
        view = inflater.inflate(R.layout.prodactivity_cart_fragment, container, false);

        try {
            File sdFileCart = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "Retail/", "корзина");
            BufferedReader br = new BufferedReader(new FileReader(sdFileCart), 100);
            if (br.readLine() == null) { Utils.writeStringToFile(sdFileCart, " ", false); }
            br.close();

            int lineNumber = 1;
            String str;
            float fbuf = 0;
            BufferedReader br2 = new BufferedReader(new FileReader(sdFileCart), 100);
            while((str = br2.readLine()) != null) {
                if (str.contains(";")) {
                    String[] sArr = str.split(";");
                    cartList.add(new CartList(
                            lineNumber,                     //Number
                            sArr[0],                      //Name
                            Integer.parseInt(sArr[1]),    //Vendorcode
                            Float.parseFloat(sArr[2]),    //Price
                            Float.parseFloat(sArr[3]),    //Amount
                            sArr[4],                      //AmountMeasure
                            Integer.parseInt(sArr[5]),    //AmountAll
                            Integer.parseInt(sArr[6])     //AmountAtStorage
                    ));
                    lineNumber++;

                    fbuf += (Float.parseFloat(sArr[2]) * Integer.parseInt(sArr[5]));
                }
            }
            br2.close();

            String s = String.valueOf(fbuf);
            if (s.contains(".")) {
                String[] sArr = s.split("\\.");
                if (sArr[1].length() >= 2) {
                    s = sArr[0] + "," + sArr[1].substring(0, 2);
                } else if (sArr[1].length() == 1) {
                    s = sArr[0] + "," + sArr[1] + "0";
                }
            }
            totalpriceTV = view.findViewById(R.id.totalprice_tv);
            totalpriceTV.setText(s);
        } catch(IOException e) {
            e.printStackTrace();
            Log.e(this.getClass().getName(), "IOException= " + e);
        }

        ImageButton closeFragmentBtn = view.findViewById(R.id.closefragment_btn);
        closeFragmentBtn.setOnClickListener(this);

        final RecyclerView cartRecyclerView = view.findViewById(R.id.cart_rv);
        cartRecyclerView.setHasFixedSize(false);
        cartRecyclerView.setMotionEventSplittingEnabled(false);
        cartRecyclerView.setAdapter(cartRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        cartRecyclerView.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        cartRecyclerView.setItemAnimator(itemAnimator);

        return view;
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.closefragment_btn) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
        }
    }

    public void calculation() {
        File sdFileCart = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + "Retail/", "корзина");
        try {
            if (cartList.size() == 0) {
                totalpriceTV.setText("0,00");
            } else {
                String str;
                float fbuf = 0;
                BufferedReader br = new BufferedReader(new FileReader(sdFileCart), 100);
                while ((str = br.readLine()) != null) {
                    String[] sArr = str.split(";");
                    fbuf += (Float.parseFloat(sArr[2]) * Integer.parseInt(sArr[5]));
                }
                br.close();

                String s = String.valueOf(fbuf);
                if (s.contains(".")) {
                    String[] sArr = s.split("\\.");
                    if (sArr[1].length() >= 2) {
                        s = sArr[0] + "," + sArr[1].substring(0, 2);
                    } else if (sArr[1].length() == 1) {
                        s = sArr[0] + "," + sArr[1] + "0";
                    }
                }
                totalpriceTV.setText(s);
            }
        } catch (IOException e) { Log.e(this.getClass().getName(),"IOException= " + e); }
    }
}
