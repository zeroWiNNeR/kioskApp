package com.adamanta.kioskapp.products.models;

public class CategoriesList {

    private int Number;
    private String Name;
    private String ButtonColor;
    private String ButtonTextColor;

    public CategoriesList(){
    }

    public CategoriesList(int number, String name, String buttonColor, String buttonTextColor) {
        Number = number;
        Name = name;
        ButtonColor = buttonColor;
        ButtonTextColor = buttonTextColor;

    }

    // Setters

    public void setNumber(int number){ this.Number = number; }

    public void setName(String name) {
        this.Name = name;
    }

    public void setButtonColor(String buttonColor) {
        this.ButtonColor = buttonColor;
    }

    public void setButtonTextColor(String buttonTextColor) {
        this.ButtonTextColor = buttonTextColor;
    }

    // Getters

    public int getNumber() { return Number; }

    public String getName() {
        return Name;
    }

    public String getButtonColor() {
        return ButtonColor;
    }

    public String getButtonTextColor() {
        return ButtonTextColor;
    }

    // Others

    public CategoriesList copy(){
        CategoriesList copy = new CategoriesList();
        copy.setNumber(Number);
        copy.setName(Name);
        copy.setButtonColor(ButtonColor);
        copy.setButtonTextColor(ButtonTextColor);
        return copy;
    }
}