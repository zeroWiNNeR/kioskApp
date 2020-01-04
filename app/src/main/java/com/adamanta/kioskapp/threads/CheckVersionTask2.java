package com.adamanta.kioskapp.threads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.adamanta.kioskapp.settings.utils.SettingsDBHelper;

public class CheckVersionTask2 extends AsyncTask<String, Integer, Void> {

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
    private String appVersion = "";
    private String priceVersion = "";
    private String catalogVersion = "";
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
            Log.e(TAG, "Context == NULL" );
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            Log.e(TAG,"doInBackground");
            try {
//                    String[] reboot = new String[] { "su", "-c", "reboot" };
                String[] reboot = new String[] { "su", "-c", "sh /sdcard/Retail/scripts/launch.sh" };
                //-c will cause the next argument to be treated as a command
                Process process = Runtime.getRuntime().exec(reboot);
                process.waitFor();  //wait for the native process to finish executing.
            } catch (Exception e) {
                Toast.makeText(context," Device not rooted.\n Could not reboot...",Toast.LENGTH_SHORT).show();
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
//        try {
//            String[] reboot = new String[] { "su", "-c", "reboot" };
//            //-c will cause the next argument to be treated as a command
//            Process process = Runtime.getRuntime().exec(reboot);
//            process.waitFor();  //wait for the native process to finish executing.
//        } catch (Exception e) {
//            Toast.makeText(context," Device not rooted.\n Could not reboot...",Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.e(TAG, "onCanceled Error");
    }
}
