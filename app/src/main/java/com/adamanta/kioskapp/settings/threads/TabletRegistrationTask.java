package com.adamanta.kioskapp.settings.threads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.adamanta.kioskapp.settings.interfaces.ISettingsActivity;
import com.adamanta.kioskapp.settings.utils.SettingsDBHelper;
import com.adamanta.kioskapp.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TabletRegistrationTask extends AsyncTask<String, String, String> {

    private final String TAG = this.getClass().getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private ProgressDialog dialog;
    private String responseText = "";
    private String contractId = "";
    private String imei = "";
    private String androidId = "";
    private String city = "";
    private String street = "";
    private String house = "";
    private String apartment = "";

    public TabletRegistrationTask(Activity activity, Context context,
                                  String contractId, String imei, String androidID,
                                  String city, String street, String house, String apartment
    ) {
        this.context = context;
        this.dialog = new ProgressDialog(activity);
        this.contractId = contractId;
        this.imei = imei;
        this.androidId = androidID;
        this.city = city;
        this.street = street;
        this.house = house;
        this.apartment = apartment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Регистрация...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            final MediaType JSON = MediaType.get("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("contractId", contractId);
            jsonObject.put("imei", imei);
            jsonObject.put("androidId", androidId);
            jsonObject.put("address", city+","+street+","+house+","+apartment);
            String json = String.valueOf(jsonObject);

            String url = "http://"+ Constants.SERVER_IP + ":" + Constants.SERVER_PORT + "/tablet";
            RequestBody body = RequestBody.Companion.create(json, JSON);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                responseText = Objects.requireNonNull(response.body()).string();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Excp= " + e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        dialog.dismiss();

        if (context == null) {
            Log.e(TAG, "Context error");
        } else {
            String status = "";
            String message = "";
            long dbId = 0L;
            try {
                JSONObject jsonResponseObject = new JSONObject(responseText);
                status = jsonResponseObject.getString("status");
                message = jsonResponseObject.getString("message");
                dbId = jsonResponseObject.getLong("dbId");

                if (status.contains("ERROR") || status.equals("")) {
                    Log.e(TAG, "Ошибка регистрации");
                    ((ISettingsActivity) context).showToastMessage(message);
                } else if (status.equals("OK")) {
                    Log.e(TAG, "Планшет зарегистрирован");
                    ((ISettingsActivity) context).showToastMessage(message);

                    SettingsDBHelper settingsDBHelper = new SettingsDBHelper(context);
                    settingsDBHelper.addData("saved", "true");
                    settingsDBHelper.addData("dbId", String.valueOf(dbId));
                    settingsDBHelper.addData("contractId", contractId);
                    settingsDBHelper.addData("imei", imei);
                    settingsDBHelper.addData("androidId", androidId);
                    settingsDBHelper.addData("city", city);
                    settingsDBHelper.addData("street", street);
                    settingsDBHelper.addData("house", house);
                    settingsDBHelper.addData("apartment", apartment);

                    ((ISettingsActivity) context).checkRegistration();
                } else {
                    ((ISettingsActivity) context).showToastMessage("Ошибка регистрации на сервере");
                }

            } catch (JSONException ex) {
                ((ISettingsActivity) context).showToastMessage("Ошибка регистрации на сервере");
            }
        }
    }

}
