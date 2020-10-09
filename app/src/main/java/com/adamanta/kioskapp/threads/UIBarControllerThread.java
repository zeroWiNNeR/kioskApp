package com.adamanta.kioskapp.threads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.adamanta.kioskapp.IMainActivity;
import com.adamanta.kioskapp.settings.utils.SettingsDBHelper;
import com.adamanta.kioskapp.utils.Constants;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UIBarControllerThread extends Thread {

    private final String TAG = this.getClass().getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private long lastPollingTime = 0L;
    private boolean isOnline = false;

    // получаем ссылку на context Activity
    public void link(Context context) {
        this.context = context;
    }
    // обнуляем ссылку
    public void unLink() {
        this.context = null;
    }

    @Override
    public void run() {
        Log.e(TAG, "uiBarControllerThread started");
        while(!Thread.currentThread().isInterrupted()) {
            try {
                long now = System.currentTimeMillis();
                long SERVER_POLLING_INTERVAL = 30000;

                if (now - lastPollingTime > SERVER_POLLING_INTERVAL) {
                    String  dbId = "";
                    if (context == null) {
                        Log.e(TAG, "Context error" );
                    } else {
                        SettingsDBHelper settingsDBHelper = new SettingsDBHelper(context);
                        dbId = settingsDBHelper.getStringValueByArgument("dbId");
                    }

                    if (dbId.equals("null")) {
                        isOnline = false;
                    } else {
                        try {
                            String url = "http://"+ Constants.SERVER_IP+":"+Constants.SERVER_PORT+"/tablet"+"/"+dbId;
                            final MediaType JSON = MediaType.get("application/json; charset=utf-8");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("1", "1");
                            String json = String.valueOf(jsonObject);
                            RequestBody body = RequestBody.Companion.create(json, JSON);
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(url)
                                    .patch(body)
                                    .build();
                            try (Response response = client.newCall(request).execute()) {
                                if (!response.isSuccessful())
                                    throw new IOException("Unexpected code " + response);
                                String responseText = Objects.requireNonNull(response.body()).string();
                                JSONObject jsonResponseObject = new JSONObject(responseText);
                                String status = jsonResponseObject.get("status").toString();

                                isOnline = status.equals("OK");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            isOnline = false;
                            Log.e(TAG,"Exception: " + e);
                        }

                        lastPollingTime = now;
                    }
                }
                ((IMainActivity) context).updateUI(isOnline);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "UIBarRunner stop");
                Thread.currentThread().interrupt();
            }
        }
    }
}
