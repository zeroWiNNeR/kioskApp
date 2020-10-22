package com.adamanta.kioskapp.products.fragments.productImagesFragment;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.IMainActivity;
import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.products.utils.Utils;

import java.util.List;

public class ProductImagesRVAdapter extends RecyclerView.Adapter<ProductImagesRVAdapter.ViewHolder> {

    private List<ProductImage> productImagesList;
    private int positionNumber = 1000;
    private int prevPositionNumber = 0;
    private boolean isItFirstCreate = true;

    public ProductImagesRVAdapter(List<ProductImage> productImagesList) { this.productImagesList = productImagesList; }

    @NonNull
    @Override
    public ProductImagesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_productimages_rv_item,
                viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ProductImage image = productImagesList.get(position);
        viewHolder.productImagesButtonListener.setItem(image);

        Bitmap bitmap = Utils.getScaledBitmap(image.getImage().getAbsolutePath(), 200, 200);
        viewHolder.productImagesImgV.setImageBitmap(bitmap);

        if (isItFirstCreate) {
            if (position == 0) {
                viewHolder.productImagesImgV.setBackgroundResource(R.drawable.black_border);
            } else {
                viewHolder.productImagesImgV.setBackgroundResource(R.drawable.light_black_border);
            }
            isItFirstCreate = false;
        } else {
            if (position == positionNumber) {
                viewHolder.productImagesImgV.setBackgroundResource(R.drawable.black_border);
            } else {
                viewHolder.productImagesImgV.setBackgroundResource(R.drawable.light_black_border);
            }
        }
    }

    @Override
    public int getItemCount() { return productImagesList.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImagesImgV;
        private final productImagesButtonListener productImagesButtonListener;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImagesImgV = itemView.findViewById(R.id.fragment_productimages_rv_item_imgv);
            productImagesButtonListener = new productImagesButtonListener();
            productImagesImgV.setOnClickListener(productImagesButtonListener);
        }
    }

    private class productImagesButtonListener implements View.OnClickListener {
        private ProductImage productImage;

        private void setItem (ProductImage productImage) {
            this.productImage = productImage;
        }

        @Override
        public void onClick(@NonNull View v) {
            notifyItemChanged(prevPositionNumber);
            positionNumber = productImage.getPositionInList();
            //notifyDataSetChanged();
            notifyItemChanged(positionNumber);
            prevPositionNumber = positionNumber;

            ((IMainActivity) v.getContext()).productImagesFragmentChangeMainImage(productImage.getImage().getAbsolutePath());
        }

    }
}
