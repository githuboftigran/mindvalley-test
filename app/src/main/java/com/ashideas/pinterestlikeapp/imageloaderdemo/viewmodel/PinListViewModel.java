package com.ashideas.pinterestlikeapp.imageloaderdemo.viewmodel;

import com.ashideas.pinterestlikeapp.PinterestLikeApplication;
import com.ashideas.pinterestlikeapp.imageloaderdemo.model.PinterestPin;
import com.ashideas.pinterestlikeapp.imageloaderdemo.service.PinService;

import java.util.List;

import javax.inject.Inject;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModel;

public class PinListViewModel extends ViewModel {

    private static final int LIMIT = 10;

    private ObservableList<PinterestPin> pins;
    private ObservableBoolean refreshing;
    private ObservableBoolean loadingMore;
    private int page;

    @Inject
    PinService pinService;

    public PinListViewModel() {
        PinterestLikeApplication.getInstance().getAppComponent().inject(this);
        pins = new ObservableArrayList<>();
        refreshing = new ObservableBoolean();
        loadingMore = new ObservableBoolean();
    }

    public ObservableList<PinterestPin> getPins() {
        return pins;
    }

    public ObservableBoolean getRefreshing() {
        return refreshing;
    }

    public ObservableBoolean getLoadingMore() {
        return loadingMore;
    }

    public void refresh() {
        refreshing.set(true);
        page = 0;
        pinService.loadPins(new PinService.PinsCallback() {
            @Override
            public void onSuccess(List<PinterestPin> newPins) {
                pins.clear();
                pins.addAll(newPins);
                refreshing.set(false);
            }

            @Override
            public void onError() {
                refreshing.set(false);
            }
        }, page, LIMIT);
    }

    public void loadMore() {
        loadingMore.set(true);
        page++;
        pinService.loadPins(new PinService.PinsCallback() {
            @Override
            public void onSuccess(List<PinterestPin> newPins) {
                pins.addAll(newPins);
                loadingMore.set(false);
            }

            @Override
            public void onError() {
                loadingMore.set(false);
            }
        }, page, LIMIT);
    }
}
