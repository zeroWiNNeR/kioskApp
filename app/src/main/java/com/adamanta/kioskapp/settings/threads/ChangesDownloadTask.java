package com.adamanta.kioskapp.settings.threads;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.adamanta.kioskapp.product.model.Category;
import com.adamanta.kioskapp.product.model.Change;
import com.adamanta.kioskapp.product.model.Product;
import com.adamanta.kioskapp.product.utils.CategoriesDBHelper;
import com.adamanta.kioskapp.product.utils.ProductsDBHelper;
import com.adamanta.kioskapp.settings.ISettingsActivity;
import com.adamanta.kioskapp.settings.utils.SettingsDBHelper;
import com.adamanta.kioskapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangesDownloadTask extends AsyncTask<String, String, String> {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private ProgressDialog dialog;

    public ChangesDownloadTask(Context context) {
        this.context = context;
        this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e(this.getClass().getSimpleName(), "ChangesDownloadThread started");
        dialog = new ProgressDialog(context);
        dialog.setMessage("Обновление списка товаров...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        boolean isUpdated = false;
        while (!isUpdated) {
            try {
                SettingsDBHelper settingsDBHelper = new SettingsDBHelper(context);
                CategoriesDBHelper categoriesDBHelper = new CategoriesDBHelper(context);
                ProductsDBHelper productsDBHelper = new ProductsDBHelper(context);
                long lastAppliedChangeId = settingsDBHelper.getLastAppliedChangeId();
                String url = "http://"+Constants.SERVER_IP+":"+Constants.SERVER_PORT+"/changeapplied"+"?lastAppliedChangeId="+lastAppliedChangeId;

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    String responseText = Objects.requireNonNull(response.body()).string();
                    JSONObject jsonResponseObject = new JSONObject(responseText);
                    String status = jsonResponseObject.getString("status");
                    if (status.equals("SUCCESS")) {
                        JSONArray arr = jsonResponseObject.getJSONArray("changes");
                        List<Change> changes = new ArrayList<>();
                        for (int i=0; i<arr.length(); i++) {
                            JSONObject jsonObjectChange = arr.getJSONObject(i);
                            Change change = new Change();
                            if (jsonObjectChange.has("1"))
                                change.setAction(jsonObjectChange.getString("1"));
                            if (jsonObjectChange.has("2"))
                                change.setType(jsonObjectChange.getString("2").charAt(0));
                            if (jsonObjectChange.has("3"))
                                change.setDbid(jsonObjectChange.getLong("3"));
                            if (jsonObjectChange.has("4"))
                                change.setCategory(jsonObjectChange.getLong("4"));
                            if (jsonObjectChange.has("5"))
                                change.setParentCategory(jsonObjectChange.getLong("5"));
                            if (jsonObjectChange.has("6"))
                                change.setPosition(jsonObjectChange.getInt("6"));
                            if (jsonObjectChange.has("7"))
                                change.setIsEnable(jsonObjectChange.getBoolean("7"));
                            if (jsonObjectChange.has("8"))
                                change.setName(jsonObjectChange.getString("8"));
                            if (change.getType()=='p') {
                                if (jsonObjectChange.has("9"))
                                    change.setFullName(jsonObjectChange.getString("9"));
                                if (jsonObjectChange.has("10"))
                                    change.setArticle(jsonObjectChange.getLong("10"));
                                if (jsonObjectChange.has("11"))
                                    change.setBarcode(jsonObjectChange.getLong("11"));
                                if (jsonObjectChange.has("12"))
                                    change.setWeight(jsonObjectChange.getString("12"));
                                if (jsonObjectChange.has("13"))
                                    change.setMinSize(BigDecimal.valueOf(jsonObjectChange.getDouble("13")));
                                if (jsonObjectChange.has("14"))
                                    change.setSizeStep(BigDecimal.valueOf(jsonObjectChange.getDouble("14")));
                                if (jsonObjectChange.has("15"))
                                    change.setPricePerSizeStep(BigDecimal.valueOf(jsonObjectChange.getDouble("15")));
                                if (jsonObjectChange.has("16"))
                                    change.setWeightPerSizeStep(BigDecimal.valueOf(jsonObjectChange.getDouble("16")));
                                if (jsonObjectChange.has("17"))
                                    change.setMaxSize(BigDecimal.valueOf(jsonObjectChange.getDouble("17")));
                                if (jsonObjectChange.has("18"))
                                    change.setSizeType(jsonObjectChange.getString("18"));
                                if (jsonObjectChange.has("19"))
                                    change.setStockQuantity(BigDecimal.valueOf(jsonObjectChange.getDouble("19")));
                                if (jsonObjectChange.has("20"))
                                    change.setManufacturer(jsonObjectChange.getString("20"));
                                if (jsonObjectChange.has("21"))
                                    change.setDescription(jsonObjectChange.getString("21"));
                                if (jsonObjectChange.has("22"))
                                    change.setComposition(jsonObjectChange.getString("22"));
                                if (jsonObjectChange.has("23"))
                                    change.setPrevComposition(jsonObjectChange.getString("23"));
                                if (jsonObjectChange.has("24"))
                                    change.setPrevCompositionDate(jsonObjectChange.getString("24"));
                                if (jsonObjectChange.has("25"))
                                    change.setPrevPrevComposition(jsonObjectChange.getString("25"));
                                if (jsonObjectChange.has("26"))
                                    change.setPrevPrevCompositionDate(jsonObjectChange.getString("26"));
                                if (jsonObjectChange.has("27"))
                                    change.setInformation(jsonObjectChange.getString("27"));
                                if (jsonObjectChange.has("28"))
                                    change.setImagesInfo(jsonObjectChange.getString("28"));
                            }
                            changes.add(change);
                        }

                        for (Change change : changes) {
                            try {
                                if (change.getAction().equals("add")) {
                                    if (change.getType() == 'c') {
                                        Category category = new Category();
                                        category.setType('c');
                                        category.setCategory(change.getCategory());
                                        category.setParentCategory(change.getParentCategory());
                                        category.setPosition(change.getPosition());
                                        category.setIsEnable(change.getIsEnable());
                                        category.setName(change.getName());
                                        if (!categoriesDBHelper.saveCategory(category)) {
                                            Log.e(this.getClass().getSimpleName(), "Ошибка сохранения категории в БД!");
                                            return null;
                                        }
                                    } else if (change.getType() == 'p') {
                                        Product product = new Product();
                                        product.setType('p');
                                        product.setParentCategory(change.getParentCategory());
                                        product.setPosition(change.getPosition());
                                        product.setIsEnable(change.getIsEnable());
                                        product.setName(change.getName());
                                        product.setFullName(change.getFullName());
                                        product.setArticle(change.getArticle());
                                        product.setBarcode(change.getBarcode());
                                        product.setWeight(change.getWeight());
                                        product.setMinSize(change.getMinSize());
                                        product.setSizeStep(change.getSizeStep());
                                        product.setPricePerSizeStep(change.getPricePerSizeStep());
                                        product.setWeightPerSizeStep(change.getWeightPerSizeStep());
                                        product.setMaxSize(change.getMaxSize());
                                        product.setSizeType(change.getSizeType());
                                        product.setStockQuantity(change.getStockQuantity());
                                        product.setManufacturer(change.getManufacturer());
                                        product.setDescription(change.getDescription());
                                        product.setComposition(change.getComposition());
                                        product.setPrevComposition(change.getPrevComposition());
                                        product.setPrevCompositionDate(change.getPrevCompositionDate());
                                        product.setPrevPrevComposition(change.getPrevPrevComposition());
                                        product.setPrevPrevCompositionDate(change.getPrevPrevCompositionDate());
                                        product.setInformation(change.getInformation());
                                        product.setImagesInfo(change.getImagesInfo());

                                        saveImages(product.getArticle(), product.getImagesInfo());

                                        if (!productsDBHelper.saveProduct(product)) {
                                            Log.e(this.getClass().getSimpleName(), "Ошибка сохранения продукта в БД!");
                                            return null;
                                        }
                                    }
                                } else if (change.getAction().equals("edit")) {
                                    if (change.getDbid() != null && change.getDbid() != 0L) {
                                        if (change.getType() == 'c') {
                                            Category category = categoriesDBHelper.getByCategory(change.getCategory());
                                            if (category == null) {
                                                Log.e(this.getClass().getSimpleName(), "Не удалось получить категорию из БД!");
                                                return null;
                                            }
                                            if (change.getParentCategory() != null) category.setParentCategory(change.getParentCategory());
                                            if (change.getPosition() > 0) category.setPosition(change.getPosition());
                                            category.setIsEnable(change.getIsEnable());
                                            if (change.getName() != null) category.setName(change.getName());

                                            if (!categoriesDBHelper.updateCategory(category))
                                                return null;

                                        } else if (change.getType() == 'p') {
                                            Product product = productsDBHelper.getByArticle(change.getArticle());
                                            if (product == null) {
                                                Log.e(this.getClass().getSimpleName(), "Не удалось получить продукт из БД!");
                                                return null;
                                            }
                                            if (change.getParentCategory() != null) product.setParentCategory(change.getParentCategory());
                                            if (change.getPosition() > 0) product.setPosition(change.getPosition());
                                            product.setIsEnable(change.getIsEnable());
                                            if (change.getName() != null) product.setName(change.getName());
                                            if (change.getFullName() != null) product.setFullName(change.getFullName());
                                            if (change.getArticle() != null) product.setArticle(change.getArticle());
                                            if (change.getBarcode() != null) product.setBarcode(change.getBarcode());
                                            if (change.getWeight() != null) product.setWeight(change.getWeight());
                                            if (change.getMinSize() != null) product.setMinSize(change.getMinSize());
                                            if (change.getSizeStep() != null) product.setSizeStep(change.getSizeStep());
                                            if (change.getPricePerSizeStep() != null) product.setPricePerSizeStep(change.getPricePerSizeStep());
                                            if (change.getWeightPerSizeStep() != null) product.setWeightPerSizeStep(change.getWeightPerSizeStep());
                                            if (change.getMaxSize() != null) product.setMaxSize(change.getMaxSize());
                                            if (change.getSizeType() != null) product.setSizeType(change.getSizeType());
                                            if (change.getStockQuantity() != null) product.setStockQuantity(change.getStockQuantity());
                                            if (change.getManufacturer() != null) product.setManufacturer(change.getManufacturer());
                                            if (change.getDescription() != null) product.setDescription(change.getDescription());
                                            if (change.getComposition() != null) product.setComposition(change.getComposition());
                                            if (change.getPrevComposition() != null) product.setPrevComposition(change.getPrevComposition());
                                            if (change.getPrevCompositionDate() != null) product.setPrevCompositionDate(change.getPrevCompositionDate());
                                            if (change.getPrevPrevComposition() != null) product.setPrevPrevComposition(change.getPrevPrevComposition());
                                            if (change.getPrevPrevCompositionDate() != null) product.setPrevPrevCompositionDate(change.getPrevPrevCompositionDate());
                                            if (change.getInformation() != null) product.setInformation(change.getInformation());
                                            if (change.getImagesInfo() != null) {
                                                if (change.getImagesInfo().split("\\|").length == 1) {
                                                    product.setImagesInfo("|" + change.getImagesInfo().split("\\|")[0]);
                                                } else {
                                                    product.setImagesInfo("|" + change.getImagesInfo().split("\\|")[1]);
                                                    updateImages(product.getArticle(), change.getImagesInfo());
                                                }
                                            }

                                            if (!productsDBHelper.updateProduct(product))
                                                return null;
                                        }
                                    }
                                } else if (change.getAction().equals("del")) {
                                    if (change.getType() == 'c') {
                                        if (!categoriesDBHelper.deleteByCategory(change.getCategory())) {
                                            Log.e(this.getClass().getSimpleName(), "Ошибка при удалении категории из БД!");
                                            return null;
                                        }
                                    } else if (change.getType() == 'p') {
                                        deleteFolderWithImages(change.getArticle());
                                        if (!productsDBHelper.deleteByArticle(change.getArticle())) {
                                            Log.e(this.getClass().getSimpleName(), "Ошибка при удалении продукта из БД!");
                                            return null;
                                        }
                                    }
                                }

                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                                Log.e(this.getClass().getSimpleName(), "Ошибка! Какое-то значение объектов null");
                            }
                        }

                        if (lastAppliedChangeId==0L) {
                            settingsDBHelper.setValue("lastAppliedChangeId", String.valueOf(arr.length()));
                        } else {
                            lastAppliedChangeId += arr.length();
                            settingsDBHelper.setValue("lastAppliedChangeId",  String.valueOf(lastAppliedChangeId));
                        }
                    } else if (status.equals("UPDATED")) {
                        isUpdated = true;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(this.getClass().getSimpleName(), "Ошибка отправки/приема запроса, либо ошибка записи!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(this.getClass().getSimpleName(), "Ошибка парсинга JSON");
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ((ISettingsActivity) context).showToastMessage("Список синхронизирован!");
        dialog.dismiss();
    }

    private void saveImages(Long article, String imagesInfo) {
        String[] imgInfo = imagesInfo.split("\\|");
        String[] images;
        if (imgInfo.length == 1) {
            images = imgInfo[0].split(";");
        } else {
            images = imgInfo[1].split(";");
        }
        for (String image : images) {
            try {
                String imageFilename = image.split("=")[0];
                URL imgURl = new URL("http://"+Constants.SERVER_IP+":"+Constants.SERVER_PORT+"/uploads/images/products/"+article+"/"+imageFilename);
                String articlePath = context.getFilesDir()+"/images/products/"+article;
                File articleFolder = new File(articlePath);
                if (!articleFolder.exists())
                    articleFolder.mkdir();
                String destinationFile = context.getFilesDir()+"/images/products/"+article+"/"+imageFilename;
                InputStream is = imgURl.openStream();
                OutputStream os = new FileOutputStream(destinationFile);
                byte[] b = new byte[128000];
                int length;
                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }
                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(this.getClass().getSimpleName(), "Ошибка при сохранении изображений: \n" + e);
            }
        }
    }

    private void updateImages(Long article, String imagesInfo) {
        String[] imgInfo = imagesInfo.split("\\|");
        String[] imagesChanges;
        if (imgInfo.length > 1) {
            imagesChanges = imgInfo[0].split(";");
            for (String imagesChange : imagesChanges) {
                String[] imagesChangesInfo = imagesChange.split(" ");
                if (imagesChangesInfo[0].equals("add")) {
                    try {
                        URL imgURl = new URL("http://"+Constants.SERVER_IP+":"+Constants.SERVER_PORT+"/uploads/images/products/"+article+"/"+imagesChangesInfo[1]);
                        String articlePath = context.getFilesDir()+"/images/products/"+article;
                        File articleFolder = new File(articlePath);
                        if (!articleFolder.exists())
                            articleFolder.mkdir();
                        String destinationFile = context.getFilesDir()+"/images/products/"+article+"/"+imagesChangesInfo[1];
                        InputStream is = imgURl.openStream();
                        OutputStream os = new FileOutputStream(destinationFile);
                        byte[] b = new byte[128000];
                        int length;
                        while ((length = is.read(b)) != -1) {
                            os.write(b, 0, length);
                        }
                        is.close();
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(this.getClass().getSimpleName(), "Ошибка при сохранении изображений: \n" + e);
                    }
                } else if (imagesChangesInfo[0].equals("edit")) {

                } else if (imagesChangesInfo[0].equals("del")) {
                    String destinationFile = context.getFilesDir()+"/images/products/"+article+"/"+imagesChangesInfo[1];
                    File file = new File(destinationFile);
                    if (file.exists())
                        file.delete();
                }
            }
        }
    }

    private void deleteFolderWithImages(Long article) {
        File directory = new File(context.getFilesDir()+"/images/products/"+article);
        deleteRecursive(directory);
    }

    private void deleteRecursive(File fileOrFolder) {
        if (fileOrFolder.isDirectory())
            for (File child : fileOrFolder.listFiles())
                deleteRecursive(child);
        fileOrFolder.delete();
    }

}
