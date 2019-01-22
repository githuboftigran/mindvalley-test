package com.ashideas.pinterestlikeapp.imageloaderdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ashideas.pinterestlikeapp.PinterestLikeApplication;
import com.ashideas.pinterestlikeapp.databinding.ItemPinterestPinBinding;
import com.ashideas.pinterestlikeapp.imageloaderdemo.model.PinterestPin;
import com.ashideas.pinterestlikeapp.imageloaderdemo.viewmodel.PinViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PinsAdapter extends RecyclerView.Adapter<PinsAdapter.PinViewHolder> {

    private LayoutInflater inflater;
    private List<PinterestPin> pins;

    public PinsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        pins = new ArrayList<>();
    }

    public void insertPins(List<PinterestPin> pins, int start) {
        this.pins.addAll(start, pins);
        notifyItemRangeInserted(start, pins.size());
    }

    public void removePins(int start, int count) {
        pins.subList(start, start + count).clear();
        notifyItemRangeRemoved(start, count);
    }

    @NonNull
    @Override
    public PinViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int index) {
        ItemPinterestPinBinding binding = ItemPinterestPinBinding.inflate(inflater, viewGroup, false);
        return new PinViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PinViewHolder pinViewHolder, int index) {
        pinViewHolder.viewModel.setPin(pins.get(index));
    }

    @Override
    public int getItemCount() {
        return pins.size();
    }

    public class PinViewHolder extends RecyclerView.ViewHolder {

        private PinViewModel viewModel;

        public PinViewHolder(ItemPinterestPinBinding binding) {
            super(binding.getRoot());
            viewModel = new PinViewModel(PinterestLikeApplication.getInstance());
            binding.setViewmodel(viewModel);
        }
    }
}
