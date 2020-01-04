package com.adamanta.kioskapp.favorites;

public class FavoritesList {

    private String Name;
    private String Uri;

    public FavoritesList(){
    }

    public FavoritesList (String name, String uri){
        Name = name;
        Uri = uri;
    }

    // Setters

    public void setName(String name){
        this.Name = name;
    }

    public void setUri(String uri) { this.Uri = uri; }

    // Getters

    public String getName(){
        return Name;
    }

    public String getUri(){
        return Uri;
    }

    //Others

    public FavoritesList copy(){
        FavoritesList copy = new FavoritesList();
        copy.setName(Name);
        copy.setUri(Uri);
        return copy;
    }
}
