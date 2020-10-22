package com.adamanta.kioskapp.products.fragments.productImagesFragment;

import java.io.File;

public class ProductImage {

    private File image;
    private int positionInList;

    public ProductImage(File image, int positionInList) {
        this.image = image;
        this.positionInList = positionInList;
    }

    public void setImage(File image) { this.image = image; }

    public void setPositionInList(int positionInList) { this.positionInList = positionInList; }


    public File getImage() { return this.image; }

    public int getPositionInList() { return this.positionInList; }

}
