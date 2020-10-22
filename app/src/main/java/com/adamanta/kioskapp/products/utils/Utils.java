package com.adamanta.kioskapp.products.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.DecimalFormat;

public final class Utils {

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

}
