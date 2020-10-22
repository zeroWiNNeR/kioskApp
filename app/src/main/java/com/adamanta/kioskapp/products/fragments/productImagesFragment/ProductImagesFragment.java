package com.adamanta.kioskapp.products.fragments.productImagesFragment;

import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.products.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductImagesFragment extends Fragment implements View.OnClickListener {

    private ImageView mainImgV;
    private final List<ProductImage> productImagesList = new ArrayList<>();

    public static ProductImagesFragment newInstance(Long article, String imagesNamesAndPositions) {
        ProductImagesFragment f = new ProductImagesFragment();
        Bundle args = new Bundle();
        args.putLong("article", article);
        args.putString("imagesNamesAndPositions", imagesNamesAndPositions);
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
        View view = inflater.inflate(R.layout.fragment_productimages, container, false);

        ImageButton closeFragmentBtn = view.findViewById(R.id.fragment_productimages_closefragment_imgb);
        closeFragmentBtn.setOnClickListener(this);
        mainImgV = view.findViewById(R.id.fragment_productimages_main_imgv);

        long article = 0L;
        String imagesNamesAndPositions = "";
        if (this.getArguments() != null) {
            article = this.getArguments().getLong("article");
            imagesNamesAndPositions = this.getArguments().getString("imagesNamesAndPositions");
        }

        String[] images = {};
        if (imagesNamesAndPositions != null) {
            images = imagesNamesAndPositions.split(";");
        }

        for (int i=0; i<images.length; i++) {
            String imageName = images[i].split("=")[0];
            File imageFile = new File(view.getContext().getFilesDir().getAbsolutePath()+"/images/products/"+article+"/"+imageName);
            productImagesList.add(new ProductImage(imageFile, i));
            if (i==0) {
                Bitmap bitmap = Utils.getScaledBitmap(imageFile.getAbsolutePath(), 800,800);
                mainImgV.setImageBitmap(bitmap);
            }
        }

        ProductImagesRVAdapter productImagesRVAdapter = new ProductImagesRVAdapter(productImagesList);
        final RecyclerView productImagesRecyclerView = view.findViewById(R.id.fragment_productimages_rv);
        productImagesRecyclerView.setHasFixedSize(false);
        productImagesRecyclerView.setAdapter(productImagesRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        productImagesRecyclerView.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        productImagesRecyclerView.setItemAnimator(itemAnimator);

        return view;
    }

    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.fragment_productimages_closefragment_imgb) {
            if (getActivity() != null)
                getActivity().getSupportFragmentManager().beginTransaction().remove(ProductImagesFragment.this).commit();
        }
    }

    public void changeMainImage(String imageAbsolutePath) {
        mainImgV.setImageBitmap(null);
        Bitmap bitmap = Utils.getScaledBitmap(imageAbsolutePath, 800,800);
        mainImgV.setImageBitmap(bitmap);
    }

}
