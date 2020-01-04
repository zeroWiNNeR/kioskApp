package com.adamanta.kioskapp.products.fragments;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.adamanta.kioskapp.products.adapters.CartRVAdapter;
import com.adamanta.kioskapp.products.models.CartList;
import com.adamanta.kioskapp.products.utils.Utils;
import com.adamanta.kioskapp.R;

public class ProductsCartFragment extends Fragment implements View.OnClickListener {
    private View v;
    private List<CartList> cartList = new ArrayList<>();
    private final String TAG = this.getClass().getSimpleName();

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
        v = inflater.inflate(R.layout.prodactivity_cart_fragment, container, false);

        try{
            File sdFileCart = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "Retail/", "корзина");

            BufferedReader br = new BufferedReader(new FileReader(sdFileCart), 100);
            if (br.readLine() == null){ Utils.writeStringToFile(sdFileCart, " ", false); }
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
            totalpriceTV = v.findViewById(R.id.totalprice_tv);
            totalpriceTV.setText(s);
        }
        catch(IOException e) { e.printStackTrace(); Log.e(TAG, "IOException= " + e); }


        //****************************** Buttons ***********************************
        ImageButton closeFragmentBtn = v.findViewById(R.id.closefragment_btn);
        closeFragmentBtn.setOnClickListener(this);
        //****************************** Buttons ***********************************

        //****************************** Items Adapter *****************************
        final RecyclerView cartRecyclerView = v.findViewById(R.id.cart_rv);
        cartRecyclerView.setHasFixedSize(false);
        cartRecyclerView.setMotionEventSplittingEnabled(false);
        cartRecyclerView.setAdapter(cartRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        cartRecyclerView.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        cartRecyclerView.setItemAnimator(itemAnimator);
        //****************************** Items Adapter *****************************

        return v;
    }

    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()){
            case R.id.closefragment_btn:
                try{
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                } catch (NullPointerException e){ Log.e(TAG,"NullPointExc" + e); }
                break;

            default:
                break;
        }
    }

    public void calculation() {
        File sdFileCart = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + "Retail/", "корзина");
        try {
            if(cartList.size() == 0){
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
        }
        catch (IOException e) { Log.e(TAG,"IOException= " + e); }
    }
}
