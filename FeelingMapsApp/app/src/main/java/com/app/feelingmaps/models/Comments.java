package com.app.feelingmaps.models;

import java.util.ArrayList;
import java.util.List;

public class Comments {
    private float rating;
    private List<Categories> categories = new ArrayList<>();
    private String comment;
    private String user;

    public Comments(float rating, List<String> categories, String comment,String user) {
        this.rating = rating;
        for(int i = 0; i<categories.size();i++){
            Categories category = new Categories(categories.get(i));
            this.categories.add(category);
        }
        this.comment = comment;
        this.user = user;
    }

    public float getRating() {
        return rating;
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public String getCategory(int position) {
        return categories.get(position).getCategory();
    }

    public String getComment() {
        return comment;
    }

    public String getUser() {
        return user;
    }
}
