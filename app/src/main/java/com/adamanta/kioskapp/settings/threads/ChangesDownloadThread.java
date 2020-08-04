package com.adamanta.kioskapp.settings.threads;

import android.content.Context;
import android.util.Log;

import com.adamanta.kioskapp.product.model.Change;
import com.adamanta.kioskapp.settings.utils.SettingsDBHelper;
import com.adamanta.kioskapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangesDownloadThread extends Thread {

    private final String TAG = this.getClass().getSimpleName();
    private Context context;

    public ChangesDownloadThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        Log.e(TAG, "ChangesDownloadThread started");
        SettingsDBHelper settingsDBHelper = new SettingsDBHelper(context);
        long lastAppliedChangeId = settingsDBHelper.getLastAppliedChangeId();
        try {
            String url = "http://"+Constants.SERVER_IP+":"+Constants.SERVER_PORT+"/changeapplied"+"?lastAppliedChangeId="+lastAppliedChangeId;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                String responseText = Objects.requireNonNull(response.body()).string();
                JSONObject jsonResponseObject = new JSONObject(responseText);
                String status = jsonResponseObject.getString("status");
                if (status.equals("SUCCESS")) {
                    JSONArray arr = jsonResponseObject.getJSONArray("changes");
                    List<Change> changes = new ArrayList<>();
                    for (int i=0; i<arr.length(); i++) {
                        JSONObject jsonObjectChange = arr.getJSONObject(i);
                        Change change = new Change();
                        if (jsonObjectChange.has("1"))
                            change.setAction(jsonObjectChange.getString("1"));
                        if (jsonObjectChange.has("2"))
                            change.setType(jsonObjectChange.getString("2").charAt(0));
                        if (jsonObjectChange.has("3"))
                            change.setDbid(jsonObjectChange.getLong("3"));
                        if (jsonObjectChange.has("4"))
                            change.setCategory(jsonObjectChange.getLong("4"));
                        if (jsonObjectChange.has("5"))
                            change.setParentCategory(jsonObjectChange.getLong("5"));
                        if (jsonObjectChange.has("6"))
                            change.setPosition(jsonObjectChange.getInt("6"));
                        if (jsonObjectChange.has("7"))
                            change.setIsEnable(jsonObjectChange.getBoolean("7"));
                        if (jsonObjectChange.has("8"))
                            change.setName(jsonObjectChange.getString("8"));
                        if (change.getType()=='p') {
                            if (jsonObjectChange.has("9"))
                                change.setFullName(jsonObjectChange.getString("9"));
                            if (jsonObjectChange.has("10"))
                                change.setArticle(jsonObjectChange.getLong("10"));
                            if (jsonObjectChange.has("11"))
                                change.setBarcode(jsonObjectChange.getLong("11"));
                            if (jsonObjectChange.has("12"))
                                change.setWeight(jsonObjectChange.getString("12"));
                            if (jsonObjectChange.has("13"))
                                change.setMinSize(BigDecimal.valueOf(jsonObjectChange.getDouble("13")));
                            if (jsonObjectChange.has("14"))
                                change.setSizeStep(BigDecimal.valueOf(jsonObjectChange.getDouble("14")));
                            if (jsonObjectChange.has("15"))
                                change.setPricePerSizeStep(BigDecimal.valueOf(jsonObjectChange.getDouble("15")));
                            if (jsonObjectChange.has("16"))
                                change.setWeightPerSizeStep(BigDecimal.valueOf(jsonObjectChange.getDouble("16")));
                            if (jsonObjectChange.has("17"))
                                change.setMaxSize(BigDecimal.valueOf(jsonObjectChange.getDouble("17")));
                            if (jsonObjectChange.has("18"))
                                change.setSizeType(jsonObjectChange.getString("18"));
                            if (jsonObjectChange.has("19"))
                                change.setStockQuantity(BigDecimal.valueOf(jsonObjectChange.getDouble("19")));
                            if (jsonObjectChange.has("20"))
                                change.setManufacturer(jsonObjectChange.getString("20"));
                            if (jsonObjectChange.has("21"))
                                change.setDescription(jsonObjectChange.getString("21"));
                            if (jsonObjectChange.has("22"))
                                change.setComposition(jsonObjectChange.getString("22"));
                            if (jsonObjectChange.has("23"))
                                change.setPrevComposition(jsonObjectChange.getString("23"));
                            if (jsonObjectChange.has("24"))
                                change.setPrevCompositionDate(jsonObjectChange.getString("24"));
                            if (jsonObjectChange.has("25"))
                                change.setPrevPrevComposition(jsonObjectChange.getString("25"));
                            if (jsonObjectChange.has("26"))
                                change.setPrevPrevCompositionDate(jsonObjectChange.getString("26"));
                            if (jsonObjectChange.has("27"))
                                change.setInformation(jsonObjectChange.getString("27"));
                            if (jsonObjectChange.has("28"))
                                change.setImagesInfo(jsonObjectChange.getString("28"));
                        }
                        changes.add(change);
                    }
                    if (lastAppliedChangeId==0L) {
                        settingsDBHelper.setValue("lastAppliedChangeId", String.valueOf(arr.length()));
                    } else {
                        lastAppliedChangeId += arr.length();
                        settingsDBHelper.setValue("lastAppliedChangeId",  String.valueOf(lastAppliedChangeId));
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Ошибка отправки/приема запроса!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Ошибка парсинга JSON");
        }
    }

}
