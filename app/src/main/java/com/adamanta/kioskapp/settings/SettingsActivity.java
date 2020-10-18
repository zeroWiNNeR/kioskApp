package com.adamanta.kioskapp.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.adamanta.kioskapp.MainActivity;
import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.settings.threads.ChangesDownloadTask;
import com.adamanta.kioskapp.settings.threads.TabletRegistrationTask;
import com.adamanta.kioskapp.settings.utils.SettingsDBHelper;

import java.io.File;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements ISettingsActivity {

    private SettingsDBHelper settingsDBHelper;
    private EditText contractIdET, cityET, streetET, houseET, apartmentET;
    private ImageButton saveButton;
    private String imei = "";
    private String androidId = "";
    private String contractId = "";
    private String city = "";
    private String street = "";
    private String house = "";
    private String apartment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        contractIdET = findViewById(R.id.activity_settings_contractid_et);
        cityET = findViewById(R.id.activity_settings_city_et);
        streetET = findViewById(R.id.activity_settings_street_et);
        houseET = findViewById(R.id.activity_settings_house_et);
        apartmentET = findViewById(R.id.activity_settings_apartment_et);
        saveButton = findViewById(R.id.activity_settings_save_imgbtn);

        settingsDBHelper = new SettingsDBHelper(this);

        checkRegistration();
//        Log.e(TAG, "mac: " + getMacAddr());
//        Log.e(TAG, "imei: " + getImeiAddr());
//        Log.e(TAG, "androidId: " + getDeviceUniqueID(this));
    }


    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.activity_settings_main_menu_imgbtn) {
            Context context = v.getContext();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        } else if (v.getId() == R.id.activity_settings_save_imgbtn) {
            String saved = settingsDBHelper.getStringValueByArgument("saved");
            if (saved.equals("null") || saved.equals("false")) {
                saveChanges();
                TabletRegistrationTask savingTask = new TabletRegistrationTask(
                        this, this, contractId, imei, androidId,
                        city, street, house, apartment);
                savingTask.execute();
            }
        } else if (v.getId() == R.id.activity_settings_getproductsupdate_btn) {
            ChangesDownloadTask changesDownloadTask = new ChangesDownloadTask(this);
            changesDownloadTask.execute();
        } else if (v.getId() == R.id.activity_settings_clear_btn) {
            v.getContext().deleteDatabase("CATEGORIES");
            v.getContext().deleteDatabase("PRODUCTS");
            v.getContext().deleteDatabase("SETTINGS");
            v.getContext().deleteDatabase("FAVORITES");

            deleteRecursive(v.getContext().getFilesDir());

            Toast.makeText(getApplicationContext(),"Удаление выполнено успешно!",Toast.LENGTH_SHORT).show();
            v.getContext().startActivity(new Intent(v.getContext(), MainActivity.class));
        }
    }

    private void deleteRecursive(File folder) {
        if (folder.isDirectory()) {
            for (File child : folder.listFiles())
                deleteRecursive(child);
        }
        folder.delete();
    }

    private String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {e.printStackTrace();Log.e("SETTINGS","getMacAddrExc: " + e);}
        return "02:00:00:00:00:00";
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private String getImeiAddr() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                return telephonyManager.getDeviceId();
            } else {
                return "00:00";
            }
        } catch (Exception e) {e.printStackTrace(); Log.e("SETTINGS","getMacAddrExc: " + e);}
        return "Error while get IMEI";
    }

    private String getDeviceUniqueID(Activity activity){
        @SuppressLint("HardwareIds")
        String device_unique_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }

    private void saveChanges(){
        if (contractIdET.getText().toString().matches("[\\d]+")){
            try{
                contractId = contractIdET.getText().toString().replaceAll("\\s+", "");
                imei = getImeiAddr();
                androidId = getDeviceUniqueID(this);
                city = cityET.getText().toString().replaceAll("\\s+", "");
                street = streetET.getText().toString().replaceAll("\\s+", "");
                house = houseET.getText().toString().replaceAll("\\s+", "");
                apartment = apartmentET.getText().toString().replaceAll("\\s+", "");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SETTINGS", "saveChangesExc: " + e);
            }
        } else {
            Toast.makeText(getApplicationContext(),"Номер договора введён не верно!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void checkRegistration() {
        String[] values = settingsDBHelper.getAllValues();
        if (values != null) {
            contractIdET.setText(values[2]);
            contractIdET.setFocusable(false);
            cityET.setText(values[5]);
            cityET.setFocusable(false);
            streetET.setText(values[6]);
            streetET.setFocusable(false);
            houseET.setText(values[7]);
            houseET.setFocusable(false);
            apartmentET.setText(values[8]);
            apartmentET.setFocusable(false);
            saveButton.setClickable(false);
            saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A8A8A8")));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {
            contractIdET.setText("");
            contractIdET.setFocusable(true);
            cityET.setText("");
            cityET.setFocusable(true);
            streetET.setText("");
            streetET.setFocusable(true);
            houseET.setText("");
            houseET.setFocusable(true);
            apartmentET.setText("");
            apartmentET.setFocusable(true);
            saveButton.setClickable(true);
            saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6495ED")));
        }
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}