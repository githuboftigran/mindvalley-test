package com.ashideas.pinterestlikeapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class BindingAdapters {

    @BindingAdapter("android:background")
    public static void setBackground(View view, String color) {
        view.setBackgroundColor(Color.parseColor(color));
    }

    @BindingAdapter("refreshing")
    public static void setRefreshing(SwipeRefreshLayout layout, boolean refreshing) {
        layout.setRefreshing(refreshing);
    }

    @BindingAdapter("src")
    public static void setSrc(ImageView image, Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }
}
