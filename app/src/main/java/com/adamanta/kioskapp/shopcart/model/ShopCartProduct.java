package com.adamanta.kioskapp.shopcart.model;

import com.adamanta.kioskapp.product.model.CategoryAndProduct;

import java.math.BigDecimal;

public class ShopCartProduct extends CategoryAndProduct {

    private long id;

    private Long parentCategory;

    private int position;

    private boolean isEnable;

    private String name;

    private String fullName;

    private Long article;

    private Long barcode;

    private String weight;

    private BigDecimal minSize;

    private BigDecimal sizeStep;

    private BigDecimal pricePerSizeStep;

    private BigDecimal weightPerSizeStep;

    private BigDecimal maxSize;

    private BigDecimal allCount;

    private String sizeType;

    private BigDecimal stockQuantity;

    private String manufacturer;

    private String description;

    private String composition;

    private String prevComposition;

    private String prevCompositionDate;

    private String prevPrevComposition;

    private String prevPrevCompositionDate;

    private String information;

    private String imagesInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Long parentCategory) {
        this.parentCategory = parentCategory;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(boolean enable) {
        isEnable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getArticle() {
        return article;
    }

    public void setArticle(Long article) {
        this.article = article;
    }

    public Long getBarcode() {
        return barcode;
    }

    public void setBarcode(Long barcode) {
        this.barcode = barcode;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public BigDecimal getMinSize() {
        return minSize;
    }

    public void setMinSize(BigDecimal minSize) {
        this.minSize = minSize;
    }

    public BigDecimal getSizeStep() {
        return sizeStep;
    }

    public void setSizeStep(BigDecimal sizeStep) {
        this.sizeStep = sizeStep;
    }

    public BigDecimal getPricePerSizeStep() {
        return pricePerSizeStep;
    }

    public void setPricePerSizeStep(BigDecimal pricePerSizeStep) {
        this.pricePerSizeStep = pricePerSizeStep;
    }

    public BigDecimal getWeightPerSizeStep() {
        return weightPerSizeStep;
    }

    public void setWeightPerSizeStep(BigDecimal weightPerSizeStep) {
        this.weightPerSizeStep = weightPerSizeStep;
    }

    public BigDecimal getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(BigDecimal maxSize) {
        this.maxSize = maxSize;
    }

    public BigDecimal getAllCount() {
        return allCount;
    }

    public void setAllCount(BigDecimal allCount) {
        this.allCount = allCount;
    }

    public String getSizeType() {
        return sizeType;
    }

    public void setSizeType(String sizeType) {
        this.sizeType = sizeType;
    }

    public BigDecimal getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public String getPrevComposition() {
        return prevComposition;
    }

    public void setPrevComposition(String prevComposition) {
        this.prevComposition = prevComposition;
    }

    public String getPrevCompositionDate() {
        return prevCompositionDate;
    }

    public void setPrevCompositionDate(String prevCompositionDate) {
        this.prevCompositionDate = prevCompositionDate;
    }

    public String getPrevPrevComposition() {
        return prevPrevComposition;
    }

    public void setPrevPrevComposition(String prevPrevComposition) {
        this.prevPrevComposition = prevPrevComposition;
    }

    public String getPrevPrevCompositionDate() {
        return prevPrevCompositionDate;
    }

    public void setPrevPrevCompositionDate(String prevPrevCompositionDate) {
        this.prevPrevCompositionDate = prevPrevCompositionDate;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getImagesInfo() {
        return imagesInfo;
    }

    public void setImagesInfo(String imagesInfo) {
        this.imagesInfo = imagesInfo;
    }

}
