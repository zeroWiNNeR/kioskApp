package com.adamanta.kioskapp.shopcart;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.IMainActivity;
import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.shopcart.model.ShopCartProduct;
import com.adamanta.kioskapp.shopcart.utils.ShopCartDBHelper;

import java.math.BigDecimal;
import java.util.List;

public class ShopCartRVAdapter extends RecyclerView.Adapter<ShopCartRVAdapter.ViewHolder> {

    private View view;
    private List<ShopCartProduct> shopCartProductList;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;

    public ShopCartRVAdapter(List<ShopCartProduct> shopCartProductList) { this.shopCartProductList = shopCartProductList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_shopcart_rv_item,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ShopCartProduct shopCartProduct = shopCartProductList.get(position);
        viewHolder.cartProductButtonListener.setShopCartProduct(shopCartProduct);
        viewHolder.productNameTV.setText(shopCartProduct.getName());
        if (shopCartProduct.getStockQuantity().equals(BigDecimal.ZERO)) {
            viewHolder.productAmountAtStorageImgV.setBackgroundColor(Color.parseColor("#EE5C5C"));
        } else if (shopCartProduct.getStockQuantity().compareTo(new BigDecimal(10L)) <= 0) {
            viewHolder.productAmountAtStorageImgV.setBackgroundColor(Color.parseColor("#FDE446"));
        } else if (shopCartProduct.getStockQuantity().compareTo(new BigDecimal(10L)) >= 0){
            viewHolder.productAmountAtStorageImgV.setBackgroundColor(Color.parseColor("#58FF47"));
        }

        BigDecimal productCurrentPrice = shopCartProduct.getAllCount().divide(shopCartProduct.getSizeStep()).multiply(shopCartProduct.getPricePerSizeStep());
        viewHolder.productPriceTV.setText(String.valueOf(productCurrentPrice));

        if (shopCartProduct.getAllCount().stripTrailingZeros().scale() <= 0) {
            viewHolder.productCountTV.setText(String.valueOf(shopCartProduct.getAllCount().toBigIntegerExact()));
        } else {
            viewHolder.productCountTV.setText(String.valueOf(shopCartProduct.getAllCount()));
        }
        viewHolder.productCountMeasureTV.setText(shopCartProduct.getSizeType());
    }

    @Override
    public int getItemCount() { return shopCartProductList.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameTV;
        private final ImageView productAmountAtStorageImgV;
        private final TextView productCountTV;
        private final TextView productCountMeasureTV;
        private final TextView productPriceTV;
        private final CartProductButtonListener cartProductButtonListener;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartProductButtonListener = new CartProductButtonListener();
            productNameTV = itemView.findViewById(R.id.fragment_shopcart_rv_name_tv);
            productAmountAtStorageImgV = itemView.findViewById(R.id.fragment_shopcart_rv_amountatstore_imgv);
            ImageButton itemRemoveIBtn = itemView.findViewById(R.id.fragment_shopcart_rv_minus_imgbtn);
            itemRemoveIBtn.setOnClickListener(cartProductButtonListener);
            productCountTV = itemView.findViewById(R.id.fragment_shopcart_rv_count_tv);
            productCountMeasureTV = itemView.findViewById(R.id.fragment_shopcart_rv_itemcountmeasure_tv);
            ImageButton itemAddIBtn = itemView.findViewById(R.id.fragment_shopcart_rv_plus_imgbtn);
            itemAddIBtn.setOnClickListener(cartProductButtonListener);
            productPriceTV = itemView.findViewById(R.id.fragment_shopcart_rv_price_tv);
            ImageButton deleteItemIBtn = itemView.findViewById(R.id.fragment_shopcart_rv_delete_imgbtn);
            deleteItemIBtn.setOnClickListener(cartProductButtonListener);
        }
    }

    private class CartProductButtonListener implements View.OnClickListener {
        private ShopCartProduct shopCartProduct;

        private void setShopCartProduct(ShopCartProduct shopCartProduct) {
            this.shopCartProduct = shopCartProduct;
        }

        @Override
        public void onClick(@NonNull View v) {
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;

            if (v.getId() == R.id.fragment_shopcart_rv_minus_imgbtn) {
                BigDecimal currentCountAll = shopCartProduct.getAllCount();
                currentCountAll = currentCountAll.subtract(shopCartProduct.getSizeStep());
                if (currentCountAll.compareTo(shopCartProduct.getMinSize()) >= 0) {
                    ShopCartDBHelper shopCartDBHelper = new ShopCartDBHelper(view.getContext());
                    shopCartDBHelper.minusOneProductSizeStepFromAllCount(shopCartProduct);

                    shopCartProduct.setAllCount(shopCartProduct.getAllCount().subtract(shopCartProduct.getSizeStep()));
                    int position = shopCartProductList.indexOf(shopCartProduct);
                    notifyItemChanged(position);
                }
            } else if (v.getId() == R.id.fragment_shopcart_rv_plus_imgbtn) {
                BigDecimal currentCountAll = shopCartProduct.getAllCount();
                currentCountAll = currentCountAll.add(shopCartProduct.getSizeStep());
                if (currentCountAll.compareTo(shopCartProduct.getMaxSize()) <= 0) {
                    ShopCartDBHelper shopCartDBHelper = new ShopCartDBHelper(view.getContext());
                    shopCartDBHelper.addAdditionalCountToShopCartProduct(shopCartProduct, shopCartProduct.getSizeStep());

                    shopCartProduct.setAllCount(shopCartProduct.getAllCount().add(shopCartProduct.getSizeStep()));
                    int position = shopCartProductList.indexOf(shopCartProduct);
                    notifyItemChanged(position);
                }
            } else if (v.getId() == R.id.fragment_shopcart_rv_delete_imgbtn) {
                ShopCartDBHelper shopCartDBHelper = new ShopCartDBHelper(view.getContext());
                shopCartDBHelper.deleteByArticle(shopCartProduct.getArticle());

                int position = shopCartProductList.indexOf(shopCartProduct);
                shopCartProductList.remove(position);
                notifyItemChanged(position);
            }

            ((IMainActivity) view.getContext()).shopCartFragmentCalculationTotalPrice();
        }

    }


}
