package com.adamanta.kioskapp.shopcart;

public class CartList{

    private int Number;
    private String Name;
    private int Vendorcode;
    private float Price;
    private float Amount;
    private String AmountMeasure;
    private int AmountAll;
    private int AmountAtStorage;
    private boolean IsCountInt;

    public CartList(){
    }

    public CartList(int number, String name, int vendorcode, float price,
                    float amount, String amountMeasure, int amountAll, int amountAtStorage){
        Number = number;
        Name = name;
        Vendorcode = vendorcode;
        Price = price;
        Amount = amount;
        AmountMeasure = amountMeasure;
        AmountAll = amountAll;
        AmountAtStorage = amountAtStorage;
    }

    // Setters

    public void setNumber(int number){
        this.Number = number;
    }

    public void setName(String name){
        this.Name = name;
    }

    public void setVendorcode(int vendorcode) { this.Vendorcode = vendorcode;}

    public void setPrice(float price){
        this.Price = price;
    }

    public void setAmount(float amount){
        this.Amount = amount;
    }

    public void setAmountMeasure(String amountMeasure) { this.AmountMeasure = amountMeasure; }

    public void setAmountAll(int amountAll){
        this.AmountAll = amountAll;
    }

    public void setAmountAtStorage(int amountAtStorage){
        this.AmountAtStorage = amountAtStorage;
    }

    public void setIsCountInt(boolean isCountInt) { this.IsCountInt = isCountInt; }

    // Getters

    public int getNumber(){
        return Number;
    }

    public String getName(){
        return Name;
    }

    public int getVendorcode() { return Vendorcode; }

    public float getPrice(){
        return Price;
    }

    public float getAmount(){
        return Amount;
    }

    public String getAmountMeasure(){
        return AmountMeasure;
    }

    public int getAmountAll(){
        return AmountAll;
    }

    public int getAmountAtStorage(){
        return AmountAtStorage;
    }

    public boolean getIsCountInt() { return IsCountInt; }

    //Others

    public CartList copy(){
        CartList copy = new CartList();
        copy.setNumber(Number);
        copy.setName(Name);
        copy.setVendorcode(Vendorcode);
        copy.setPrice(Price);
        copy.setAmount(Amount);
        copy.setAmountMeasure(AmountMeasure);
        copy.setAmountAll(AmountAll);
        copy.setAmountAtStorage(AmountAtStorage);
        return copy;
    }

}
