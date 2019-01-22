package com.ashideas.pinterestlikeapp.imageloaderdemo.model;

public class MultiSizeImage {
    private String small;
    private String medium;
    private String large;

    public MultiSizeImage(String small, String medium, String large) {
        this.small = small;
        this.medium = medium;
        this.large = large;
    }

    public String getSmall() {
        return small;
    }

    public String getMedium() {
        return medium;
    }

    public String getLarge() {
        return large;
    }
}
