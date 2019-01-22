package com.ashideas.pinterestlikeapp.imageloaderdemo.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;

import com.ashideas.httplibrary.client.HttpClient;
import com.ashideas.httplibrary.request.HttpGetRequest;
import com.ashideas.httplibrary.response.BitmapHttpCallback;
import com.ashideas.httplibrary.response.HttpResponse;
import com.ashideas.pinterestlikeapp.PinterestLikeApplication;
import com.ashideas.pinterestlikeapp.imageloaderdemo.model.PinterestPin;
import com.ashideas.pinterestlikeapp.imageloaderdemo.service.PinService;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.AndroidViewModel;

public class PinViewModel extends AndroidViewModel {

    private ObservableField<PinterestPin> pin;
    private ObservableField<Bitmap> image;
    private ObservableField<Bitmap> userImage;

    @Inject
    HttpClient client;

    public PinViewModel(@NonNull Application application) {
        super(application);
        PinterestLikeApplication.getInstance().getAppComponent().inject(this);
        pin = new ObservableField<>();
        image = new ObservableField<>();
        userImage = new ObservableField<>();
    }

    public void setPin(PinterestPin pin) {
        this.pin.set(pin);
        HttpGetRequest imageRequest = new HttpGetRequest(pin.getImageUrl().getMedium());
        image.set(null);
        userImage.set(null);
        client.requestAsync(imageRequest, new BitmapHttpCallback() {
            @Override
            public void onSuccess(HttpResponse<Bitmap> response) {
                image.set(response.getData());
            }
        });

        HttpGetRequest userImageRequest = new HttpGetRequest(pin.getUserImage().getSmall());
        client.requestAsync(userImageRequest, new BitmapHttpCallback() {
            @Override
            public void onSuccess(HttpResponse<Bitmap> response) {
                userImage.set(response.getData());
            }
        });
    }

    public ObservableField<PinterestPin> getPin() {
        return pin;
    }

    public ObservableField<Bitmap> getImage() {
        return image;
    }

    public ObservableField<Bitmap> getUserImage() {
        return image;
    }
}
