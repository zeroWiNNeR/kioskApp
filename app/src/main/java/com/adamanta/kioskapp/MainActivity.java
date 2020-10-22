package com.adamanta.kioskapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adamanta.kioskapp.favorites.FavoritesFragment;
import com.adamanta.kioskapp.favorites.IFavoritesFragment;
import com.adamanta.kioskapp.products.ProductsFragment;
import com.adamanta.kioskapp.products.fragments.productImagesFragment.ProductImagesFragment;
import com.adamanta.kioskapp.products.model.CategoryAndProduct;
import com.adamanta.kioskapp.products.model.Product;
import com.adamanta.kioskapp.search.SearchFragment;
import com.adamanta.kioskapp.settings.SettingsActivity;
import com.adamanta.kioskapp.shopcart.ShopCartFragment;
import com.adamanta.kioskapp.threads.UIBarControllerThread;
import com.adamanta.kioskapp.utils.FoldersCreator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements IFavoritesFragment, IMainActivity {

    private ProductsFragment productsFragment;
    private SearchFragment searchFragment;
    private boolean isSearchFragmentOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText searchET = findViewById(R.id.activity_main_search_et);
        searchET.setOnClickListener(this::onClick);
        searchET.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            // при изменении текста выполняем фильтрацию
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchFragment != null)
                    searchFragment.searchInDb(s.toString());
            }
        });

        FoldersCreator.createFolders(this);

        UIBarControllerThread uiBarControllerThread;

        boolean isAliveUIBarControllerThread = false;
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet) {
            if (t.getThreadGroup() == Thread.currentThread().getThreadGroup()) {
                System.out.println("Thread: "+t+" : "+"state: "+t.getState());
            }
            if (t.getName().equals("UIBarControllerThread")) {
                isAliveUIBarControllerThread = true;
                uiBarControllerThread = (UIBarControllerThread) t;
                uiBarControllerThread.link(this);
                return;
            }
        }

        if (!isAliveUIBarControllerThread) {
            uiBarControllerThread = (UIBarControllerThread) getLastNonConfigurationInstance();
            if (uiBarControllerThread == null) {
                uiBarControllerThread = new UIBarControllerThread();
                uiBarControllerThread.setName("UIBarControllerThread");
                uiBarControllerThread.setPriority(2);
                uiBarControllerThread.link(this);
                uiBarControllerThread.start();
            }
        }
    }

    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.activity_main_products_btn) {
            productsFragment = ProductsFragment.newInstance(0L, 0L);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activity_main_fragment_layout, productsFragment, "ProductsFragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();

        } else if (v.getId() == R.id.activity_main_favorites_btn) {
            Fragment favoritesFragment = FavoritesFragment.newInstance(123);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activity_main_fragment_layout, favoritesFragment, "FavoritesFragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();

        } else if (v.getId() == R.id.activity_main_shopcart_btn) {
            Fragment cartFragment = ShopCartFragment.newInstance(123);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activity_main_fragment_layout, cartFragment, "ShopCartFragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();

        } else if (v.getId() == R.id.activity_main_settings_btn) {
            Context context = v.getContext();
            Intent intent = new Intent(context, SettingsActivity.class);
            context.startActivity(intent);

        } else if (v.getId() == R.id.activity_main_search_et) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            searchFragment = SearchFragment.newInstance(123);
            ft.replace(R.id.activity_main_fragment_layout, searchFragment, "SearchFragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
            ImageView searchIcon = findViewById(R.id.activity_main_searchicon_imgv);
            searchIcon.setImageResource(R.drawable.ic_close);
            searchIcon.setOnClickListener(this::onClick);
            EditText searchET = findViewById(R.id.activity_main_search_et);
            searchET.setCursorVisible(true);

        } else if (v.getId() == R.id.activity_main_searchicon_imgv) {
            getSupportFragmentManager().popBackStack();
            ImageView searchIcon = findViewById(R.id.activity_main_searchicon_imgv);
            searchIcon.setImageResource(R.drawable.ic_baseline_search_24);
            searchIcon.setOnClickListener(null);
            EditText searchET = findViewById(R.id.activity_main_search_et);
            searchET.setCursorVisible(false);
        }
    }

    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if(!hasFocus) {
//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);
//        }
    }


    @Override
    public void favoritesFragmentSetProductCard(Product product) {
        FavoritesFragment fragment = (FavoritesFragment)getSupportFragmentManager()
                .findFragmentByTag("FavoritesFragment");
        if (fragment != null) {
            fragment.setProductCard(product);
        }
    }

    @Override
    public void updateUI(boolean isOnline) {
        runOnUiThread(() -> {
            Date currentDate = new Date();
            DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String curTime = timeFormat.format(currentDate);
            TextView currentTimeTV = findViewById(R.id.activity_main_current_time_tv);
            currentTimeTV.setText(curTime);
            TextView currentOnlineStatus = findViewById(R.id.activity_main_current_online_status_tv);
            if (isOnline) {
                currentOnlineStatus.setText("ПОДКЛЮЧЕНО");
                currentOnlineStatus.setTextColor(Color.rgb(127, 255, 0));
            } else {
                currentOnlineStatus.setText("НЕТ СОЕДИНЕНИЯ");
                currentOnlineStatus.setTextColor(Color.rgb(255, 0, 0));
            }
        });
    }

    @Override
    public void productsFragmentUpdateSecondRVData(long parentCategory) {
        ProductsFragment fragment = (ProductsFragment) getSupportFragmentManager().findFragmentByTag("ProductsFragment");
        if (fragment != null)
            fragment.updateSecondRVData(parentCategory);
    }
    @Override
    public void productsFragmentResetPickedCategories() {
        ProductsFragment fragment = (ProductsFragment) getSupportFragmentManager().findFragmentByTag("ProductsFragment");
        if (fragment != null)
            fragment.resetPickedCategories();
    }
    @Override
    public void productsFragmentSetProductCard (CategoryAndProduct product) {
        ProductsFragment fragment = (ProductsFragment) getSupportFragmentManager().findFragmentByTag("ProductsFragment");
        if (fragment != null)
            fragment.setProductCard(product);
    }
    @Override
    public void productsFragmentOpenProductFromSearch(Product product) {
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ImageView searchIcon = findViewById(R.id.activity_main_searchicon_imgv);
        searchIcon.setImageResource(R.drawable.ic_baseline_search_24);
        searchIcon.setOnClickListener(null);
        EditText searchET = findViewById(R.id.activity_main_search_et);
        searchET.setText(null);
        searchET.setCursorVisible(false);
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (searchFragment != null) {
            ft.remove(searchFragment);
            searchFragment = null;
        }
        productsFragment = ProductsFragment.newInstance(product.getArticle(), product.getParentCategory());
        ft.replace(R.id.activity_main_fragment_layout, productsFragment, "ProductsFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void productImagesFragmentChangeMainImage (String imageAbsolutePath) {
        ProductImagesFragment fragment = (ProductImagesFragment) getSupportFragmentManager().findFragmentByTag("ProductImagesFragment");
        if (fragment != null)
            fragment.changeMainImage(imageAbsolutePath);
    }

    @Override
    public void shopCartFragmentCalculationTotalPrice() {
        ShopCartFragment fragment = (ShopCartFragment) getSupportFragmentManager().findFragmentByTag("ShopCartFragment");
        if (fragment != null)
            fragment.calculation();
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }




//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (serverSocket != null) {
//            try {
//                serverSocket.close();
//            } catch (IOException e) { e.printStackTrace(); }
//        }
//    }

}
