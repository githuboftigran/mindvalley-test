package com.ashideas.pinterestlikeapp.misc;


import androidx.databinding.ObservableList;

public class AbstractObservableListChangeCallback<T> extends ObservableList.OnListChangedCallback<ObservableList<T>> {

    @Override
    public void onChanged(ObservableList<T> sender) {

    }

    @Override
    public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {

    }

    @Override
    public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {

    }

    @Override
    public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {

    }

    @Override
    public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {

    }
}
