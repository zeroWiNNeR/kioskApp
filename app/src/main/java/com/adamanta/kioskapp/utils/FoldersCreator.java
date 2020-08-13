package com.adamanta.kioskapp.utils;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FoldersCreator {

    public static void createFolders(Context context) {

        List<String> paths = new ArrayList<>();
        paths.add(context.getFilesDir()+"/images");
        paths.add(context.getFilesDir()+"/images/products");

        for (String path : paths) {
            File file = new File(path);
            if (!file.exists())
                file.mkdir();
        }

    }
}
