package com.adamanta.kioskapp.settings;
import android.os.Environment;

public class AppSettings {
    private static String getAppPath = Environment.getExternalStorageDirectory().getAbsolutePath();


    private String DIR_SD = "Retail/ProductsActivity/категории";
    public String getDIR_SD() {
        return DIR_SD;
    }
    public void setDIR_SD(String DIR_SD) {
        this.DIR_SD = DIR_SD;
    }


}
