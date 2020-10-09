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


    public static void addProductToCartFile(boolean isCountInt, String productName, String vendorcode,
                                            String priceProduct, String countProduct,
                                            String amountMeasure, int amountProductAll) {
        File sdFileCart = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + "Retail/", "корзина");
        try {
            int AmountAtStorage = getAmountAtStorage(vendorcode);
            int numOfLines = getNumOfLinesInFile(sdFileCart);
            if (isCountInt){
                float priceProductFloat = 0;
                try{
                    priceProductFloat=Float.parseFloat(priceProduct.substring(0, priceProduct.length()-5));
                }catch(NumberFormatException e){e.printStackTrace();Log.e(TAG,"NumExc= "+e);}
                //каждая строка файла - отдельный элемент в массиве
                String [] sArr = new String[numOfLines];
                BufferedReader br2 = new BufferedReader(new FileReader(sdFileCart), 100);
                for (int i=0; i<numOfLines; i++){ sArr[i] = br2.readLine(); }
                br2.close();

                if (sArr.length == 1) {
                    if (sArr[0].equals(" ")) {
                        String finstring = productName + ";" + vendorcode + ";"
                                + priceProductFloat + ";" + countProduct + ";"
                                + amountMeasure + ";" + amountProductAll + ";"
                                + AmountAtStorage;
                        writeStringToFile(sdFileCart, finstring, false);
                    }
                    else{
                        if (sArr[0].contains(vendorcode)){
                            String[] sArrBuf = sArr[0].split(";");
                            int bufInt = Integer.parseInt(sArrBuf[3]) + Integer.parseInt(countProduct);
                            int bufint = Integer.parseInt(sArrBuf[5]) + amountProductAll;
                            String finstring = sArrBuf[0] + ";" + sArrBuf[1] + ";"
                                    + sArrBuf[2] + ";" + bufInt + ";"
                                    + amountMeasure + ";"+ bufint + ";" + sArrBuf[6];
                            writeStringToFile(sdFileCart, finstring, false);
                        }
                        else{
                            String finstring = productName + ";" + vendorcode + ";"
                                    + priceProductFloat + ";" + countProduct + ";"
                                    + amountMeasure + ";" + amountProductAll + ";"
                                    + AmountAtStorage;
                            writeStringToFile(sdFileCart, "\n"+finstring, true);
                        }
                    }
                }
                else if (sArr.length > 1) {
                    int num = 0;
                    boolean is = false;
                    for(int i=0; i<sArr.length; i++){
                        if(sArr[i].contains(vendorcode)){
                            num = i;
                            is = true;
                        }
                    }
                    if (is){
                        String [] sArrBuf = sArr[num].split(";");
                        int bufInt = Integer.parseInt(sArrBuf[3]) + Integer.parseInt(countProduct);
                        int bufint = Integer.parseInt(sArrBuf[5]) + amountProductAll;
                        String finstring = sArrBuf[0] + ";" + sArrBuf[1] + ";"
                                + sArrBuf[2] + ";" + bufInt + ";"
                                + amountMeasure + ";" + bufint + ";"
                                + sArrBuf[6];
                        sArr[num]=finstring;
                        writeStringsToFile(sdFileCart, sArr, false);
                    }
                    else {
                        String finstring = productName + ";" + vendorcode + ";"
                                + priceProductFloat + ";" + countProduct + ";"
                                + amountMeasure + ";" + amountProductAll + ";"
                                + AmountAtStorage;
                        writeStringToFile(sdFileCart, "\n"+finstring, true);
                    }
                }
            }
            else if (!isCountInt) {
                float   priceProductFloat = 0,
                        countProductFloat = 0;
                try {
                    priceProductFloat = Float.parseFloat(priceProduct.substring(0, priceProduct.length()-5));
                    countProductFloat = Float.parseFloat(countProduct);
                }catch(NumberFormatException e){e.printStackTrace();Log.e(TAG,"NumExc= "+e);}
                //каждая строка - отдельный элемент в массиве
                String [] sArr = new String[numOfLines];
                BufferedReader br2 = new BufferedReader(new FileReader(sdFileCart), 100);
                for (int i=0; i<numOfLines; i++){ sArr[i] = br2.readLine(); }
                br2.close();

                if (sArr.length == 1) {
                    if (sArr[0].equals(" ")) {
                        //Log.e(TAG, "в файле 1 строка c пробелом");
                        String finstring = productName + ";" + vendorcode + ";"
                                + priceProductFloat + ";" + countProductFloat + ";"
                                + amountMeasure + ";" + amountProductAll + ";" + AmountAtStorage;
                        writeStringToFile(sdFileCart, finstring, false);
                    } else {
                        //Log.e(TAG, "в файле 1 строка c товаром");
                        if (sArr[0].contains(vendorcode)) {
                            //Log.e(TAG, "добавлен такой же товар");
                            String[] sArrBuf = sArr[0].split(";");
                            float buffloat = Float.parseFloat(sArrBuf[3]) + countProductFloat;
                            int bufint = Integer.parseInt(sArrBuf[5]) + amountProductAll;
                            String finstring = sArrBuf[0] + ";" + sArrBuf[1] + ";"
                                    + sArrBuf[2] + ";" + buffloat + ";"
                                    + amountMeasure + ";" + bufint + ";" + sArrBuf[5];
                            writeStringToFile(sdFileCart, finstring, false);
                        } else {
                            //Log.e(TAG, "добавлен другой товар");
                            String finstring = productName + ";" + vendorcode + ";"
                                    + priceProductFloat + ";" + countProductFloat + ";"
                                    + amountMeasure + ";" + amountProductAll + ";" + AmountAtStorage;
                            writeStringToFile(sdFileCart, "\n"+finstring, true);
                        }
                    }
                }
                else if (sArr.length > 1) {
                    int num = 0;
                    boolean is = false;
                    for (int i = 0; i < sArr.length; i++) {
                        if (sArr[i].contains(vendorcode)) {
                            num = i;
                            is = true;
                        }
                    }
                    if (is) {
                        //Log.e(TAG, "Строка " + num + " содержит артикул " + vendorcode);
                        String[] sArrBuf = sArr[num].split(";");
                        float buffloat = Float.parseFloat(sArrBuf[3]) + countProductFloat;
                        int bufint = Integer.parseInt(sArrBuf[5]) + amountProductAll;
                        String finstring = sArrBuf[0] + ";" + sArrBuf[1] + ";"
                                + sArrBuf[2] + ";" + buffloat + ";"
                                + amountMeasure + ";" + bufint + ";" + sArrBuf[6];
                        sArr[num] = finstring;
                        writeStringsToFile(sdFileCart, sArr, false);
                    } else {
                        //Log.e(TAG, "Файл не содержит строку с артикулом: " + vendorcode);
                        String finstring = productName + ";" + vendorcode + ";"
                                + priceProductFloat + ";" + countProductFloat + ";"
                                + amountMeasure + ";" + amountProductAll + ";"
                                + AmountAtStorage;
                        writeStringToFile(sdFileCart, "\n"+finstring, true);
                    }
                }
            }
        }
        catch(IOException e){e.printStackTrace();Log.e(TAG,"brException="+e);}
    }

    public static void deleteProductFromCartFile (int position){
        try{
            File sdFileCart = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "Retail/", "корзина");
            int numOfLinesInFile = getNumOfLinesInFile(sdFileCart);
            if (numOfLinesInFile == 1){
                Utils.writeStringToFile(sdFileCart, " ", false);
            }
            else {
                if (position == 0){
                    String str;
                    int sArrIndex = 0;
                    String[] sArr = new String[numOfLinesInFile-1];
                    BufferedReader br = new BufferedReader(new FileReader(sdFileCart), 100);
                    br.readLine();
                    while ((str = br.readLine()) != null) { sArr[sArrIndex] = str; sArrIndex++; }
                    br.close();
                    FileWriter fw = new FileWriter(sdFileCart, false);
                    for (int i=0; i<sArr.length-1; i++){ fw.write(sArr[i] + "\n"); }
                    fw.write(sArr[sArr.length-1]);
                    fw.close();
                }
                else if (position == numOfLinesInFile-1){
                    String[] sArr = new String[numOfLinesInFile-1];
                    BufferedReader br = new BufferedReader(new FileReader(sdFileCart), 100);
                    for (int i=0; i<numOfLinesInFile-1; i++) { sArr[i] = br.readLine(); }
                    br.close();
                    FileWriter fw = new FileWriter(sdFileCart, false);
                    for (int i=0; i<sArr.length-1; i++){ fw.write(sArr[i] + "\n"); }
                    fw.write(sArr[sArr.length-1]);
                    fw.close();
                }
                else {
                    BufferedReader br = new BufferedReader(new FileReader(sdFileCart), 100);
                    String[] sArr = new String[numOfLinesInFile-1];
                    int sArrIndex = 0;
                    int i =0;
                    for (sArrIndex = 0; sArrIndex<position; sArrIndex++){
                        sArr[sArrIndex] = br.readLine();
                        i = sArrIndex;
                    }
                    br.readLine();
                    for (sArrIndex = i+1; sArrIndex<numOfLinesInFile-1; sArrIndex++) {
                        sArr[sArrIndex] = br.readLine();
                    }
                    br.close();
                    FileWriter fw = new FileWriter(sdFileCart, false);
                    for (int k=0; k<sArr.length-1; k++){ fw.write(sArr[k] + "\n"); }
                    fw.write(sArr[sArr.length-1]);
                    fw.close();
                }
            }
        }
        catch(IOException e) { Log.e(TAG, "IOExc= " + e); }
    }


    public static boolean checkInFavorites(String productName) {
        boolean isContain = false;
        try{
            File sdFileFavorites = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "Retail/", "избранное");
            BufferedReader br = new BufferedReader(new FileReader(sdFileFavorites), 100);
            String str;

            while ((str = br.readLine()) != null) {
                if (str.contains(productName)){
                    isContain = true;
                }
            }
            br.close();
        }
        catch(IOException e) { Log.e(TAG, "IOExc= " + e); }

        return isContain;
    }

    public static void addToFavorites(String productName) {
        File sdFileFavorites = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + "Retail/", "избранное");
        try {
            BufferedReader br = new BufferedReader(new FileReader(sdFileFavorites), 100);
            if (br.readLine().length() < 3) {
                FileWriter fw = new FileWriter(sdFileFavorites, false);
                fw.write(productName);
                fw.close();
            }
            else {
                writeStringToFile(sdFileFavorites, "\n"+productName, true);
            }
            br.close();
        }
        catch(IOException e) { Log.e(TAG, "IOExc= " + e); }
    }

    public static void deleteFromFavorites(String productName) {
        File sdFileFavorites = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + "Retail/", "избранное");
        try {
            int numOfLinesInFile = getNumOfLinesInFile(sdFileFavorites);
            int position = 0;
            BufferedReader brr = new BufferedReader(new FileReader(sdFileFavorites), 100);
            for (int i=0; i<numOfLinesInFile; i++){
                if (brr.readLine().contains(productName)) {
                    position = i;
                }
            }
            brr.close();
            if (numOfLinesInFile == 1){
                Utils.writeStringToFile(sdFileFavorites, " ", false);
            }
            else {
                String[] sArr = new String[numOfLinesInFile-1];
                BufferedReader br = new BufferedReader(new FileReader(sdFileFavorites), 100);
                if (position == 0){
                    String str;
                    int sArrIndex = 0;
                    br.readLine();
                    while ((str = br.readLine()) != null) { sArr[sArrIndex] = str; sArrIndex++; }
                    br.close();
                    FileWriter fw = new FileWriter(sdFileFavorites, false);
                    for (int i=0; i<sArr.length-1; i++){ fw.write(sArr[i] + "\n"); }
                    fw.write(sArr[sArr.length-1]);
                    fw.close();
                }
                else if (position == numOfLinesInFile-1){
                    for (int i=0; i<numOfLinesInFile-1; i++) { sArr[i] = br.readLine(); }
                    br.close();
                    FileWriter fw = new FileWriter(sdFileFavorites, false);
                    for (int i=0; i<sArr.length-1; i++){ fw.write(sArr[i] + "\n"); }
                    fw.write(sArr[sArr.length-1]);
                    fw.close();
                }
                else {
                    int sArrIndex = 0;
                    int i =0;
                    for (sArrIndex = 0; sArrIndex<position; sArrIndex++){
                        sArr[sArrIndex] = br.readLine();
                        i = sArrIndex;
                    }
                    br.readLine();
                    for (sArrIndex = i+1; sArrIndex<numOfLinesInFile-1; sArrIndex++) {
                        sArr[sArrIndex] = br.readLine();
                    }
                    br.close();
                    FileWriter fw = new FileWriter(sdFileFavorites, false);
                    for (int k=0; k<sArr.length-1; k++){ fw.write(sArr[k] + "\n"); }
                    fw.write(sArr[sArr.length-1]);
                    fw.close();
                }
            }
        }
        catch(IOException e) { Log.e(TAG, "IOExc= " + e); }
    }

    public static void deleteProductFromFavoritesInPosition (int position){
        try{
            File sdFileCart = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "Retail/", "избранное");
            int numOfLinesInFile = getNumOfLinesInFile(sdFileCart);
            if (numOfLinesInFile == 1){
                Utils.writeStringToFile(sdFileCart, " ", false);
            }
            else {
                if (position == 0){
                    String str;
                    int sArrIndex = 0;
                    String[] sArr = new String[numOfLinesInFile-1];
                    BufferedReader br = new BufferedReader(new FileReader(sdFileCart), 100);
                    br.readLine();
                    while ((str = br.readLine()) != null) { sArr[sArrIndex] = str; sArrIndex++; }
                    br.close();
                    FileWriter fw = new FileWriter(sdFileCart, false);
                    for (int i=0; i<sArr.length-1; i++){ fw.write(sArr[i] + "\n"); }
                    fw.write(sArr[sArr.length-1]);
                    fw.close();
                }
                else if (position == numOfLinesInFile-1){
                    String[] sArr = new String[numOfLinesInFile-1];
                    BufferedReader br = new BufferedReader(new FileReader(sdFileCart), 100);
                    for (int i=0; i<numOfLinesInFile-1; i++) { sArr[i] = br.readLine(); }
                    br.close();
                    FileWriter fw = new FileWriter(sdFileCart, false);
                    for (int i=0; i<sArr.length-1; i++){ fw.write(sArr[i] + "\n"); }
                    fw.write(sArr[sArr.length-1]);
                    fw.close();
                }
                else {
                    BufferedReader br = new BufferedReader(new FileReader(sdFileCart), 100);
                    String[] sArr = new String[numOfLinesInFile-1];
                    int sArrIndex = 0;
                    int i =0;
                    for (sArrIndex = 0; sArrIndex<position; sArrIndex++){
                        sArr[sArrIndex] = br.readLine();
                        i = sArrIndex;
                    }
                    br.readLine();
                    for (sArrIndex = i+1; sArrIndex<numOfLinesInFile-1; sArrIndex++) {
                        sArr[sArrIndex] = br.readLine();
                    }
                    br.close();
                    FileWriter fw = new FileWriter(sdFileCart, false);
                    for (int k=0; k<sArr.length-1; k++){ fw.write(sArr[k] + "\n"); }
                    fw.write(sArr[sArr.length-1]);
                    fw.close();
                }
            }
        }
        catch(IOException e) { Log.e(TAG, "IOExc= " + e); }
    }

}
