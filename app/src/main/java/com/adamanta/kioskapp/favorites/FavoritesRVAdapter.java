package com.adamanta.kioskapp.favorites;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.adamanta.kioskapp.products.utils.Utils;
import com.adamanta.kioskapp.R;

public class FavoritesRVAdapter extends RecyclerView.Adapter<FavoritesRVAdapter.ViewHolder>{
    private final String TAG = this.getClass().getSimpleName();
    private Activity activity;

    private List<FavoritesList> favoritesList;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;

    public FavoritesRVAdapter(List<FavoritesList> cartList) { this.favoritesList = cartList; }

    @NonNull
    @Override
    public FavoritesRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favoritesrv_item,
                viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FavoritesList item = favoritesList.get(position);
        viewHolder.favoritesButtonListener.setItem(item);
        viewHolder.favoritesItemNumberTV.setText(String.valueOf(item.getName()));
    }

    @Override
    public int getItemCount() { return favoritesList.size(); }

    private void delete(FavoritesList favoritesItem) {
        int position = favoritesList.indexOf(favoritesItem);
        favoritesList.remove(position);
        notifyItemRemoved(position);

        Utils.deleteProductFromFavoritesInPosition(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView favoritesItemNumberTV;

        private favoritesButtonListener favoritesButtonListener;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            favoritesItemNumberTV = itemView.findViewById(R.id.favorites_rvitemnumber_tv);
            ImageButton favoritesItemDeleteIBtn = itemView.findViewById(R.id.favorites_rvdeleteitem_ibtn);


            favoritesButtonListener = new favoritesButtonListener();
            favoritesItemDeleteIBtn.setOnClickListener(favoritesButtonListener);
            favoritesItemNumberTV.setOnClickListener(favoritesButtonListener);
        }
    }

    private class favoritesButtonListener implements View.OnClickListener {
        private FavoritesList favoritesItem;

        private void setItem(FavoritesList favoritesList) {
            this.favoritesItem = favoritesList;
        }

        @Override
        public void onClick(@NonNull View v) {
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;

            switch (v.getId()) {
                case R.id.favorites_rvdeleteitem_ibtn:
                    delete(favoritesItem);
                    break;

                case R.id.favorites_rvitemnumber_tv:
                    if (v.getContext() instanceof Activity)
                        activity = (Activity) v.getContext();
                    try{
                        ((FavoritesSet) activity).favoritesSetCard(
                                favoritesItem.getUri(),
                                favoritesItem.getName()
                        );
                    }
                    catch (ClassCastException e) { Log.e(TAG, "ClassCastExc" + e); }
                    break;

                default:
                    break;
            }
        }

    }

}