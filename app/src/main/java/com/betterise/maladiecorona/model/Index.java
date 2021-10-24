package com.betterise.maladiecorona.model;

import com.google.gson.annotations.SerializedName;

public class Index {
    @SerializedName("value")
    private String value;

    public Index() {
    }

    public Index(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
