package com.adamanta.kioskapp.threads;

import android.content.Context;
import android.util.Log;

import com.adamanta.kioskapp.products.interfaces.UIController;

public class TimeRunner implements Runnable {

    private Context context;
    private long lastPollingTime = System.currentTimeMillis();

    public TimeRunner(Context context) {
        this.context = context;
    }

    // @Override
    public void run() {
        Log.e("ConnectionTestThread", "ConnectionTestThread started");
        while(!Thread.currentThread().isInterrupted()) {
            try {
                long now = System.currentTimeMillis();
                long SERVER_POLLING_INTERVAL = 30000;
                if (now - lastPollingTime > SERVER_POLLING_INTERVAL) {


                    lastPollingTime = now;
                }
                ((UIController) context).updateUI(true);
                Thread.sleep(10000);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch(Exception e) {

            }
        }
    }
}
