package com.ashideas.pinterestlikeapp;

import android.app.Application;

import com.ashideas.pinterestlikeapp.di.AppComponent;
import com.ashideas.pinterestlikeapp.di.DaggerAppComponent;

public class PinterestLikeApplication extends Application {

    private static PinterestLikeApplication instance;

    public static PinterestLikeApplication getInstance() {
        return instance;
    }

    private AppComponent appComponent;

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appComponent = DaggerAppComponent.builder().build();
    }
}
