package com.ashideas.pinterestlikeapp.imageloaderdemo.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PinterestPin {

    private String id;
    private String color;
    private int likes;
    private String user;
    private MultiSizeImage userImage;
    private MultiSizeImage imageUrl;

    public PinterestPin(JSONObject json) {
        try {
            this.id = json.getString("id");
            this.color = json.getString("color");
            this.likes = json.getInt("likes");
            this.user = json.getJSONObject("user").getString("username");
            JSONObject profileImageJson = json.getJSONObject("user").getJSONObject("profile_image");
            String profSmall = profileImageJson.getString("small");
            String profMedium = profileImageJson.getString("medium");
            String profLarge = profileImageJson.getString("large");
            this.userImage = new MultiSizeImage(profSmall, profMedium, profLarge);
            JSONObject imageUrlJson = json.getJSONObject("urls");
            String imageSmall = imageUrlJson.getString("small");
            String imageMedium = imageUrlJson.getString("regular");
            String imageLarge = imageUrlJson.getString("full");
            imageUrl = new MultiSizeImage(imageSmall, imageMedium, imageLarge);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public int getLikes() {
        return likes;
    }

    public String getUser() {
        return user;
    }

    public MultiSizeImage getUserImage() {
        return userImage;
    }

    public MultiSizeImage getImageUrl() {
        return imageUrl;
    }
}
