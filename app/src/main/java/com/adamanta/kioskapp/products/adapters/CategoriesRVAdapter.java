package com.adamanta.kioskapp.products.adapters;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.adamanta.kioskapp.products.ProductsActivity;
import com.adamanta.kioskapp.products.models.ProductsList;
import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.settings.AppSettings;
import com.adamanta.kioskapp.products.models.CategoriesList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoriesRVAdapter extends RecyclerView.Adapter<CategoriesRVAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private AppSettings appSettings = new AppSettings();

    private List<CategoriesList> categories;
    private List<ProductsList> products = new ArrayList<>();

    private int categorylevel = 0;
    private int positionNumber =1000 ;

    public CategoriesRVAdapter(List<CategoriesList> categories) { this.categories = categories; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.prodactivity_categrv_item,
                viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CategoriesList category = categories.get(position);
        viewHolder.categoriesButtonListener.setCategory(category);
        //Log.e(TAG, "Кнопка catRV: " + appSettings.getDIR_SD() + "/" + category.getName());
        viewHolder.categoriesButton.setText(category.getName());
        if (position == positionNumber){
            viewHolder.categoriesButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4545fd")));
            //viewHolder.categoriesButton.setBackgroundColor(ColorStateList.valueOf(Color.parseColor(category.getButtonColor())));
            viewHolder.categoriesButton.setTextColor(Color.parseColor("#ffffff"));
        }
        else{
            viewHolder.categoriesButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(category.getButtonColor())));
            //viewHolder.categoriesButton.setBackgroundColor(ColorStateList.valueOf(Color.parseColor(category.getButtonColor())));
            viewHolder.categoriesButton.setTextColor(Color.parseColor(category.getButtonTextColor()));
        }
    }

    @Override
    public int getItemCount() { return categories.size(); }

    public void refreshProdActivityCatRV(){
        categories.clear();
        notifyDataSetChanged();
        positionNumber = 1000;
        //Log.e(TAG, "Список catRV обновлён, внесены след. записи: ");
    }

    public String prevFolderPath(int categorylevel){
        String folder = appSettings.getDIR_SD();
        int pep = folder.lastIndexOf('/');
        if (categorylevel > 0 ){ folder = folder.substring(0, pep); }
        appSettings.setDIR_SD(folder);
        return folder;
    }

    public int categoryLevel(int num){
        categorylevel += num;
        /*if(num == 1){Log.e(TAG, "categorylevel увеличился= " + categorylevel); }
        else if(num == -1){ Log.e(TAG, "categorylevel уменьшился= " + categorylevel); }*/
        if(categorylevel ==0){
            notifyDataSetChanged();
        }
        return categorylevel;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Button categoriesButton;
        private categoriesButtonListener categoriesButtonListener;

        private ViewHolder(View itemView) {
            super(itemView);
            categoriesButton = itemView.findViewById(R.id.recyclerViewItemCategoriesButton);
            categoriesButtonListener = new categoriesButtonListener();
            categoriesButton.setOnClickListener(categoriesButtonListener);
        }
    }

    private class categoriesButtonListener implements View.OnClickListener {
        private CategoriesList category;

        private void setCategory(CategoriesList category) { this.category = category; }

        @Override
        public void onClick(View v) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.e(TAG, "Память не доступна: " + Environment.getExternalStorageState());
                return;
            }
            File sdPath = Environment.getExternalStorageDirectory();
            sdPath = new File(sdPath.getAbsolutePath() + "/" + appSettings.getDIR_SD()
                    + "/" + category.getName());
            File sdFile = new File(sdPath, "содержание");
            try {
                BufferedReader br = new BufferedReader(new FileReader(sdFile), 100);
                String categoryName, buttonColor, textColor;
                categories.clear();
                products.clear();
                String dir = appSettings.getDIR_SD() + "/" + category.getName();
                appSettings.setDIR_SD(dir);
                int number = 0;
                String str = br.readLine();
                if(str != null) {
                    if (str.contains("Каталоги")) {
                        //Log.e(TAG,"Считывание файла с категориями происходит из: "+appSettings.getDIR_SD());
                        while ((str = br.readLine()) != null) {
                            String [] buf = str.split(";");
                            categoryName = buf[0];
                            buttonColor = buf[1];
                            textColor = buf[2];
                            number++;
                            categories.add(new CategoriesList(number, categoryName, buttonColor, textColor));
                        }
                        ((ProductsActivity) (v.getContext())).productsActivityMainImgVAction("show");
                        ((ProductsActivity) (v.getContext())).clearProductCard();
                        positionNumber = 1000;
                        //Log.e(TAG, "Эти записи внесены в список адаптера, список catRV обновлён");
                    }
                    else if (str.contains("Товары")) {
                        //Log.e(TAG,"Считывание файла с товарами происходит из: "+appSettings.getDIR_SD());
                        while ((str = br.readLine()) != null) {
                            String [] buf = str.split(";");
                            categoryName = buf[0];
                            buttonColor = buf[1];
                            textColor = buf[2];
                            number++;
                            products.add(new ProductsList(number, categoryName, buttonColor, textColor));
                        }
                        ((ProductsActivity) (v.getContext())).backAction(v, dir);
                        ((ProductsActivity) (v.getContext())).productsColumnImgVAction("show");

                        ProductsList product = products.get(0);
                        ((ProductsActivity)(v.getContext())).setProductCard(product.getName());
                        ((ProductsActivity) (v.getContext())).productsActivityMainImgVAction("hide");
                        positionNumber = category.getNumber() - 1;
                        //Log.e(TAG, "Создан список RV ProductsRVAdapter со списком товаров");
                    }
                    else { Log.e(TAG, "Тип файлов в файле 'содержание' не обнаружен"); }
                }
                ((ProductsActivity)(v.getContext())).createProductsRVAdapter(products);
                br.close();
                notifyDataSetChanged();
                categoryLevel(1);
            }
            catch(FileNotFoundException e){e.printStackTrace();Log.e(TAG,"FileNotFoundEx="+e);}
            catch (IOException e) { e.printStackTrace(); Log.e(TAG, "IOException= " + e); }
        }

    }
}