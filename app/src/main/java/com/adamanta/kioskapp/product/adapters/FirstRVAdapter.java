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
import com.adamanta.kioskapp.product.model.Category;

import java.util.List;

public class FirstRVAdapter extends RecyclerView.Adapter<FirstRVAdapter.ViewHolder> {

    private View view;
    private List<Category> categories;
    private long pickedCategoryId = -1L;

    public FirstRVAdapter(List<Category> categories) { this.categories = categories; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.prodactivity_categrv_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Category category = categories.get(position);
        viewHolder.firstRVButtonListener.setCategory(category);
        viewHolder.firstRVItemButton.setText(category.getName());

        if (pickedCategoryId == -1L) {
            viewHolder.firstRVItemButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4284D3")));
//            viewHolder.categoriesButton.setBackgroundColor(ColorStateList.valueOf(Color.parseColor("#0C5DA5")));
            viewHolder.firstRVItemButton.setTextColor(Color.parseColor("#ffffff"));
        } else {
            if (category.getCategory().equals(pickedCategoryId)) {
                viewHolder.firstRVItemButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4545fd")));
                viewHolder.firstRVItemButton.setTextColor(Color.parseColor("#ffffff"));
            } else {
                viewHolder.firstRVItemButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4284D3")));
                viewHolder.firstRVItemButton.setTextColor(Color.parseColor("#ffffff"));
            }
        }
    }

    @Override
    public int getItemCount() { return categories.size(); }

    public void resetRVData() {
        pickedCategoryId = -1L;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Button firstRVItemButton;
        private FirstRVButtonListener firstRVButtonListener;

        private ViewHolder(View itemView) {
            super(itemView);
            firstRVButtonListener = new FirstRVButtonListener();
            firstRVItemButton = itemView.findViewById(R.id.recyclerViewItemCategoriesButton);
            firstRVItemButton.setOnClickListener(firstRVButtonListener);
        }
    }

    private class FirstRVButtonListener implements View.OnClickListener {
        private Category category;

        private void setCategory(Category category) { this.category = category; }

        @Override
        public void onClick(View v) {
            if (category != null) {
                ((IMainActivity) view.getContext()).productsFragmentResetPickedCategories();
                ((IMainActivity) view.getContext()).productsFragmentUpdateSecondRVData(category.getCategory());
                pickedCategoryId = category.getCategory();
            }
            notifyDataSetChanged();
        }

    }
}