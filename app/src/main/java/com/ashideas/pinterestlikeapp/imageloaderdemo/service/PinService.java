package com.ashideas.pinterestlikeapp.imageloaderdemo.service;

import com.ashideas.httplibrary.client.HttpClient;
import com.ashideas.httplibrary.client.HttpError;
import com.ashideas.httplibrary.request.HttpGetRequest;
import com.ashideas.httplibrary.response.HttpResponse;
import com.ashideas.httplibrary.response.JSONArrayCallback;
import com.ashideas.pinterestlikeapp.imageloaderdemo.model.PinterestPin;
import com.ashideas.pinterestlikeapp.misc.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PinService {

    private HttpClient client;
    @Inject
    public PinService(HttpClient client) {
        this.client = client;
    }

    public void loadPins(PinsCallback callback, int page, int limit) {

        //TODO pagination should be added
        //TODO This api doesn't support it yet, so just fetch same content every time
        HttpGetRequest jsonRequest = new HttpGetRequest(Constants.API_URL);

        client.requestAsync(jsonRequest, new JSONArrayCallback() {
            @Override
            public void onSuccess(HttpResponse<JSONArray> response) {
                try {
                    JSONArray json = response.getData();
                    List<PinterestPin> pins = new ArrayList<>();
                    for (int i = 0; i < json.length(); i++) {
                        pins.add(new PinterestPin(json.getJSONObject(i)));
                    }
                    if (callback != null) {
                        callback.onSuccess(pins);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(HttpError error) {
                if (callback != null) {
                    callback.onError();
                }
            }
        });
    }

    public interface PinsCallback {
        void onSuccess(List<PinterestPin> pins);
        void onError();
    }
}
