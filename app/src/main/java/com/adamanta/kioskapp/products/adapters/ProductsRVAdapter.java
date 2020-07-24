package com.adamanta.kioskapp.products.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.products.ProductsActivity;
import com.adamanta.kioskapp.products.models.ProductsList;

import java.util.List;

public class ProductsRVAdapter extends RecyclerView.Adapter<ProductsRVAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();

    private List<ProductsList> products;

    private int positionNumber = 1000 ;
    private boolean isItFirstCreate = true;

    public ProductsRVAdapter(List<ProductsList> products) { this.products = products; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.prodactivity_prodrv_item,
                viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ProductsList product = products.get(position);
        viewHolder.productsButtonListener.setProduct(product);
        viewHolder.productsButton.setText(product.getName());
        if (isItFirstCreate) {
            if (position == 0) {
                //viewHolder.productsButton.setBackgroundColor(Color.parseColor("#4545fd"));
                viewHolder.productsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4545fd")));
                viewHolder.productsButton.setTextColor(Color.parseColor("#ffffff"));
            } else {
                //viewHolder.productsButton.setBackgroundColor(Color.parseColor(product.getButtonColor()));
                viewHolder.productsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(product.getButtonColor())));
                viewHolder.productsButton.setTextColor(Color.parseColor(product.getButtonTextColor()));
            }
            isItFirstCreate = false;
        }
        else {
            if (position == positionNumber) {
                //viewHolder.productsButton.setBackgroundColor(Color.parseColor("#4545fd"));
                viewHolder.productsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4545fd")));
                viewHolder.productsButton.setTextColor(Color.parseColor("#ffffff"));
            } else {
                //viewHolder.productsButton.setBackgroundColor(Color.parseColor(product.getButtonColor()));
                viewHolder.productsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(product.getButtonColor())));
                viewHolder.productsButton.setTextColor(Color.parseColor(product.getButtonTextColor()));
            }
        }
    }

    @Override
    public int getItemCount() { return products.size(); }

    public void refreshProdActivityProdRV(){
        products.clear();
        notifyDataSetChanged();
        positionNumber = 1000;
        //Log.e(TAG, "Список catRV обновлён, внесены след. записи: ");
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private Button productsButton;

        private productsButtonListener productsButtonListener;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            productsButton = itemView.findViewById(R.id.recyclerViewItemProductsButton);
            productsButtonListener = new productsButtonListener();
            productsButton.setOnClickListener(productsButtonListener);
        }
    }

    private class productsButtonListener implements View.OnClickListener {
        private ProductsList product;

        private void setProduct(ProductsList product) {
            this.product = product;
        }

        @Override
        public void onClick(View v) {
            positionNumber = product.getNumber() - 1;
            notifyDataSetChanged();
            String productName = product.getName();
            ((ProductsActivity)(v.getContext())).setProductCard(productName);
        }
    }
}
