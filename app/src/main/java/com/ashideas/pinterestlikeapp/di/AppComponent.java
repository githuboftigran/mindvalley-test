package com.ashideas.pinterestlikeapp.di;

import com.ashideas.pinterestlikeapp.imageloaderdemo.viewmodel.PinListViewModel;
import com.ashideas.pinterestlikeapp.imageloaderdemo.viewmodel.PinViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component (modules = {HttpModule.class})
public interface AppComponent {
    void inject(PinListViewModel viewModel);
    void inject(PinViewModel viewModel);
}
