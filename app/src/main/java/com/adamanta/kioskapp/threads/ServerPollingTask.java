package com.adamanta.kioskapp.threads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.adamanta.kioskapp.settings.utils.SettingsDBHelper;

public class ServerPollingTask extends AsyncTask<String, Integer, Void> {

    private final String TAG = this.getClass().getSimpleName();
    private SettingsDBHelper mDatabaseHelper;
    static final int restServicePort = 45000;
    //ServerSocket serverSocket;
    //ServerSocketThread serverSocketThread;

    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String dbId = "";
    private String contractId = "";
    private String imei = "";
    private String androidId = "";
    private String responseText = "";
    private boolean stop = false;

    // получаем ссылку на MainActivity
    public void link(Activity act, Context context) {
        this.activity = act;
        this.context = context;
    }
    // обнуляем ссылку
    public void unLink() {
        this.activity = null;
        this.context = null;
        this.stop = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (context == null) {
            Log.e(TAG, "Context error" );
        } else {
            mDatabaseHelper = new SettingsDBHelper(context);
            try{
                Cursor data = mDatabaseHelper.getRowsFromCOL3();
                if (data!=null && data.getCount() > 0){
                    if(data.moveToFirst()){
                        int i=0;
                        while(data.moveToNext()){
                            i++;
                            if(i==1)
                                dbId = data.getString(0);
                            if(i==2)
                                contractId = data.getString(0);
                            if(i==7)
                                imei = data.getString(0);
                            if(i==8)
                                androidId = data.getString(0);
                        }
                    }
                    data.close();
                } else {
                    Log.e(TAG, "DB reading Error");
                }
            } catch (Exception e) {e.printStackTrace();Log.e(TAG,"onPreExc: " + e);}
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        while (!stop) {
            try {
                String url = "http://94.50.162.187:45000/tablet" + "/" + dbId;

                final MediaType JSON = MediaType.get("application/json; charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                String json = String.valueOf(jsonObject);
                jsonObject.put("1", "1");

                RequestBody body = RequestBody.Companion.create(json, JSON);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .patch(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    Log.e(TAG, "doInBackground Request= " + request);
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    responseText = Objects.requireNonNull(response.body()).string();
                }
            } catch (Exception e) {e.printStackTrace();Log.e(TAG,"Exc: " + e);}

            if (responseText.contains("Error") || responseText.contains("Авторизация") || responseText.equals("")) {
                Log.e(TAG, "doInBackground ResponseError= " + responseText);
                stop = true;
            } else {
                publishProgress(1);
            }

            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) { e.printStackTrace();Log.e(TAG,"InteruptExc"+e);}
        }

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
        //Log.e(TAG, "OnPostExecute Response= " + responseText);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.e(TAG, "onCanceled Error");
    }
}
