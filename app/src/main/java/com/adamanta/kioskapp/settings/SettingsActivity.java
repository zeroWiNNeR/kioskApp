package com.adamanta.kioskapp.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CpuUsageInfo;
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

import org.json.JSONObject;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.adamanta.kioskapp.MainActivity;
import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.settings.utils.SettingsDBHelper;
import com.adamanta.kioskapp.threads.ServerPollingTask;

public class SettingsActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    SettingsDBHelper mDatabaseHelper;
    ServerPollingTask serverPollingTask;
    private EditText contractIdET, cityET, streetET, houseET, apartmentET;
    private ImageButton saveButton;
    private String imei = "";
    private String androidId = "";
    private String contractId = "";
    private String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        contractIdET = findViewById(R.id.contractid_et);
        cityET = findViewById(R.id.city_et);
        streetET = findViewById(R.id.street_et);
        houseET = findViewById(R.id.house_et);
        apartmentET = findViewById(R.id.apartment_et);
        saveButton = findViewById(R.id.SaveButton);

        mDatabaseHelper = new SettingsDBHelper(this);

        checkChanges();

        Log.e(TAG, "mac: " + getMacAddr());
        Log.e(TAG, "imei: " + getImeiAddr());
        Log.e(TAG, "androidId: " + getDeviceUniqueID(this));

        serverPollingTask = (ServerPollingTask) getLastNonConfigurationInstance();
        if (serverPollingTask == null) {
            serverPollingTask = new ServerPollingTask();
            serverPollingTask.link(this, SettingsActivity.this);
            serverPollingTask.execute();
        }
    }

    //***************************** Buttons *********************************
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.MainMenuButton:
                Context context = v.getContext();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                break;

            case R.id.SaveButton:
                Cursor data = mDatabaseHelper.getValue("saved");
                if (data==null || data.getCount()==0) {
                    saveChanges();
                    SavingTask savingTask = new SavingTask(this, this, contractId, imei, androidId, address);
                    savingTask.execute();
                }
                if (!data.isClosed()){
                    data.close();
                }
                break;

            default:
                break;
        }
    }
    //***************************** Buttons *********************************

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
        } catch (Exception e) {e.printStackTrace();Log.e(TAG,"getMacAddrExc: " + e);}
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
        } catch (Exception e) {e.printStackTrace(); Log.e(TAG,"getMacAddrExc: " + e);}
        return "Error";
    }

    private String getDeviceUniqueID(Activity activity){
        @SuppressLint("HardwareIds")
        String device_unique_id = Settings.Secure.
                getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }

    private void saveChanges(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (contractIdET.getText().toString().matches("[\\d]+")){
            try{
                contractId = contractIdET.getText().toString().replaceAll("\\s+", "");
                imei = getImeiAddr();
                androidId = getDeviceUniqueID(this);
                address = cityET.getText().toString().replaceAll("\\s+", "") + "," +
                        streetET.getText().toString().replaceAll("\\s+", "") + "," +
                        houseET.getText().toString().replaceAll("\\s+", "") + "," +
                        apartmentET.getText().toString().replaceAll("\\s+", "");
            } catch (Exception e) { e.printStackTrace(); Log.e(TAG, "saveChangesExc: " + e); }
        } else {
            Toast.makeText(getApplicationContext(),"Номер договора введён не верно!",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkChanges(){
        try{
            Cursor data = mDatabaseHelper.getRowsFromCOL3();
            if (data!=null && data.getCount() > 0){
                if(data.moveToFirst()){
                    int i=0;
                    while(data.moveToNext()){
                        i++;
                        if(i==2)
                            contractIdET.setText(data.getString(0));
                        if(i==3)
                            cityET.setText(data.getString(0));
                        if(i==4)
                            streetET.setText(data.getString(0));
                        if(i==5)
                            houseET.setText(data.getString(0));
                        if(i==6)
                            apartmentET.setText(data.getString(0));
                    }
                }
                data.close();

                contractIdET.setFocusable(false);
                cityET.setFocusable(false);
                streetET.setFocusable(false);
                houseET.setFocusable(false);
                apartmentET.setFocusable(false);
                saveButton.setClickable(false);
                saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A8A8A8")));

            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                contractIdET.setFocusable(true);
                cityET.setFocusable(true);
                streetET.setFocusable(true);
                houseET.setFocusable(true);
                apartmentET.setFocusable(true);
                saveButton.setClickable(true);
                saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6495ED")));
            }
        }
        catch (Exception e) {e.printStackTrace();Log.e(TAG,"checkChangesExc: " + e);}
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        serverPollingTask.unLink();
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverPollingTask.unLink();
        serverPollingTask = null;
    }

    @SuppressLint("StaticFieldLeak")
    private class SavingTask extends AsyncTask<String, String, String> {
        Activity activity;
        Context context;
        private ProgressDialog dialog;
        private String contractId = "";
        private String imei = "";
        private String androidId = "";
        private String responseText = "";
        private String address = "";
        private String dbId = "";

        SavingTask(Activity act, Context context, String contractId, String imei, String androidID, String address) {
            this.activity = act;
            this.context = context;
            this.dialog = new ProgressDialog(act);
            this.contractId = contractId;
            this.imei = imei;
            this.androidId = androidID;
            this.address = address;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Регистрация...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            try {
                final MediaType JSON = MediaType.get("application/json; charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("contractId", contractId);
                jsonObject.put("imei", imei);
                jsonObject.put("androidId", androidId);
                jsonObject.put("address", address);
                String json = String.valueOf(jsonObject);

                RequestBody body = RequestBody.Companion.create(json, JSON);

                String url = "http://94.50.162.187:45000/tablet";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    responseText = response.body().string();
                }

            } catch (Exception e) { e.printStackTrace(); Log.e(TAG, "Excp= " + e); }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            Log.e(TAG, "RESPONSE= " + responseText);

            if (responseText.contains("Error") || responseText.equals("")) {
                Toast.makeText(getApplicationContext(),"Ошибка регистрации",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),"Планшет зарегистрирован",Toast.LENGTH_SHORT).show();
                dbId = responseText;

                mDatabaseHelper.addData("saved", "true");
                mDatabaseHelper.addData("dbId", dbId);
                mDatabaseHelper.addData("contractId",
                        contractIdET.getText().toString().replaceAll("\\s+", ""));
                mDatabaseHelper.addData("city",
                        cityET.getText().toString().replaceAll("\\s+", ""));
                mDatabaseHelper.addData("street",
                        streetET.getText().toString().replaceAll("\\s+", ""));
                mDatabaseHelper.addData("house",
                        houseET.getText().toString().replaceAll("\\s+", ""));
                mDatabaseHelper.addData("apartment",
                        apartmentET.getText().toString().replaceAll("\\s+", ""));
                mDatabaseHelper.addData("imei", imei);
                mDatabaseHelper.addData("androidId", androidId);

                contractIdET.setFocusable(false);
                cityET.setFocusable(false);
                streetET.setFocusable(false);
                houseET.setFocusable(false);
                apartmentET.setFocusable(false);
                saveButton.setClickable(false);
                saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A8A8A8")));
            }

            serverPollingTask = (ServerPollingTask) getLastNonConfigurationInstance();
            if (serverPollingTask == null) {
                serverPollingTask = new ServerPollingTask();
                serverPollingTask.link(activity, context);
                serverPollingTask.execute();
            }

        }
    }

}