package com.adamanta.kioskapp.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.IMainActivity;
import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.products.model.Product;

import java.util.List;

public class SearchRVAdapter extends RecyclerView.Adapter<SearchRVAdapter.ViewHolder>{

    private List<Product> productsList;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 500;

    public SearchRVAdapter(List<Product> productsList) { this.productsList = productsList; }

    @NonNull
    @Override
    public SearchRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_search_rv_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Product product = productsList.get(position);
        viewHolder.searchButtonListener.setItem(product);
        viewHolder.fullNameTV.setText(String.valueOf(product.getFullName()));
        viewHolder.priceTV.setText(String.valueOf(product.getPricePerSizeStep()));
    }

    @Override
    public int getItemCount() { return productsList.size(); }

    public void updateData(List<Product> productsList) {
      this.productsList = productsList;
      notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView fullNameTV;
        private final TextView priceTV;
        private final SearchButtonListener searchButtonListener;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            searchButtonListener = new SearchButtonListener();
            ConstraintLayout constraintLayout = itemView.findViewById(R.id.fragment_search_rv_item_constraintlayout);
            constraintLayout.setOnClickListener(searchButtonListener);
            priceTV = itemView.findViewById(R.id.fragment_search_rv_item_price_tv);
            fullNameTV = itemView.findViewById(R.id.fragment_search_rv_item_name_tv);
        }
    }

    private class SearchButtonListener implements View.OnClickListener {
        private Product product;

        private void setItem(Product product) {
          this.product = product;
        }

        @Override
        public void onClick(@NonNull View v) {
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;

            if (v.getId() == R.id.fragment_search_rv_item_constraintlayout) {
                ((IMainActivity) v.getContext()).productsFragmentOpenProductFromSearch(product);
            }

        }

    }

}