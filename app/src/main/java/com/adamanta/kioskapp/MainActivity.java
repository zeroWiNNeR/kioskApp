package com.adamanta.kioskapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adamanta.kioskapp.favorites.FavoritesFragment;
import com.adamanta.kioskapp.favorites.FavoritesSet;
import com.adamanta.kioskapp.product.ProductsFragment;
import com.adamanta.kioskapp.productImagesFragment.ProductImagesSet;
import com.adamanta.kioskapp.products.interfaces.Postman;
import com.adamanta.kioskapp.settings.SettingsActivity;
import com.adamanta.kioskapp.shopcart.ProductsCartFragment;
import com.adamanta.kioskapp.threads.UIBarControllerThread;
import com.adamanta.kioskapp.utils.FoldersCreator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements Postman, FavoritesSet, ProductImagesSet, IMainActivity {
    private final String TAG = this.getClass().getSimpleName();
    UIBarControllerThread uiBarControllerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //checkTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "MainActivity");

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
        Context context;
        Intent intent;
        switch (v.getId()) {
            case R.id.ProductsButton:
//                context = v.getContext();
//                intent = new Intent(context, ProductsActivity.class);
//                context.startActivity(intent);
                Fragment productsFragment = ProductsFragment.newInstance(123);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainactivity_fragment_layout, productsFragment, "productsFragment");
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.FavoritesButton:
                Fragment favoritesFragment = FavoritesFragment.newInstance(123);
                FragmentTransaction ftFavoritesFragment = getSupportFragmentManager().beginTransaction();
                ftFavoritesFragment.replace(R.id.mainactivity_fragment_layout, favoritesFragment, "favoritesFragment");
                ftFavoritesFragment.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftFavoritesFragment.addToBackStack(null);
                ftFavoritesFragment.commit();
                break;

            case R.id.CartButton:
                Fragment cartFragment = ProductsCartFragment.newInstance(123);
                FragmentTransaction ftCartFragment = getSupportFragmentManager().beginTransaction();
                ftCartFragment.replace(R.id.mainactivity_fragment_layout, cartFragment, "cartFragment");
                ftCartFragment.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftCartFragment.addToBackStack(null);
                ftCartFragment.commit();
                break;

            case R.id.SettingsButton:
                context = v.getContext();
                intent = new Intent(context, SettingsActivity.class);
                context.startActivity(intent);
                break;

            default:
                break;
        }
    }
    //***************************** Buttons *********************************


    //************* UI ***********************
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

//    private void checkTheme() {
//        try {
//            File settingsFile = new File(
//                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/Retail/",
//                    "settings");
//            int numberLines = Utils.getNumOfLinesInFile(settingsFile);
//            int lineNumber = 0;
//            for (int i = 0; i < numberLines; i++) {
//                if (lineNumber == 6) {
//                    BufferedReader br = new BufferedReader(new FileReader(settingsFile), 100);
//                    String[] sArr = br.readLine().split("=");
//                    if (sArr[1].contains("green")) {
//                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//                        int theme = sp.getInt("THEME", R.style.AppTheme_greenNoActionBar);
//                        setTheme(theme);
//                    } else if (sArr[1].contains("blue")) {
//                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//                        int theme = sp.getInt("THEME", R.style.AppTheme_blueNoActionBar);
//                        setTheme(theme);
//
//                    }
//                }
//                lineNumber++;
//            }
//        } catch (IOException e) { Log.e(TAG, "Exc= " + e); }
//
//    }
    //************* UI ***********************


    @Override
    public void calculation() {
        ProductsCartFragment fragment = (ProductsCartFragment)getSupportFragmentManager()
                .findFragmentByTag("cartFragment");
        if (fragment != null) {
            fragment.calculation();
        }
    }

    @Override
    public void favoritesSetCard(String productPath, String productName) {
        FavoritesFragment fragment = (FavoritesFragment)getSupportFragmentManager()
                .findFragmentByTag("favoritesFragment");
        if (fragment != null) {
            fragment.setCard(productPath, productName);
        }
    }

    @Override
    public void productImagesSetImgV(String path, String productName) {
        FavoritesFragment fragment = (FavoritesFragment)getSupportFragmentManager()
                .findFragmentByTag("favoritesFragment");
        if (fragment != null) {
            fragment.ftr(path, productName);
        }
    }

    @Override
    public void productImagesFragmentClose() {
        FavoritesFragment fragment = (FavoritesFragment)getSupportFragmentManager()
                .findFragmentByTag("favoritesFragment");
        if (fragment != null) {
            fragment.closeProductsImagesFragment();
        }
    }

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
