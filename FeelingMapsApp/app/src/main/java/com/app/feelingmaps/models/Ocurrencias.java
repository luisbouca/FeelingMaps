package com.app.feelingmaps.models;

public class Ocurrencias {
    String key;
    int value;

    public Ocurrencias(String key) {
        this.key = key;
        value=0;
    }

    public String getKey() {
        return key;
    }

    public void addOccurence(){
        value ++;
    }

    public int getValue() {
        return value;
    }
}
