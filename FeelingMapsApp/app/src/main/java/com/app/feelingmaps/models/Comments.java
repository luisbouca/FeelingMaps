package com.app.feelingmaps.models;

import java.util.List;

public class Comments {
    private float rating;
    private List<String> categories;
    private String comment;

    public Comments(float rating, List<String> categories, String comment) {
        this.rating = rating;
        this.categories = categories;
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getCategory(int position) {
        return categories.get(position);
    }

    public String getComment() {
        return comment;
    }
}
