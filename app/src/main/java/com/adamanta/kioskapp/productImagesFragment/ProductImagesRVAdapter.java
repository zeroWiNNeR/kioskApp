package com.adamanta.kioskapp.productImagesFragment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import com.adamanta.kioskapp.products.utils.Utils;
import com.adamanta.kioskapp.R;

public class ProductImagesRVAdapter extends RecyclerView.Adapter<ProductImagesRVAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private Activity activity;

    private List<ProductImagesList> productImagesList;

    private int positionNumber = 1000;
    private int prevpositionNumber = 0;
    private boolean isItFirstCreate = true;

    public ProductImagesRVAdapter(List<ProductImagesList> productImagesList) { this.productImagesList = productImagesList; }

    @NonNull
    @Override
    public ProductImagesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.productimages_rv_item,
                viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ProductImagesList item = productImagesList.get(position);
        viewHolder.productImagesButtonListener.setItem(item);

        File sdFile = new File(item.getPath(), item.getProductName());
        Bitmap bitmap = Utils.getScaledBitmap(sdFile.getAbsolutePath(), 200, 200);
        viewHolder.productImagesImgV.setImageBitmap(bitmap);

        if (isItFirstCreate) {
            if (position == 0) {
                viewHolder.productImagesImgV.setBackgroundResource(R.drawable.black_border);
            } else {
                viewHolder.productImagesImgV.setBackgroundResource(R.drawable.light_black_border);
            }
            isItFirstCreate = false;
        }
        else {
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
        private ImageView productImagesImgV;

        private productImagesButtonListener productImagesButtonListener;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImagesImgV = itemView.findViewById(R.id.productimages_rvitem_imgv);

            productImagesButtonListener = new productImagesButtonListener();
            productImagesImgV.setOnClickListener(productImagesButtonListener);
        }
    }

    private class productImagesButtonListener implements View.OnClickListener {
        private ProductImagesList productImagesItem;

        private void setItem(ProductImagesList productImagesList) {
            this.productImagesItem = productImagesList;
        }

        @Override
        public void onClick(@NonNull View v) {
            notifyItemChanged(prevpositionNumber);
            positionNumber = productImagesItem.getNumberInList();
            //notifyDataSetChanged();
            notifyItemChanged(positionNumber);
            prevpositionNumber = positionNumber;

            if (v.getContext() instanceof Activity)
                activity = (Activity) v.getContext();
            try{
                ((ProductImagesSet) activity).productImagesSetImgV(
                        productImagesItem.getPath(),
                        productImagesItem.getProductName()
                );
            }
            catch (ClassCastException e) { Log.e(TAG, "ClassCastExc" + e); }
        }

    }
}
