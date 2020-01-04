package com.adamanta.kioskapp.productImagesFragment;

public class ProductImagesList {

    private String Path;
    private String ProductName;
    private int NumberInList;

    public ProductImagesList(){
    }

    public ProductImagesList( String path, String productName, int numberInList) {
        Path = path;
        ProductName = productName;
        NumberInList = numberInList;
    }

    // Setters

    public void setPath(String path) { this.Path = path; }

    public void setProductName(String productName) { this.ProductName = productName; }

    public void setNumberInList (int numberInList) { this.NumberInList = numberInList; }

    // Getters

    public String getPath() { return Path; }

    public String getProductName() { return ProductName; }

    public int getNumberInList() { return NumberInList; }

    // Others

    public ProductImagesList copy() {
        ProductImagesList copy = new ProductImagesList();
        copy.setPath(Path);
        copy.setProductName(ProductName);
        copy.setNumberInList(NumberInList);
        return copy;
    }
}
