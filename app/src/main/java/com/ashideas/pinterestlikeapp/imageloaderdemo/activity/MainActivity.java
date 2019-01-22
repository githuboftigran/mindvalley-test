package com.ashideas.pinterestlikeapp.imageloaderdemo.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.ashideas.pinterestlikeapp.R;
import com.ashideas.pinterestlikeapp.databinding.ActivityMainBinding;
import com.ashideas.pinterestlikeapp.imageloaderdemo.adapter.PinsAdapter;
import com.ashideas.pinterestlikeapp.imageloaderdemo.model.PinterestPin;
import com.ashideas.pinterestlikeapp.imageloaderdemo.viewmodel.PinListViewModel;
import com.ashideas.pinterestlikeapp.misc.AbstractObservableListChangeCallback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableList;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        PinListViewModel viewModel = ViewModelProviders.of(this).get(PinListViewModel.class);
        SwipeRefreshLayout swipeRefresh = findViewById(R.id.pinsSwipeRefresh);
        swipeRefresh.setOnRefreshListener(viewModel::refresh);

        initRecyclerView(viewModel);

        binding.setViewmodel(viewModel);
        viewModel.refresh();
    }

    private void initRecyclerView(PinListViewModel viewModel) {
        RecyclerView pinsRecycler = findViewById(R.id.pinsRecycler);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), getResources().getInteger(R.integer.pins_columns));
        pinsRecycler.setLayoutManager(layoutManager);
        int gap = getResources().getDimensionPixelSize(R.dimen.list_gap);
        pinsRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = gap;
                outRect.top = gap;
                outRect.left = gap;
                outRect.right = gap;
            }
        });

        PinsAdapter pinsAdapter = new PinsAdapter(this);
        pinsRecycler.setAdapter(pinsAdapter);

        viewModel.getPins().addOnListChangedCallback(new AbstractObservableListChangeCallback<PinterestPin>() {
            @Override
            public void onItemRangeInserted(ObservableList<PinterestPin> sender, int positionStart, int itemCount) {
                pinsAdapter.insertPins(sender.subList(positionStart, positionStart + itemCount), positionStart);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<PinterestPin> sender, int positionStart, int itemCount) {
                pinsAdapter.removePins(positionStart, itemCount);
            }
        });

        pinsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    if (totalItemCount == lastVisibleItem + 1 && !viewModel.getLoadingMore().get()) {
                        viewModel.loadMore();
                    }
                }
            }
        });
    }
}