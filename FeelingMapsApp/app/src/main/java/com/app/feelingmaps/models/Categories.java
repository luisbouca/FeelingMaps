package com.app.feelingmaps.models;

public class Categories {
    private boolean selected;
    private String category;

    public Categories(String category) {
        this.selected = false;
        this.category = category;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getCategory() {
        return category;
    }
    public void select() {
        selected = !selected;
    }
}
