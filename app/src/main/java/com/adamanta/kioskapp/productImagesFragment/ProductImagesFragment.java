package com.adamanta.kioskapp.productImagesFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.adamanta.kioskapp.products.utils.Utils;
import com.adamanta.kioskapp.R;

public class ProductImagesFragment extends Fragment implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    private ImageView mainImgV;

    private List<ProductImagesList> productImagesList = new ArrayList<>();
    private ProductImagesRVAdapter productImagesRVAdapter = new ProductImagesRVAdapter(productImagesList);

    public static ProductImagesFragment newInstance(String PathFilename) {
        ProductImagesFragment f = new ProductImagesFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("PathFilename", PathFilename);
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
        View v = inflater.inflate(R.layout.productimages_fragment, container, false);

        //****************************** Views ***********************************
        ImageButton closeFragmentBtn = v.findViewById(R.id.productimages_closefragment_imgb);
        closeFragmentBtn.setOnClickListener(this);

        mainImgV = v.findViewById(R.id.productimages_main_imgv);
        //****************************** Views ***********************************

        try {
            String sbuf = this.getArguments().getString("PathFilename");

            String[] sArr = sbuf.split(";");
            File sdFile = new File(sArr[0]);
            File[] filesArray = sdFile.listFiles();
            int NumberInList = 0;
            for (File f : filesArray) {
                if (f.getName().contains(sArr[1]+"_")) {
                    productImagesList.add(new ProductImagesList(sArr[0], f.getName(), NumberInList));
                    NumberInList++;
                }
            }

            File File = new File(sArr[0], sArr[1]+"_1.webp");
            Bitmap bitmap = Utils.getScaledBitmap(File.getAbsolutePath(), 800,800);
            mainImgV.setImageBitmap(bitmap);

        }
        catch (NullPointerException e) { Log.e(TAG, "NPExc= " + e); }


        //****************************** ProductImages Adapter *****************************
        final RecyclerView productImagesRecyclerView = v.findViewById(R.id.productimages_rv);
        productImagesRecyclerView.setHasFixedSize(false);
        productImagesRecyclerView.setAdapter(productImagesRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        productImagesRecyclerView.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        productImagesRecyclerView.setItemAnimator(itemAnimator);
        //****************************** ProductImages Adapter *****************************

        return v;
    }

    public void onClick(@NonNull View v) {
        switch (v.getId()){
            case R.id.productimages_closefragment_imgb:
                /*try{
                    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                } catch (NullPointerException e){ Log.e(TAG,"NullPointExc" + e); }
                 */
                try{
                    ((ProductImagesSet) getActivity()).productImagesFragmentClose();
                }
                catch (ClassCastException e) { Log.e(TAG, "ClassCastExc" + e); }
                break;

            default:
                break;
        }
    }

    public void setImageView(String path, String productName) {
        File sdFile = new File(path, productName);
        mainImgV.setImageBitmap(null);
        Bitmap bitmap = Utils.getScaledBitmap(sdFile.getAbsolutePath(), 800,800);
        mainImgV.setImageBitmap(bitmap);
    }

}
