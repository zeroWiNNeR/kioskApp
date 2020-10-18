package com.adamanta.kioskapp.product.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.IMainActivity;
import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.product.model.CategoryAndProduct;

import java.util.List;

public class SecondRVAdapter extends RecyclerView.Adapter<SecondRVAdapter.ViewHolder> {

    private List<CategoryAndProduct> categoryAndProductList;
    private long pickedProductArticle = -1L;

    public SecondRVAdapter(List<CategoryAndProduct> categoryAndProductList) { this.categoryAndProductList = categoryAndProductList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.prodactivity_prodrv_item,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CategoryAndProduct categoryAndProduct = categoryAndProductList.get(position);
        viewHolder.secondRVButtonListener.setProduct(categoryAndProduct);
        viewHolder.secondRVItemButton.setText(categoryAndProduct.getName());

        if (categoryAndProduct.getType() == 'c') {
            viewHolder.secondRVItemButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4284D3")));
            viewHolder.secondRVItemButton.setTextColor(Color.parseColor("#ffffff"));
        } else if (categoryAndProduct.getType() == 'p') {
            if (categoryAndProduct.getArticle().equals(pickedProductArticle)) {
                viewHolder.secondRVItemButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#65a413")));
                viewHolder.secondRVItemButton.setTextColor(Color.parseColor("#ffffff"));
            } else {
                viewHolder.secondRVItemButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A4F43D")));
                viewHolder.secondRVItemButton.setTextColor(Color.parseColor("#ffffff"));
            }
        }
    }

    @Override
    public int getItemCount() { return categoryAndProductList.size(); }

    public void updateListData (List<CategoryAndProduct> categoryAndProductList) {
        this.categoryAndProductList = categoryAndProductList;
        pickedProductArticle = -1L;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private Button secondRVItemButton;
        private SecondRVButtonListener secondRVButtonListener;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            secondRVButtonListener = new SecondRVButtonListener();
            secondRVItemButton = itemView.findViewById(R.id.recyclerViewItemProductsButton);
            secondRVItemButton.setOnClickListener(secondRVButtonListener);
        }
    }

    private class SecondRVButtonListener implements View.OnClickListener {
        private CategoryAndProduct categoryAndProduct;

        private void setProduct(CategoryAndProduct categoryAndProduct) {
            this.categoryAndProduct = categoryAndProduct;
        }

        @Override
        public void onClick(View view) {
            if (categoryAndProduct.getType() == 'c') {
                ((IMainActivity) view.getContext()).productsFragmentUpdateSecondRVData(categoryAndProduct.getCategory());
            } else if (categoryAndProduct.getType() == 'p') {
                ((IMainActivity) view.getContext()).productsFragmentSetProductCard(categoryAndProduct);
                pickedProductArticle = categoryAndProduct.getArticle();
            }
            notifyDataSetChanged();
        }
    }
}
