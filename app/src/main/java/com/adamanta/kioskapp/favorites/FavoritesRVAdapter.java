package com.adamanta.kioskapp.favorites;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.favorites.utils.FavoritesDBHelper;
import com.adamanta.kioskapp.products.model.Product;

import java.util.List;

public class FavoritesRVAdapter extends RecyclerView.Adapter<FavoritesRVAdapter.ViewHolder>{

    private View view;
    private List<Product> favoriteProductsList;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;

    public FavoritesRVAdapter(List<Product> favoriteProductsList) { this.favoriteProductsList = favoriteProductsList; }

    @NonNull
    @Override
    public FavoritesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_favorites_rv_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Product product = favoriteProductsList.get(position);
        viewHolder.favoritesButtonListener.setItem(product);
        viewHolder.favoritesItemNumberTV.setText(String.valueOf(product.getName()));
    }

    @Override
    public int getItemCount() { return favoriteProductsList.size(); }

    private void deleteFromFavoritesList(Product product) {
        FavoritesDBHelper favoritesDBHelper = new FavoritesDBHelper(view.getContext());
        favoritesDBHelper.deleteByArticle(product.getArticle());
        int position = favoriteProductsList.indexOf(product);
        favoriteProductsList.remove(position);
        notifyItemRemoved(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView favoritesItemNumberTV;
        private final FavoritesButtonListener favoritesButtonListener;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            favoritesButtonListener = new FavoritesButtonListener();
            favoritesItemNumberTV = itemView.findViewById(R.id.fragment_favorites_rv_productname_tv);
            favoritesItemNumberTV.setOnClickListener(favoritesButtonListener);
            ImageButton favoritesItemDeleteIBtn = itemView.findViewById(R.id.fragment_favorites_rv_deleteitem_imgbtn);
            favoritesItemDeleteIBtn.setOnClickListener(favoritesButtonListener);
        }
    }

    private class FavoritesButtonListener implements View.OnClickListener {
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

            if (v.getId() == R.id.fragment_favorites_rv_deleteitem_imgbtn) {
                deleteFromFavoritesList(product);
            } else if (v.getId() == R.id.fragment_favorites_rv_productname_tv) {
                if (v.getContext() instanceof Activity) {
                    ((IFavoritesFragment) v.getContext()).favoritesFragmentSetProductCard(product);
                }
            }
        }

    }

}