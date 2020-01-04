package com.adamanta.kioskapp.products.adapters;
import android.app.Activity;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.adamanta.kioskapp.products.interfaces.Postman;
import com.adamanta.kioskapp.products.models.CartList;
import com.adamanta.kioskapp.products.utils.Utils;
import com.adamanta.kioskapp.R;

public class CartRVAdapter extends RecyclerView.Adapter<CartRVAdapter.ViewHolder> implements Postman {
    private final String TAG = this.getClass().getSimpleName();
    private Activity activity;
    View v;

    private List<CartList> cartList;
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;

    public CartRVAdapter(List<CartList> cartList) { this.cartList = cartList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.prodactivity_cartrv_item,
                viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CartList item = cartList.get(position);
        viewHolder.cartitemButtonListener.setItem(item);
        viewHolder.itemNumberTV.setText(String.valueOf(item.getNumber()));
        viewHolder.itemNameTV.setText(item.getName());
        if (item.getAmountAtStorage() == 0) {
            viewHolder.amountAtStorageIV.setBackgroundColor(Color.parseColor("#EE5C5C"));
        } else if (item.getAmountAtStorage() == 1) {
            viewHolder.amountAtStorageIV.setBackgroundColor(Color.parseColor("#FDE446"));
        } else if (item.getAmountAtStorage() == 2 ){
            viewHolder.amountAtStorageIV.setBackgroundColor(Color.parseColor("#58FF47"));
        }

        String s = String.valueOf(item.getAmount());
        if (s.contains(".")){
            String[] sArr = s.split("\\.");
            if (sArr[1].equals("0") || sArr[1].equals("00")) {
                s = sArr[0];
            } else s = s;
            viewHolder.itemCountTV.setText(String.valueOf(s));
            viewHolder.itemCountMeasureTV.setText(String.valueOf(item.getAmountMeasure() + "."));
        } else {
            viewHolder.itemCountTV.setText(String.valueOf(item.getAmount()));
            viewHolder.itemCountMeasureTV.setText(String.valueOf(item.getAmountMeasure() + "."));
        }

        float f = item.getAmountAll()*item.getPrice();
        viewHolder.itemPriceTV.setText(String.valueOf(String.format("%.2f", f)));
    }

    @Override
    public int getItemCount() { return cartList.size(); }

    private void changeAmount(CartList item, char action){
        int position = cartList.indexOf(item);
        try {
            File sdFileCart = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "Retail/", "корзина");
            int numOfLines = Utils.getNumOfLinesInFile(sdFileCart);
            //каждая строка файла - отдельный элемент в массиве
            String[] sArr = new String[numOfLines];
            BufferedReader br2 = new BufferedReader(new FileReader(sdFileCart), 100);
            for (int i=0; i<numOfLines; i++){ sArr[i] = br2.readLine(); }
            br2.close();

            String[] sArrBuf = sArr[position].split(";");
            if (!sArrBuf[3].contains(".")) {
                int iBuf = Integer.parseInt(sArrBuf[3]) / Integer.parseInt(sArrBuf[5]);
                if (action == '+'){
                    sArrBuf[3] = String.valueOf(Integer.parseInt(sArrBuf[3]) + iBuf);
                    sArrBuf[5] = String.valueOf(Integer.parseInt(sArrBuf[5]) + 1);

                    item.setAmount(Integer.parseInt(sArrBuf[3]));
                    item.setAmountAll(item.getAmountAll() + 1);
                } else if (action == '-'){
                    if (item.getAmountAll() > 1){
                        sArrBuf[3] =String.valueOf(Integer.parseInt(sArrBuf[3]) - iBuf);
                        sArrBuf[5] = String.valueOf(Integer.parseInt(sArrBuf[5]) - 1);

                        item.setAmount(Integer.parseInt(sArrBuf[3]));
                        item.setAmountAll(item.getAmountAll() - 1);
                    }
                }
            }
            else if (sArrBuf[3].contains(".")) {
                float fBuf = Float.parseFloat(sArrBuf[3]) / Float.parseFloat(sArrBuf[5]);
                if (action == '+'){
                    sArrBuf[3] = Utils.formatFloatToString(Float.parseFloat(sArrBuf[3]) + fBuf, 1);
                    sArrBuf[5] = String.valueOf(Integer.parseInt(sArrBuf[5]) + 1);

                    item.setAmount(Float.parseFloat(sArrBuf[3]));
                    item.setAmountAll(item.getAmountAll() + 1);
                } else if (action == '-'){
                    if (item.getAmountAll() > 1){
                        sArrBuf[3] = Utils.formatFloatToString(Float.parseFloat(sArrBuf[3]) - fBuf, 1);
                        sArrBuf[5] = String.valueOf(Integer.parseInt(sArrBuf[5]) - 1);

                        item.setAmount(Float.parseFloat(sArrBuf[3]));
                        item.setAmountAll(item.getAmountAll() - 1);
                    }
                }
            }

            String sArrBufString = sArrBuf[0] + ";" + sArrBuf[1] + ";" + sArrBuf[2] + ";"
                    + sArrBuf[3] + ";" + sArrBuf[4] + ";" + sArrBuf[5] + ";" + sArrBuf[6];
            sArr[position] = sArrBufString;
            if (numOfLines == 1){ Utils.writeStringToFile(sdFileCart, sArrBufString, false);
            } else { Utils.writeStringsToFile(sdFileCart, sArr, false);
            }
        }
        catch(IOException e){e.printStackTrace();Log.e(TAG,"brException="+e);}
        notifyItemChanged(position);
    }

    private void delete(CartList item) {
        int position = cartList.indexOf(item);
        cartList.remove(position);
        notifyItemRemoved(position);

        Utils.deleteProductFromCartFile(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView itemNumberTV;
        private TextView itemNameTV;
        private ImageView amountAtStorageIV;
        private TextView itemCountTV;
        private TextView itemCountMeasureTV;
        private TextView itemPriceTV;

        private cartitemButtonListener cartitemButtonListener;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNumberTV = itemView.findViewById(R.id.itemnumber_tv);
            itemNameTV = itemView.findViewById(R.id.itemname_tv);
            amountAtStorageIV = itemView.findViewById(R.id.amountatstor_iv);
            ImageButton itemRemoveIBtn = itemView.findViewById(R.id.itemremove_ibtn);
            itemCountTV = itemView.findViewById(R.id.itemcount_tv);
            itemCountMeasureTV = itemView.findViewById(R.id.itemcountmeasure_tv);
            ImageButton itemAddIBtn = itemView.findViewById(R.id.itemadd_ibtn);
            itemPriceTV = itemView.findViewById(R.id.itemprice_tv);
            ImageButton deleteItemIBtn = itemView.findViewById(R.id.deleteitem_ibtn);

            cartitemButtonListener = new cartitemButtonListener();
            itemRemoveIBtn.setOnClickListener(cartitemButtonListener);
            itemAddIBtn.setOnClickListener(cartitemButtonListener);
            deleteItemIBtn.setOnClickListener(cartitemButtonListener);
        }
    }

    private class cartitemButtonListener implements View.OnClickListener {
        private CartList item;

        private void setItem(CartList cartList) {
            this.item = cartList;
        }

        @Override
        public void onClick(@NonNull View v) {
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;

            switch (v.getId()) {
                case R.id.itemremove_ibtn:
                    changeAmount(item, '-');
                    if (v.getContext() instanceof Activity)
                        activity = (Activity) v.getContext();
                    try {
                        ((Postman) activity).calculation();
                    } catch (ClassCastException e) {
                        Log.e(TAG, "ClassCastExc" + e);
                    }
                    break;

                case R.id.itemadd_ibtn:
                    changeAmount(item, '+');
                    if (v.getContext() instanceof Activity)
                        activity = (Activity) v.getContext();
                    try {
                        ((Postman) activity).calculation();
                    } catch (ClassCastException e) {
                        Log.e(TAG, "ClassCastExc" + e);
                    }
                    break;

                case R.id.deleteitem_ibtn:
                    delete(item);
                    if (v.getContext() instanceof Activity)
                        activity = (Activity) v.getContext();
                    try {
                        ((Postman) activity).calculation();
                    } catch (ClassCastException e) {
                        Log.e(TAG, "ClassCastExc" + e);
                    }
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    public void calculation(){
    }
}
