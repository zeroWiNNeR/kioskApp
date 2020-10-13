package com.adamanta.kioskapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adamanta.kioskapp.favorites.FavoritesFragment;
import com.adamanta.kioskapp.favorites.IFavoritesFragment;
import com.adamanta.kioskapp.product.ProductsFragment;
import com.adamanta.kioskapp.product.fragments.productImagesFragment.ProductImagesFragment;
import com.adamanta.kioskapp.product.model.CategoryAndProduct;
import com.adamanta.kioskapp.product.model.Product;
import com.adamanta.kioskapp.settings.SettingsActivity;
import com.adamanta.kioskapp.shopcart.ProductsCartFragment;
import com.adamanta.kioskapp.threads.UIBarControllerThread;
import com.adamanta.kioskapp.utils.FoldersCreator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements IFavoritesFragment, IMainActivity {
    UIBarControllerThread uiBarControllerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FoldersCreator.createFolders(this);

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

    //***************************** Buttons *********************************
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.ProductsButton:
                Fragment productsFragment = ProductsFragment.newInstance(123);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainactivity_fragment_layout, productsFragment, "ProductsFragment");
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.FavoritesButton:
                Fragment favoritesFragment = FavoritesFragment.newInstance(123);
                FragmentTransaction ftFavoritesFragment = getSupportFragmentManager().beginTransaction();
                ftFavoritesFragment.replace(R.id.mainactivity_fragment_layout, favoritesFragment, "FavoritesFragment");
                ftFavoritesFragment.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftFavoritesFragment.addToBackStack(null);
                ftFavoritesFragment.commit();
                break;

            case R.id.CartButton:
                Fragment cartFragment = ProductsCartFragment.newInstance(123);
                FragmentTransaction ftCartFragment = getSupportFragmentManager().beginTransaction();
                ftCartFragment.replace(R.id.mainactivity_fragment_layout, cartFragment, "CartFragment");
                ftCartFragment.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftCartFragment.addToBackStack(null);
                ftCartFragment.commit();
                break;

            case R.id.SettingsButton:
                Context context = v.getContext();
                Intent intent = new Intent(context, SettingsActivity.class);
                context.startActivity(intent);
                break;

            default:
                break;
        }
    }
    //***************************** Buttons *********************************


    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

//    @Override
//    public void calculation() {
//        ProductsCartFragment fragment = (ProductsCartFragment)getSupportFragmentManager()
//                .findFragmentByTag("CartFragment");
//        if (fragment != null) {
//            fragment.calculation();
//        }
//    }

    @Override
    public void favoritesFragmentSetProductCard(Product product) {
        FavoritesFragment fragment = (FavoritesFragment)getSupportFragmentManager()
                .findFragmentByTag("FavoritesFragment");
        if (fragment != null) {
            fragment.setProductCard(product);
        }
    }

//    @Override
//    public void productImagesSetImgV(String path, String productName) {
//        FavoritesFragment fragment = (FavoritesFragment)getSupportFragmentManager()
//                .findFragmentByTag("FavoritesFragment");
//        if (fragment != null) {
//            fragment.ftr(path, productName);
//        }
//    }

    @Override
    public void updateUI(boolean isOnline) {
        runOnUiThread(new Runnable() {
            public void run() {
                Date currentDate = new Date();
                DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String curTime = timeFormat.format(currentDate);
                TextView currentTimeTV = findViewById(R.id.current_time_tv);
                currentTimeTV.setText(curTime);
                TextView currentOnlineStatus = findViewById(R.id.current_online_status_tv);
                if (isOnline) {
                    currentOnlineStatus.setText("ПОДКЛЮЧЕНО");
                    currentOnlineStatus.setTextColor(Color.rgb(127, 255, 0));
                } else {
                    currentOnlineStatus.setText("НЕТ СОЕДИНЕНИЯ");
                    currentOnlineStatus.setTextColor(Color.rgb(255, 0, 0));
                }
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
    public void productImagesFragmentChangeMainImage (String imageAbsolutePath) {
        ProductImagesFragment fragment = (ProductImagesFragment) getSupportFragmentManager().findFragmentByTag("ProductImagesFragment");
        if (fragment != null)
            fragment.changeMainImage(imageAbsolutePath);
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (serverSocket != null) {
//            try {
//                serverSocket.close();
//            }
//            catch (IOException e) { e.printStackTrace(); }
//        }
//    }

}
