package com.adamanta.kioskapp.settings.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.adamanta.kioskapp.products.utils.ProductsCategoriesDbHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductsItemsUpdateTask extends AsyncTask<String, Integer, Void> {
    private final String TAG = this.getClass().getSimpleName();
    private ProductsCategoriesDbHelper productsCategoriesDbHelperHeper;

    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String responseText = "";

    // получаем ссылку на MainActivity
    public void link(Activity act, Context context) {
        this.activity = act;
        this.context = context;
    }
    // обнуляем ссылку
    public void unLink() {
        this.activity = null;
        this.context = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (context == null) {
            Log.e(TAG, "Context == NULL" );
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            String url = "http://94.50.162.187:45000/product";

            final MediaType JSON = MediaType.get("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                Log.e(TAG, "doInBackground Request= " + request);
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                responseText = Objects.requireNonNull(response.body()).string();
            }

        } catch (Exception e) {e.printStackTrace();Log.e(TAG,"Exc: " + e);}

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.e(TAG, "OnProgressUpdate LastResponse= " + responseText);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Log.e(TAG, "OnPostExecute Response= " + responseText);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.e(TAG, "onCanceled Error");
    }
}
