package com.adamanta.kioskapp.product.utils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public final class Utils {
    private static final String TAG = "Utils";

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight){
        //read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //figure out how much to scale down by
        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > destWidth){
            if(srcWidth > srcHeight)
                inSampleSize = Math.round(srcHeight / destHeight);
            else
                inSampleSize = Math.round(srcWidth / destWidth);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }


    public static String formatFloatToString (float f, int symbAfterPoint) {
        DecimalFormat df = new DecimalFormat("###.0");
        String[] sArr = df.format(f).split(",");
        String s = sArr[0];
        s = s + "." + sArr[1].substring(0, symbAfterPoint);

        return s;
    }


    public static int getAmountAtStorage(String vendorcode) {
        int AmountAtStorage = 9;
        try {
            File sdFileCount = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "Retail/ProductsActivity/", "колич");
            BufferedReader br = new BufferedReader(new FileReader(sdFileCount), 100);
            String str;
            while ((str = br.readLine()) != null) {
                if (str.contains(vendorcode) && str.contains("не")){
                    AmountAtStorage = 0;
                    break;
                }else if(str.contains(vendorcode) && str.contains("ма")){
                    AmountAtStorage = 1;
                    break;
                }else if(str.contains(vendorcode) && str.contains("мн")){
                    AmountAtStorage = 2;
                    break;
                }
            }
            br.close();
        }
        catch(IOException e){e.printStackTrace();Log.e(TAG,"Exception= "+e);}

        return AmountAtStorage;
    }


    public static int getNumOfLinesInFile(File f) {
        int countLines = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f), 100);
            while (br.readLine() != null) {
                countLines++;
            }
            br.close();
        }
        catch (IOException e) { Log.e(TAG,"IOException= " + e); }

        return countLines;
    }


    public static void writeStringToFile (File f, String s, boolean append) {
        try {
            FileWriter fw = new FileWriter(f, append);
            fw.write(s);
            fw.close();
        }
        catch (IOException e) { Log.e(TAG,"IOException= " + e); }
    }

    public static void writeStringsToFile (File f, String[] sArr, boolean append) {
        int countLines = getNumOfLinesInFile(f);
        try {
            FileWriter fw = new FileWriter(f, append);
            for (int i=0; i<countLines; i++) {
                if(i==countLines-1) {
                    fw.write(sArr[sArr.length-1]);
                } else {
                    fw.write(sArr[i] + "\n");
                }
            }
            fw.close();
        }
        catch (IOException e) { Log.e(TAG,"IOException= " + e); }
    }

}
